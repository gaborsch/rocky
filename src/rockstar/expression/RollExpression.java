/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.List;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class RollExpression extends CompoundExpression {

    @Override
    public String getFormat() {
        return "roll(%s)";
    }

    @Override
    public int getPrecedence() {
        return 75;
    }

    @Override
    public int getParameterCount() {
        return 2;
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        Expression expr1 = this.getParameters().get(0);
        if (expr1 instanceof VariableReference) {
            Value arrayValue = expr1.evaluate(ctx);
            if (arrayValue.isArray()) {
                Value value;
                List<Value> list = arrayValue.asListArray();
                if (! list.isEmpty()) {
                    value = list.remove(0);
                } else {
                    value = Value.MYSTERIOUS;
                }
                return value;
            } else {
                throw new RockstarRuntimeException("Invalid array reference: " + expr1);
            }
        } 
        throw new RockstarRuntimeException("Invalid array reference: " + expr1);
    }

}
