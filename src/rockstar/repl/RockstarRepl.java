/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.repl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiConsumer;
import rockstar.Rockstar;
import rockstar.parser.Line;
import rockstar.parser.ParseException;
import rockstar.parser.Parser;
import rockstar.parser.StatementFactory;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Utils;
import rockstar.statement.Block;
import rockstar.statement.BlockEnd;
import rockstar.statement.ContinuingBlockStatementI;
import rockstar.statement.FunctionBlock;
import rockstar.statement.Program;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class RockstarRepl {

    Map<String, String> options;

    public RockstarRepl(Map<String, String> options) {
        this.options = options;
    }

    public void repl(List<String> files) {
        BlockContext ctx = new BlockContext(System.in, System.out, System.err, options);

        // pre-run any programs defined as parameter
        files.forEach((filename) -> {
            try {
                Program prg = new Parser(filename).parse();
                prg.execute(ctx);
            } catch (FileNotFoundException ex) {
                System.err.println("File not found: " + filename);
            }
        });
        boolean explain = options.containsKey("-x") || options.containsKey("--explain");

        System.out.println(Rockstar.CLI_HEADER);
        System.out.println(Utils.repeat("-", Rockstar.CLI_HEADER.length()));
        System.out.println("Type 'exit' to quit, 'show' to get more info.");

        Stack<Block> blocks = new Stack();
        blocks.push(new Program("-"));
        try {
            while (true) {
                ctx.getOutput().print("> ");
                String line = ctx.getInput().readLine();

                if (line.equals("exit")) {
                    break;
                }
                if (line.startsWith("show")) {
                    if (line.startsWith("show var")) {
                        System.out.println("Variables:");
                        ctx.getVariables().forEach((name, value) -> {
                            System.out.println(name + " = " + value);
                        });
                    } else if (line.startsWith("show func")) {
                        System.out.println("Functions:");
                        ctx.getFunctions().forEach((String name, FunctionBlock func) -> {
                            System.out.println(name + " taking " + func.getParameterRefs());
                        });
                    } else {
                        System.out.println("Show commands: ");
                        System.out.println("    show var: list global variables and current values");
                        System.out.println("    show func: list functions and parameters");
                    }
                } else {
                    try {
                        // parse the statement
                        Statement stmt = StatementFactory.getStatementFor(new Line(line, "<input>", 1));

                        if (stmt == null) {
                            throw new ParseException("Unknown statement");
                        }

                        // explain if needed
                        if (explain) {
                            System.out.println(stmt.toString());
                        }
                        if (stmt instanceof BlockEnd) {
                            // simple block closing: no need to add it anywhere
                            if (blocks.size() > 1) {
                                stmt = blocks.pop();
                            }
                        } else {
                            // meaningful statements
                            if (stmt instanceof ContinuingBlockStatementI) {
                                // if it sticks to the previous block, close that block, and append it
                                Block finishedBlock = blocks.pop();
                                ((ContinuingBlockStatementI) stmt).appendTo(finishedBlock);
                            }

                            // append statement to current block
                            if (!blocks.isEmpty()) {
                                blocks.peek().addStatement(stmt);
                            } else {
                                throw new ParseException("Statement out of block");
                            }

                            // open a new block if it's a block statement
                            if (stmt instanceof Block) {
                                Block block = (Block) stmt;
                                block.setParent(blocks.peek());
                                blocks.push(block);
                            }

                        }
                        // execute only the top level commands
                        if (blocks.size() == 1) {
                            stmt.execute(ctx);
                        }

                    } catch (ParseException e) {
                        System.out.println("Parse error.");
                    } catch (Throwable t) {
                        System.out.println(t.getClass().getSimpleName() + ": " + t.getMessage());
                    }
                }
            }
        } catch (IOException ex) {
            // end of input: silently ignore
        }

    }

}
