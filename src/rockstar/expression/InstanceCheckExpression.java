/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.BlockContext;
import rockstar.runtime.RockObject;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class InstanceCheckExpression extends CompoundExpression {
	
	boolean negated = false;
	
	public InstanceCheckExpression(boolean negated) {
		super(Precedence.INSTANCE_CHECK);
		this.negated = negated;
	}

    public VariableReference getObjectRef() {
        return (VariableReference) this.getParameters().get(0);
    }

    public VariableReference getClassRef() {
        return (VariableReference) this.getParameters().get(1);
    }

    @Override
    public int getParameterCount() {
        return 2;
    }

    @Override
    public String getFormat() {
        return String.format("%s %sinstanceof %s", negated ? "not " : "", getObjectRef(), getClassRef());
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);

        Value objVal = getObjectRef().evaluate(ctx);
        if (objVal.isObject()) {
		    RockObject obj = objVal.getObject();
		    // treat the classname as a literal, check instanceof
		    boolean isInstanceOf = obj.checkInstanceof(getClassRef().getName());
		    return ctx.afterExpression(this, Value.getValue(negated ^ isInstanceOf));
        } else if (objVal.isNative()) {
        	// checking native classes 
		    Value classValue = ctx.getVariableValue(getClassRef());
		    if (classValue.isNative()) {
		    	Class<?> objCls = objVal.getNative().getNativeClass();
		    	Class<?> cls = classValue.getNative().getNativeClass();
		    	boolean isInstanceOf = cls.isAssignableFrom(objCls);
		    	return ctx.afterExpression(this, Value.getValue(negated ^ isInstanceOf));
		    }
        }
        throw new RockstarRuntimeException("Instance check on a non-object value");
    }

}
