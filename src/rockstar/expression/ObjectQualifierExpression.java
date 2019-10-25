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
        return String.format("%s.%s", getObjectRef(), getQualifierRef());
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
