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

    private Value lastValue = Value.MYSTERIOUS;
    
    @Override
    public void execute(BlockContext ctx) {
        Value v = expression.evaluate(ctx);
        lastValue = v;
        ctx.getOutput().println(v.getString());
    }

    @Override
    protected String list() {
        return "print " + expression.format();
    }
    
}
