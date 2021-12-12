/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.debugger;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;
import rockstar.expression.Expression;
import rockstar.expression.FunctionCall;
import rockstar.expression.VariableReference;
import rockstar.parser.Line;
import rockstar.runtime.BlockContext;
import rockstar.runtime.BlockContextListener;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;
import rockstar.statement.Block;
import rockstar.statement.Program;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class DebugListener implements BlockContextListener {

    private final Map<String, String> options;
    private Program program;

    private boolean evalMode = false;

    DebugListener(Map<String, String> options) {
        this.options = options;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    public void beforeStatement(BlockContext ctx, Statement stmt) {
        if (!evalMode) {
            atStatement(ctx, stmt);
        }
    }

    @Override
    public void beforeExpression(BlockContext ctx, Expression exp) {
        if (stepIntoExpr && !evalMode) {
            if (exp instanceof FunctionCall) {
                logBeforeExpression(ctx, exp);
            }
        }
    }

    @Override
    public void afterExpression(BlockContext ctx, Expression exp, Value v) {
        if (!evalMode && stepIntoExpr) {
            logExpression(ctx, exp, v);
        }
    }

    private final List<VariableReference> watches = new LinkedList<>();

    private void atStatement(BlockContext ctx, Statement stmt) {
        // expression debug is stopped at new statement
        stepIntoExpr = stepIntoExprSticky;

        if (isStoppingAtStetement(ctx, stmt)) {
            Line l = stmt.getLine();

            System.out.format("Line %d: %s\n", l.getLnum(), l.getOrigLine());
            for (int i = 0; i < watches.size(); i++) {
                VariableReference vref = watches.get(i);
                Value value = ctx.getVariableValue(vref);
                System.out.format("Watch #%d: %s = %s\n", i + 1, vref.toString(), value.toString());
            }

            boolean continueRun = false;
            while (!continueRun) {
                System.out.print(ctx.getName() + "> ");
                try {
                    String line = ctx.getEnv().getInput().readLine();

                    if (line.equals("1") || line.equals("x") || line.equals("X")) {
                        // step into expression
                        if (line.equals("X")) {
                            stepIntoExprSticky = !stepIntoExprSticky;
                            if (stepIntoExprSticky) {
                                stepIntoExpr = true;
                                stepInto = true;
                                continueRun = true;
                            }
                            System.out.println("Expression debug mode " + (stepIntoExprSticky ? "on" : "off"));
                        } else {
                            stepIntoExpr = true;
                            stepInto = true;
                            continueRun = true;
                        }
                    } else if (line.equals("5") || line.equals("")) {
                        // step into
                        stepInto = true;
                        continueRun = true;
                    } else if (line.equals("6")) {
                        // step over
                        // stop at the next statement in the same context
                        stepOvers.push(ctx);
                        continueRun = true;
                    } else if (line.equals("7")) {
                        // step return
                        BlockContext parent = ctx.getParent();
                        if (parent != null) {
                            stepOvers.push(parent);
                        }
                        continueRun = true;
                    } else if (line.equals("8")) {
                        // step run
                        continueRun = true;
                    } else if (line.equals("s")) {
                        BlockContext currCtx = ctx;
                        while (currCtx != null) {
                            String ctxName = currCtx.getName();
                            currCtx.getVariables().forEach(
                                    (name, value) -> {
                                        System.out.format("  %s: %s = %s\n", ctxName, name, value.toString());
                                    });
                            currCtx = currCtx.getParent();
                        }
                    } else if (line.startsWith("s ")) {
                        // show variable
                        String varName = line.substring(2).trim();
                        VariableReference vref = VariableReference.getInstance(varName);
                        Value value = ctx.getVariableValue(vref);
                        System.out.format("%s = %s\n", varName, value.describe());
                    } else if (line.startsWith("w ")) {
                        // add watch
                        String varName = line.substring(2).trim();
                        VariableReference vref = VariableReference.getInstance(varName);
                        watches.add(vref);
                        Value value = ctx.getVariableValue(vref);
                        System.out.format("Watch #%d: %s = %s\n", watches.size(), varName, value.toString());
                    } else if (line.startsWith("wr ")) {
                        // remove watch
                        String varName = line.substring(3).trim();
                        if (varName.startsWith("#")) {
                            try {
                                int idx = Integer.parseInt(varName.substring(1));
                                varName = watches.get(idx - 1).getName();
                            } catch (NumberFormatException nfe) {
                                // fall back to exact string match
                            }
                        }
                        VariableReference vref = VariableReference.getInstance(varName);
                        if (watches.remove(vref)) {
                            System.out.format("Watch removed: %s\n", varName);
                        } else {
                            System.out.println("Unknown watch");
                        }
                    } else if (line.equals("a")) {
                        // show aliases
                        System.out.println("Aliases:");
                        Block b = stmt.getBlock();
                        while (b != null) {
                            Iterator<Map.Entry<List<String>, List<List<String>>>> i = b.getAliasesIterator();
                            i.forEachRemaining(e
                                    -> e.getValue().forEach(v
                                            -> System.out.println(v.stream().collect(Collectors.joining(" ")) + " means " + e.getKey().stream().collect(Collectors.joining(" ")))));
                            b = b.getParent();
                        }
                    } else if (line.startsWith("br")) {
                        // remove breakpoint
                        String lineStr = line.substring(2).trim();
                        Integer lineNum;
                        if (lineStr.equals("")) {
                            // default: current line
                            lineNum = l.getLnum();
                        } else {
                            try {
                                lineNum = Integer.parseInt(lineStr);
                            } catch (NumberFormatException ex) {
                                lineNum = null;
                            }
                        }
                        if (breakpoints.remove(lineNum)) {
                            System.out.format("Breakpoint removed from line %d\n", lineNum);
                        } else {
                            System.out.println("Unknown breakpoint");
                        }
                    } else if (line.startsWith("bl")) {
                        // explain breakpoints
                        for (int i = 0; i < breakpoints.size(); i++) {
                            Integer brLine = breakpoints.get(i);
                            System.out.format("Breakpoint #%d at line %d\n", i + 1, brLine);
                        }
                    } else if (line.startsWith("b")) {
                        // add breakpoint
                        String lineStr = line.substring(1).trim();
                        Integer lineNum;
                        if (lineStr.equals("")) {
                            // default: current line
                            lineNum = l.getLnum();
                        } else {
                            try {
                                lineNum = Integer.parseInt(lineStr);
                            } catch (NumberFormatException ex) {
                                lineNum = null;
                            }
                        }
                        if (lineNum != null) {
                            breakpoints.add(lineNum);
                            System.out.format("Breakpoint added at line %d\n", lineNum);
                        } else {
                            System.out.println("Wrong line number");
                        }
                    } else if (line.startsWith("list")) {
                        // list the program
                        String optionStr = line.substring(4).trim();
                        boolean explain = false; // "-x".equals(optionStr);
                        boolean explainOnly = false; //"-X".equals(optionStr);
                        System.out.println(this.program.listProgram(true, !explainOnly, explain || explainOnly));
                    } else if (line.equals(".")) {
                        // print the current line again
                        System.out.format("Line %d: %s\n", l.getLnum(), l.getOrigLine());
                    } else if (line.startsWith("?")) {
                        // show help
                        String helpCmd = line.substring(1).trim();
                        RockstarDebugger.printDebuggerHelp(helpCmd);
                    } else if (line.equals("exit")) {
                        // exit
                        throw new RockstarRuntimeException("exit commad");
                    } else {
                        System.out.println("Wrong command, use '?' for command help");
                    }
                } catch (IOException ex) {
                    continueRun = true;
                }
            }
        }
    }

    private boolean stepInto = true;
    private boolean stepIntoExpr = false;
    private boolean stepIntoExprSticky = false;
    private final Stack<BlockContext> stepOvers = new Stack<>();
    private final List<Integer> breakpoints = new LinkedList<>();

    private boolean isStoppingAtStetement(BlockContext ctx, Statement stmt) {
        // stop at each statement if stepInto mode
        boolean stop = false;
        if (stepInto) {
            stepInto = false;
            stop = true;
        }
        // stop at the next statement of a specific context
        if (stepOvers.size() > 0 && stepOvers.peek() == ctx) {
            stepOvers.pop();
            stop = true;
        }
        // stop at a line number
        if (breakpoints.contains(stmt.getLine().getLnum())) {
            stop = true;
        }
        return stop;
    }

    private void logExpression(BlockContext ctx, Expression exp, Value v) {
        System.out.format("Evaluated in %s: %s => %s\n", ctx.getName(), exp.format(), v.toString());
    }

    private void logBeforeExpression(BlockContext ctx, Expression exp) {
        System.out.format("Stepping into: %s\n", exp.format());
    }

}
