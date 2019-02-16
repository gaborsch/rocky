/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.expression.ConstantExpression;
import rockstar.expression.PlusExpression;
import rockstar.expression.VariableReference;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockNumber;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class IncrementStatement extends Statement {

    private final VariableReference variable;
    private final int count;
    private PlusExpression plus;

    public IncrementStatement(VariableReference variable, int count) {
        this.variable = variable;
        this.count = count;
    }

    private PlusExpression getPlus() {
        if (plus == null) {
            plus = new PlusExpression();
            plus.addParameter(variable);
            plus.addParameter(new ConstantExpression(count));
        }
        return plus;
    }

    @Override
    public void execute(BlockContext ctx) {
        String varName = variable.getName(ctx);
        Value v = ctx.getVariableValue(varName);
        if (v.isMysterious() || v.isNull()) {
            v = Value.getValue(RockNumber.ZERO);
            // v is set to a numeric value
            ctx.setVariable(varName, v);
        }
        if (v.isNumeric()) {
            // increment by count
            Value value = getPlus().evaluate(ctx);
            ctx.setVariable(varName, value);
            return;
        } else if (v.isBoolean()) {
            // convert to boolean
            v = v.asBoolean();
            if (count % 2 == 1) {
                // negate boolean
                v = v.negate();
            }
            ctx.setVariable(varName, v);
            return;
        }
        throw new RockstarRuntimeException(v.getType() + " ++");
    }
    
    @Override
    protected String list() {
        return variable.format()+ " += " + count;
    }
}
