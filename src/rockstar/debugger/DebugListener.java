/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.debugger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionParser;
import rockstar.parser.Line;
import rockstar.runtime.BlockContext;
import rockstar.runtime.BlockContextListener;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class DebugListener implements BlockContextListener {

    private final Map<String, String> options;

    private boolean evalMode = false;

    DebugListener(Map<String, String> options) {
        this.options = options;
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
        stepIntoExpr = false;

        if (stopAtStetement(ctx, stmt)) {
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
                    String line = ctx.getInput().readLine();

                    if (line.equals("1") || line.equals("x")) {
                        // step into expression
                        stepIntoExpr = true;
                        stepInto = true;
                        continueRun = true;
                    } else if (line.equals("5") || line.equals("")) {
                        // step into
                        stepInto = true;
                        continueRun = true;
                    } else if (line.equals("6")) {
                        // steo over
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
                                        System.out.format("  @%s: %s = %s\n", ctxName, name, value.toString());
                                    });
                            currCtx = currCtx.getParent();
                        }
                    } else if (line.startsWith("s ")) {
                        // show variable
                        String varName = line.substring(2).trim();
                        VariableReference vref = new VariableReference(varName, false, false);
                        Value value = ctx.getVariableValue(vref);
                        System.out.format("%s = %s\n", varName, value.toString());
                    } else if (line.startsWith("w ")) {
                        // add watch
                        String varName = line.substring(2).trim();
                        VariableReference vref = new VariableReference(varName, false, false);
                        watches.add(vref);
                        Value value = ctx.getVariableValue(vref);
                        System.out.format("Watch #%d: %s = %s\n", watches.size(), varName, value.toString());
                    } else if (line.startsWith("wr ")) {
                        // remove watch
                        String varName = line.substring(3).trim();
                        if (varName.startsWith("#")) {
                            try {
                                int idx = Integer.parseInt(varName.substring(1));
                                varName = watches.get(idx - 1).getName(ctx);
                            } catch (NumberFormatException nfe) {
                                // fall back to exact string match
                            }
                        }
                        VariableReference vref = new VariableReference(varName, false, false);
                        if (watches.remove(vref)) {
                            System.out.format("Watch removed: %s\n", varName);
                        } else {
                            System.out.println("Unknown watch");
                        }
                    } else if (line.startsWith("br")) {
                        // remove breakpoint
                        String lineStr = line.substring(2).trim();
                        Integer lineNum = null;
                        if (lineStr.equals("")) {
                            // default: current line
                            lineNum = l.getLnum();
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
    private boolean stepIntoExpr = true;
    private final Stack<BlockContext> stepOvers = new Stack<>();
    private final List<Integer> breakpoints = new LinkedList<>();

    private boolean stopAtStetement(BlockContext ctx, Statement stmt) {
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
        System.out.format("Evaluated: %s => %s\n", exp.format(), v.toString());
    }

}
