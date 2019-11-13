/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

import java.util.List;

/**
 *
 * @author Gabor
 */
public class ObjectQualifierExpression extends CompoundExpression {

    public VariableReference getObjectRef() {
        return (VariableReference) this.getParameters().get(1);
    }

    public VariableReference getQualifierRef() {
        return (VariableReference) this.getParameters().get(0);
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
        Object oref = (params.size() >= 1) ? getObjectRef() : "<object>";
        Object qref = (params.size() >= 1) ? getQualifierRef() : "<method>";
        return String.format("%s.%s", oref, qref);
    }

    private FunctionCall wrappedFunctionCall = null;
    
    @Override
    public Value evaluate(BlockContext ctx) {
        // evaluate as a parameterless method call
        if (wrappedFunctionCall == null) {
            String name = getQualifierRef().getName();
            wrappedFunctionCall = new FunctionCall(getObjectRef(), name);
        }
        return wrappedFunctionCall.evaluate(ctx);
    }
    
}
