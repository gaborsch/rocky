/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import com.sun.org.apache.xpath.internal.operations.Equals;
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
        VariableReference vref = this;
        if (isLastVariable) {
            vref = ctx.getLastVariableRef();
        }
        return vref.name;
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
        if (ref == null) {
            return name;
        }
        if (ref.getType() == Ref.Type.LIST) {
            return name + " at " + ref.getExpression();
        } else {
            return name + " for " + ref.getExpression();
        }
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        VariableReference effectiveVRef = this;
        if (isLastVariable) {
            effectiveVRef = ctx.getLastVariableRef();
        }
        Value value = evaluate(effectiveVRef, ctx);

        if (value == null) {
            value = Value.MYSTERIOUS;
            ctx.setVariable(this, value);
        }

        return ctx.afterExpression(this, value);
    }

    private static Value evaluate(VariableReference vref, BlockContext ctx) {
        Value value = ctx.getVariableValue(vref);
        Ref ref = vref.getRef();
        if (ref != null) {
            // requires dereference
            if (value.getType() == ExpressionType.LIST_ARRAY
                    && ref.getType() == Ref.Type.LIST) {
                Value indexValue = ref.getExpression().evaluate(ctx);
                value = value.dereference(indexValue);
            } else if (value.getType() == ExpressionType.ASSOC_ARRAY
                    && ref.getType() == Ref.Type.ASSOC_ARRAY) {
                Value indexValue = ref.getExpression().evaluate(ctx);
                value = value.dereference(indexValue);
            } else {
                // should not reach here
                throw new RuntimeException("Unknown reference type");
            }
        }
        return value;
    }

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
            if (name.equals(o.name)) {
                if (ref == null && o.ref == null) {
                    return true;
                }
                if (ref == null || o.ref == null) {
                    return false;
                }
                return ref.equals(o.ref);
            }
        }
        return false;
    }

}
