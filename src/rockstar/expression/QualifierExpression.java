/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

import java.util.List;
import rockstar.runtime.RockstarRuntimeException;

/**
 *
 * @author Gabor
 */
public class QualifierExpression extends CompoundExpression {

    private final String token;
    private final boolean isArrayIndexing;

    public QualifierExpression(String token) {
        super();
        this.token = token;
        this.isArrayIndexing = "at".equals(token);
    }

    public boolean isArrayIndexing() {
        return isArrayIndexing;
    }

// Object reference handling 
    public Expression getObjectRef() {
        return this.getParameters().get(1);
    }

    public VariableReference getMethodRef() {
        return (VariableReference) this.getParameters().get(0);
    }

// Array reference handling
    public Expression getArrayBaseRef() {
        return (VariableReference) this.getParameters().get(0);
    }

    public Expression getArrayIndexRef() {
        return this.getParameters().get(1);
    }

    @Override
    public int getPrecedence() {
        return 50;
    }

    @Override
    public int getParameterCount() {
        return 2;
    }

    @Override
    public String getFormat() {
        List<Expression> params = this.getParameters();
        Object qref = (params.size() >= 1) ? params.get(0) : "<method>";
        Object oref = (params.size() >= 2) ? params.get(1) : "<object>";
        return String.format("%s@%s", qref, oref);
    }

    private FunctionCall wrappedFunctionCall = null;

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        if (isArrayIndexing) {
            // evaluate as array reference
            Value baseValue = getArrayBaseRef().evaluate(ctx);
            if (baseValue != null) {
                if (baseValue.isArray()) {
                    // evaluate the index
                    Value indexValue = getArrayIndexRef().evaluate(ctx);
                    return ctx.afterExpression(this, baseValue.reference(indexValue));
                }else {
                    throw new RockstarRuntimeException("Non-Array referenced: " + getArrayBaseRef() + " is " + baseValue.getType());
                }
            } else {
                throw new RockstarRuntimeException("Null reference: " + getArrayBaseRef());
            }
        } else {
            Value objValue = getObjectRef().evaluate(ctx);
            if (objValue != null) {
                if (objValue.isObject()) {
                    // evaluate as a parameterless method call
                    if (wrappedFunctionCall == null) {
                        String name = getMethodRef().getName();
                        wrappedFunctionCall = new FunctionCall((VariableReference) getObjectRef(), name);
                    }
                    return ctx.afterExpression(this, wrappedFunctionCall.evaluate(ctx));
                } else {
                    throw new RockstarRuntimeException("Non-Object referenced: " + getObjectRef() + " is " + objValue.getType());
                }
            } else {
                throw new RockstarRuntimeException("Null reference: " + getObjectRef());
            }
        }
    }
}
