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
        // check if the second parameter is Object
        Value possibleObjValue = getObjectRef().evaluate(ctx);
        if (possibleObjValue != null) {
            if (possibleObjValue.isObject()) {
                // evaluate as a parameterless method call
                if (wrappedFunctionCall == null) {
                    String name = getMethodRef().getName();
                    wrappedFunctionCall = new FunctionCall((VariableReference) getObjectRef(), name);
                }
                return ctx.afterExpression(this, wrappedFunctionCall.evaluate(ctx));
            } else {
                // evaluate as array reference
                Expression baseExpression = getArrayBaseRef();
                Value baseValue = baseExpression.evaluate(ctx);
                // the index is the second parameter, the same as the evaluated possibleObjValue
                Value indexValue = possibleObjValue;
                return ctx.afterExpression(this, baseValue.reference(indexValue));
            }
        } else {
            throw new RockstarRuntimeException("Null referenece: " + getArrayIndexRef());
        }
    }

}
