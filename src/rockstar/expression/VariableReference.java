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
public class VariableReference extends SimpleExpression {

    private final String name;
    private boolean isFunctionName = false;
    private boolean isLastVariable = false;

    private Ref ref = null;

    public VariableReference(String name, boolean isFunctionName, boolean isLastVariable) {
        this.name = name;
        this.isFunctionName = isFunctionName;
        this.isLastVariable = isLastVariable;
    }

    public void addRef(Ref ref) {
        this.ref = ref;
    }

    public String getName(BlockContext ctx) {
        String effectiveName = this.name;
        if (isLastVariable) {
            effectiveName = ctx.getLastVariableName();
        }
        return effectiveName;
    }

    public String getFunctionName() {
        return name;
    }

    public boolean isFunctionName() {
        return isFunctionName;
    }

    public Ref getRef() {
        return ref;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        String effectiveName = this.name;
        if (isLastVariable) {
            effectiveName = ctx.getLastVariableName();
        }
        Value value = ctx.getVariableValue(effectiveName);
        if (value == null) {
            value = Value.MYSTERIOUS;
            ctx.setVariable(effectiveName, value);
        } else if (ref != null) {
            // needs dereference
            if (value.getType() == ExpressionType.LIST_ARRAY
                    && ref.getType() == Ref.Type.LIST) {
                Value indexValue = ref.getExpression().evaluate(ctx);
                value = value.dereference(indexValue);
            } else if (value.getType() == ExpressionType.ASSOC_ARRAY
                    && ref.getType() == Ref.Type.ASSOC_ARRAY) {
                Value indexValue = ref.getExpression().evaluate(ctx);
                value = value.dereference(indexValue);
            }
        }
        return ctx.afterExpression(this, value);
    }

    @Override
    public String format() {
        return isLastVariable ? "<it>" : name;
    }

}
