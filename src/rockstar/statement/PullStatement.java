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
public class PullStatement extends Statement {

    private final VariableReference variable;
    private final VariableReference targetRef;

    public PullStatement(VariableReference variable, VariableReference targetRef) {
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
                    targetValue = list.remove(list.size()-1);
                } else {
                    targetValue = Value.MYSTERIOUS;
                }
                ctx.setVariable(this.targetRef, targetValue);
            } else {
                throw new RockstarRuntimeException("Pulling from a non-array type: "+array.getType());
            }
        } else {
            throw new RockstarRuntimeException("Pushing into a nonexistent variable: " + variable);
        }
    }


    @Override
    protected String explain() {
        return "pull " + targetRef.format() + " from " + variable.format();
    }
}
