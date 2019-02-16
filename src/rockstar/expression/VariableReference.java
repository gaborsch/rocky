/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class VariableReference extends SimpleExpression {

    private String name;
    private boolean isFunctionName = false;
    private boolean isLastVariable = false;

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

    public VariableReference(String name, boolean isFunctionName, boolean isLastVariable) {
        this.name = name;
        this.isFunctionName = isFunctionName;
        this.isLastVariable = isLastVariable;
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
        }
        return ctx.afterExpression(this, value);
    }

    @Override
    public String format() {
        return isLastVariable ? "<it>" : name;
    }

}
