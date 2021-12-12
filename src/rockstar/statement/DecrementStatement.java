/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.expression.ConstantExpression;
import rockstar.expression.MinusExpression;
import rockstar.expression.VariableReference;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockNumber;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class DecrementStatement extends Statement {

    private final VariableReference variable;
    private final int count;
    private MinusExpression minus;

    public DecrementStatement(VariableReference variable, int count) {
        this.variable = variable;
        this.count = count;
    }

    private MinusExpression getMinus() {
        if (minus == null) {
            minus = new MinusExpression();
            minus.addParameter(variable);
            minus.addParameter(new ConstantExpression(count));
        }
        return minus;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value v = ctx.getVariableValue(variable);

        // default to numeric 0 value
        if (v.isNull()) {
            v = Value.getValue(RockNumber.ZERO());
            // v is set to a numeric value
            ctx.setVariable(variable, v);
        }

        if (v.isNumeric()) {
            // increment by count
            Value value = getMinus().evaluate(ctx);
            ctx.setVariable(variable, value);
            return;
        } else if (v.isBoolean()) {
            // convert to boolean
            v = v.asBoolean();
            if (count % 2 == 1) {
                // negate boolean
                v = v.negate();
            }
            ctx.setVariable(variable, v);
            return;
        }
        throw new RockstarRuntimeException("Cannot decrement " + v.getType());
    }

    @Override
    protected String explain() {
        return variable.format() + " -= " + count;
    }

    @Override
    public String getASTNodeText() {
        return super.getASTNodeText() + (count != 1 ? (" by " + count) : "");
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return ASTValues.of(variable);
    }

}
