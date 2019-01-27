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
public class MinusExpression extends CompoundExpression {

    @Override
    protected String getFormat() {
        return "(%s - %s)";
    }

    @Override
    public int getPrecedence() {
        return 500;
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
                // numeric subtraction
                return new ConstantValue(v1.getNumericValue().minus(v2.getNumericValue()));
            }
        }
        throw new RockstarRuntimeException(v1.getType() + " minus " + v2.getType());
    }

}
