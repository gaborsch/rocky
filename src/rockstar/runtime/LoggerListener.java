/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.Map;
import rockstar.expression.Expression;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class LoggerListener implements BlockContextListener {

    private boolean logStmt = false;
    private boolean logExpr = false;

    public LoggerListener(Map<String, String> options) {
        logExpr = options.containsKey("--exprlog");
        // --exprlog implies --runlog, too
        logStmt = logExpr || options.containsKey("--runlog");
    }

    @Override
    public void beforeStatement(BlockContext ctx, Statement stmt) {
        if (logStmt) {
            System.out.println(stmt.getLine().getOrigLine());
        }
    }

    @Override
    public void beforeExpression(BlockContext ctx, Expression exp) {
    }

    @Override
    public void afterExpression(BlockContext ctx, Expression exp, Value v) {
        if (logExpr) {
            System.out.println("    Eval:" + exp + " = " + v);
        }
    }

}
