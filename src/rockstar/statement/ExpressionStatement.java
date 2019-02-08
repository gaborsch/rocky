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

    private final Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return super.toString()
                + "\n    EXPR: " + expression;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value v = expression.evaluate(ctx);
    }

        
    @Override
    public String explain(BlockContext ctx) {
        return null;
    }
    
    @Override
    protected String list() {
        return expression.format();
    }
}
