/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class PlusExpression extends CompoundExpression {

	public PlusExpression() {
		super(Precedence.ADDITION);
	}
	
    @Override
    public String getFormat() {
        return "(%s + %s)";
    }

    @Override
    public int getParameterCount() {
        return 2;
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        Expression expr1 = this.getParameters().get(0);
        Expression expr2 = this.getParameters().get(1);
        Value v = expr1.evaluate(ctx);
        if (expr2 instanceof ListExpression) {
            ListExpression list = (ListExpression) expr2;
            for (Expression e : list.getParameters()) {
                Value v2 = e.evaluate(ctx);
                 v = ctx.afterExpression(this, v.plus(v2));
            }
        } else {
            Value v2 = expr2.evaluate(ctx);
            v = ctx.afterExpression(this, v.plus(v2));
        }    
        return v;
    }

}
