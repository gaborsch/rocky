package rockstar.expression;

import java.util.ArrayList;
import java.util.List;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockObject;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;
import rockstar.statement.FunctionBlock;

/**
 *
 * @author Gabor
 */
public class FunctionCall extends CompoundExpression {

    private VariableReference object = null;
    private String name;

    public FunctionCall() {
        super();
    }

    FunctionCall(VariableReference object, String name) {
        super();
        this.object = object;
        this.name = name;
    }

    @Override
    public int getPrecedence() {
        return 100;
    }

    @Override
    public int getParameterCount() {
        // FunctionCall takes the name and the parameter list
        return 2;
    }

    @Override
    public String getFormat() {
        StringBuilder sb = new StringBuilder();
        if (object != null) {
            sb.append(object).append(".");
        }
        sb.append(name);
        sb.append("(");
        final List<Expression> parameters = getParameters();
        boolean isFirst = true;
        for (int i = 0; i < parameters.size(); i++) {
            if (!isFirst) {
                sb.append(", ");
            }
            sb.append(parameters.get(i));
            isFirst = false;
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public CompoundExpression setupFinished() {
        Expression expr = getParameters().remove(0);
        if (expr instanceof ObjectQualifierExpression) {
            ObjectQualifierExpression oqe = (ObjectQualifierExpression) expr;
            object = oqe.getObjectRef();
            name = oqe.getQualifierRef().getName();
        } else if (expr instanceof VariableReference) {
            name = ((VariableReference) expr).getName();
        } else {
            throw new RockstarRuntimeException("Invalid function name: " + expr);
        }

        Expression paramsExpr = getParameters().remove(0);
        ListExpression paramsListExpr = ListExpression.asListExpression(paramsExpr);
        if (paramsListExpr == null) {
            return null;
        }
        for (Expression paramExpr : paramsListExpr.getParameters()) {
            if (paramExpr instanceof ConstantExpression) {
                addParameter(paramExpr);
            } else if (paramExpr instanceof VariableReference) {
                VariableReference varRef = (VariableReference) paramExpr;
                if (!varRef.isFunctionName()) {
                    addParameter(paramExpr);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
        return this;

    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        FunctionBlock funcBlock = null;
        BlockContext callContext = ctx;

        if (object != null) {
            // method call on an object
            if (VariableReference.isSelfReference(object.getName())) {
                // self object reference?
                funcBlock = callContext.retrieveLocalFunction(name);
                throw new RockstarRuntimeException("self reference");

            } else if (VariableReference.isParentReference(object.getName())) {
                // parent object reference
                // find the caller object context
                BlockContext callerCtx = ctx;
                while (callerCtx != null && !(callerCtx instanceof RockObject)) {
                    callerCtx = callerCtx.getParent();
                }
                if (callerCtx == null) {
                    throw new RockstarRuntimeException("parent reference in a non-object context");
                }
                RockObject callerObj = (RockObject) callerCtx;
                // get the parent object, if exists
                RockObject parentObj = callerObj.getSuperObject();
                if (parentObj != null) {
                    // find the context that contains the function starting the parent
                    callContext = parentObj.getContextForFunction(name);
                    // the call context must be the same object as the caller object
                    if ((callContext != null)
                            && (callContext instanceof RockObject)
                            && (((RockObject) callContext).getObjId() == callerObj.getObjId())) {
                        // get the method from that context
                        funcBlock = callContext.retrieveLocalFunction(name);
                    }
                }
            } else {
                // object reference
                ctx.beforeExpression(object);
                Value objValue = ctx.afterExpression(object, ctx.getVariableValue(object));
                if (objValue.isObject()) {
                    // get the object itself
                    RockObject objContext = objValue.getObject();
                    // find the context that contains the function
                    callContext = objContext.getContextForFunction(name);
                    if (callContext == null) {
                        throw new RockstarRuntimeException("Invalid method call " + name + " on a " + objValue.getType().name() + " type variable " + object);
                    }
                    // get the method from the object
                    funcBlock = callContext.retrieveLocalFunction(name);
                } else {
                    throw new RockstarRuntimeException("Invalid method call " + name + " on a " + objValue.getType().name() + " type variable " + object);
                }
            }
        } else if (VariableReference.isParentReference(name)) {
            // unqualified "parent" function: must be a parent constructor reference
            BlockContext callerCtx = ctx;
            while (callerCtx != null && !(callerCtx instanceof RockObject)) {
                callerCtx = callerCtx.getParent();
            }
            if (callerCtx == null) {
                throw new RockstarRuntimeException("parent constructor call in a non-object context");
            }
            RockObject callerObj = (RockObject) callerCtx;
            // TODO check if it is called in a constructor

            // get the parent object, if exists
            RockObject parentObj = callerObj.getSuperObject();
            if (parentObj != null) {
                // context is the parent object
                callContext = parentObj;
                // get the constructor from the parent context
                funcBlock = parentObj.getConstructor();
            }
        } else {
            // pure function call or unqualified call in an object context?
            BlockContext funcCtx = ctx.getContextForFunction(name);
            if (funcCtx == null) {
                // function not found, or function exists only in subcontexts
                throw new RockstarRuntimeException("Undefined function: " + name);
            }
            // we found the function, now we need to find the overrides, if it is on an object
            if (funcCtx instanceof RockObject) {
                // search the function from the top of the object levels
                funcCtx = ((RockObject) funcCtx).getTopObject().getContextForFunction(name);
            } else {
                // find the containing context by name
                funcCtx = ctx.getContextForFunction(name);
            }
            // retrieve the function code
            funcBlock = funcCtx.retrieveLocalFunction(name);
        }

        Value retValue;
        if (funcBlock != null) {
            List<Expression> params = getParameters();
            List<Value> values = new ArrayList<>(params.size());
            params.forEach((expr) -> values.add(expr.evaluate(ctx)));
            // call the functon
            retValue = funcBlock.call(callContext, values);
        } else {
            if (object == null) {
                throw new RockstarRuntimeException("Undefined function: " + name);
            }
            throw new RockstarRuntimeException("Undefined method: " + name + " on class " + object.getName());
        }
        // return the return value
        return ctx.afterExpression(this, retValue == null ? Value.NULL : retValue);
    }

}
