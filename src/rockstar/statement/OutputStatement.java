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
public class OutputStatement extends Statement {
    
    private final Expression expression;

    public OutputStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value v = expression.evaluate(ctx);
        ctx.getEnv().getOutput().println(v.asScalar().getString());
    }

    @Override
    protected String explain() {
        return "print " + expression.format();
    }
    
}
