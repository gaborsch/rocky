/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;

/**
 *
 * @author Gabor
 */
public class MultiplyExpression extends CompoundExpression {

    @Override
    protected String getFormat() {
        return "(%s * %s)";
    }

    @Override
    public int getPrecedence() {
        return 400;
    }

    @Override
    public int getParameterCount() {
        return 2;
    }

    @Override
    public ConstantValue evaluate(BlockContext ctx) {
        Expression expr1 = this.getParameters().get(0);
        Expression expr2 = this.getParameters().get(1);
        ConstantValue v1 = expr1.evaluate(ctx);
        ConstantValue v2 = expr2.evaluate(ctx);
        if (v1.isNumeric()) {
            if (v2.isNumeric()) {
                // numeric multiplication
                return new ConstantValue(v1.getNumericValue().multiply(v2.getNumericValue()));
            }
        } else if (v1.isString()) {
            if (v2.isNumeric()) {
                // String repeating
                return new ConstantValue(v1.getStringValue().repeat(v2.getNumericValue().asInt()));
            }
        }
        throw new RockstarRuntimeException(v1.getType() + " times " + v2.getType());
    }

}
