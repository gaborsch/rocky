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

    public VariableReference(String name, boolean isFunctionName, boolean isLastVariable) {
        this.name = name;
        this.isFunctionName = isFunctionName;
        this.isLastVariable = isLastVariable;
    }

    public String getName(BlockContext ctx) {
        VariableReference vref = this;
        if (isLastVariable) {
            vref = ctx.getLastVariableRef();
        }
        return vref.name;
    }

    public String getName() {
        return name;
    }

    public boolean isFunctionName() {
        return isFunctionName;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        VariableReference effectiveVRef = this;
        if (isLastVariable) {
            effectiveVRef = ctx.getLastVariableRef();
        }
        Value value = ctx.getVariableValue(effectiveVRef);

        if (value == null) {
            value = Value.MYSTERIOUS;
            ctx.setVariable(this, value);
        }

        return ctx.afterExpression(this, value);
    }

//    private static Value evaluate(VariableReference vref, BlockContext ctx) {
//        Value value = ctx.getVariableValue(vref);
//        return value;
//    }
    public boolean isLastVariable() {
        return isLastVariable;
    }

    @Override
    public String format() {
        return isLastVariable ? "<it>" : name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof VariableReference) {
            VariableReference o = (VariableReference) obj;
            return name.equals(o.name);
        }
        return false;
    }

}
