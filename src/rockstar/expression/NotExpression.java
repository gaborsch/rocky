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
public class NotExpression extends CompoundExpression {

    public NotExpression(Expression... params) {
        super(Precedence.NEGATION, params);
    }

    @Override
    public String getFormat() {
        return "NOT (%s)";
    }

    @Override
    public int getParameterCount() {
        return 1;
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        Expression expr = this.getParameters().get(0);
        Value v = expr.evaluate(ctx);
        return ctx.afterExpression(this, v.negate());
    }

    @Override
    public String getASTNodeText() {
        return "NOT";
    }
    
    @Override
    public void accept(ExpressionVisitor visitor) {
    	visitor.visit(this);
    }

}
