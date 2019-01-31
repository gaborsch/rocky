/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.expression.Expression;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class ExpressionStatement extends Statement {

    private final Expression expr;

    public ExpressionStatement(Expression expr) {
        this.expr = expr;
    }

    public Expression getExpression() {
        return expr;
    }

    @Override
    public String toString() {
        return super.toString()
                + "\n    EXPR: " + expr;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value v = expr.evaluate(ctx);
    }

}
