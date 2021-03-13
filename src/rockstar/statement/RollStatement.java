/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.expression.VariableReference;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;
import java.util.List;
import rockstar.runtime.RockstarRuntimeException;

/**
 *
 * @author Gabor
 */
public class RollStatement extends Statement {

    private final VariableReference variable;
    private final VariableReference targetRef;

    public RollStatement(VariableReference variable, VariableReference targetRef) {
        this.variable = variable;
        this.targetRef = targetRef;
    }


    @Override
    public void execute(BlockContext ctx) {
        Value array = variable.evaluate(ctx);
        if (this.targetRef != null) {
            if (array.isArray()) {
                Value targetValue;
                List<Value> list = array.asListArray();
                if (! list.isEmpty()) {
                    targetValue = list.remove(0);
                } else {
                    targetValue = Value.MYSTERIOUS;
                }
                ctx.setVariable(this.targetRef, targetValue);
            } else {
                throw new RockstarRuntimeException("Rolling from a non-array type: "+array.getType());
            }
        } else {
            throw new RockstarRuntimeException("Rolling into a nonexistent variable: " + variable);
        }
    }


    @Override
    protected String explain() {
        return "roll " + targetRef.format() + " from " + variable.format();
    }
}
