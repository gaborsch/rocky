/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.repl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import rockstar.Rockstar;
import rockstar.parser.Line;
import rockstar.parser.MultilineReader;
import rockstar.parser.ParseException;
import rockstar.parser.Parser;
import rockstar.parser.StatementFactory;
import rockstar.runtime.BlockStack;
import rockstar.runtime.Environment;
import rockstar.runtime.FileContext;
import rockstar.runtime.Utils;
import rockstar.statement.AliasStatement;
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
        Environment env = Environment.create(System.in, System.out, System.err, options);
        FileContext ctx = new FileContext(env);

        // pre-run any programs defined as parameter
        files.forEach((String filename) -> {
            try {
                Program prg = new Parser(filename, env).parse();
                prg.execute(ctx);
            } catch (FileNotFoundException ex) {
                System.err.println("File not found: " + filename);
            }
        });
        boolean explain = options.containsKey("-x") || options.containsKey("--explain");

        System.out.println(Rockstar.CLI_HEADER);
        System.out.println(Utils.repeat("-", Rockstar.CLI_HEADER.length()));
        System.out.println("Type 'exit' to quit, 'show' to get more info.");

        BlockStack blocks = new BlockStack(env);
        blocks.push(new Program("-"));
        try {
            while (true) {
                ctx.getEnv().getOutput().print("> ");
                String l = ctx.getEnv().getInput().readLine();

                if (l.equals("exit")) {
                    break;
                }
                if (l.startsWith("show")) {
                    if (l.startsWith("show var")) {
                        System.out.println("Variables:");
                        ctx.getVariables().forEach((name, value) -> {
                            System.out.println(name + " = " + value);
                        });
                    } else if (l.startsWith("show func")) {
                        System.out.println("Functions:");
                        ctx.getFunctions().forEach((String name, FunctionBlock func) -> {
                            System.out.println(name + " taking " + func.getParameterRefs());
                        });
                    } else if (l.startsWith("show alias")) {
                        System.out.println("Aliases:");
                        Block b = blocks.peek();
                        while (b != null) {
                            Iterator<Map.Entry<List<String>, List<List<String>>>> i = b.getAliasesIterator();
                            i.forEachRemaining(
                                    e -> e.getValue().forEach(
                                            v -> System.out.println(v.stream().collect(Collectors.joining(" ")) + " means " + e.getKey().stream().collect(Collectors.joining(" ")))));
                            b = b.getParent();
                        }
                    } else {
                        System.out.println("Show commands: ");
                        System.out.println("    show var: list global variables and current values");
                        System.out.println("    show func: list functions and parameters");
                        System.out.println("    show alias: list user-defined aliases");
                    }
                } else {
                    try {
                        MultilineReader rdr = new MultilineReader(new BufferedReader(new StringReader(l)), "-");
                        final Line line = rdr.readLine();
                        if (line == null) {
                        	continue;
                        }

                        // parse the statement
                        Statement stmt = StatementFactory.getStatementFor(line, blocks.peek());

                        if (stmt == null) {
                            throw new ParseException("Unknown statement", line);
                        }
                        if (stmt instanceof AliasStatement) {
                            AliasStatement aliasStmt = (AliasStatement) stmt;
                            blocks.peek().defineAlias(aliasStmt.getKeyword(), aliasStmt.getAlias());
                        }
                        // explain if needed
                        if (explain) {
                            // TODO: AST
                            System.out.println(stmt.toString());
                        }
                        if (stmt instanceof BlockEnd) {
                            // simple block closing: no need to add it anywhere
                            stmt = blocks.removeBlock();
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
                                throw new ParseException("Statement out of block", line);
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
