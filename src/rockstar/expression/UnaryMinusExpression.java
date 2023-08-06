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
public class UnaryMinusExpression extends CompoundExpression {

	public UnaryMinusExpression() {
		super(Precedence.UNARY_MINUS);
	}
	
    @Override
    public String getFormat() {
        return "(-%s)";
    }

    @Override
    public int getParameterCount() {
        return 1;
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        Expression expr1 = this.getParameters().get(0);
        Value v1 = expr1.evaluate(ctx);
        return ctx.afterExpression(this, Value.NULL.minus(v1));
    }
    
    @Override
    public void accept(ExpressionVisitor visitor) {
    	visitor.visit(this);
    }

}
