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
public class ReferenceExpression extends CompoundExpression {

    public ReferenceExpression() {
    }

    /**
     * the base expression may contain the value (in case of definition) or the
     * array (in case of evaluation)
     *
     * @return
     */
    public Expression getBaseExpression() {
        return this.getParameters().get(0);
    }

    public Expression getIndexExpression() {
        return this.getParameters().get(1);
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        Expression baseExpression = getBaseExpression();
        Value baseValue = baseExpression.evaluate(ctx);
        Expression indexExpression = getIndexExpression();
        Value indexValue = indexExpression.evaluate(ctx);
        return ctx.afterExpression(this, baseValue.reference(indexValue));
    }

    @Override
    public int getPrecedence() {
        return 50;
    }

    @Override
    public int getParameterCount() {
        return 2;
    }

    @Override
    public String format() {
        return String.format("%s[%s]", getBaseExpression(), getIndexExpression());
    }

    @Override
    public String getFormat() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
