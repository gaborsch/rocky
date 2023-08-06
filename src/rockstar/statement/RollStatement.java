/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.expression.VariableReference;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class RollStatement extends Statement {

    final VariableReference arrayVariable;
    final VariableReference targetRef;

    public RollStatement(VariableReference arrayVariable, VariableReference targetRef) {
        this.arrayVariable = arrayVariable;
        this.targetRef = targetRef;
    }

    public RollStatement(VariableReference arrayVariable) {
        this.arrayVariable = arrayVariable;
        this.targetRef = null;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value array = arrayVariable.evaluate(ctx);
        if (array != null) {
            if (array.isArray()) {
                Value targetValue;
                List<Value> list = array.asListArray();
                if (!list.isEmpty()) {
                    targetValue = list.remove(0);
                } else {
                    targetValue = Value.MYSTERIOUS;
                }
                if (this.targetRef != null) {
                    ctx.setVariable(this.targetRef, targetValue);
                }
            } else {
                throw new RockstarRuntimeException("Rolling from a non-array type: " + array.getType());
            }
        } else {
            throw new RockstarRuntimeException("Rolling from a nonexistent variable: " + arrayVariable);
        }
    }

    @Override

    public List<ASTAware> getASTChildren() {
        return ASTValues.of(targetRef, arrayVariable);
    }
    
    @Override
    public void accept(StatementVisitor visitor) {
    	visitor.visit(this);
    }
}
