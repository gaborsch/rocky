package rockstar.expression;

import java.util.ArrayList;
import java.util.List;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.NativeObject;
import rockstar.runtime.RockObject;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;
import rockstar.statement.ASTValues;
import rockstar.statement.FunctionBlock;

/**
 *
 * @author Gabor
 */
public class FunctionCall extends CompoundExpression {

    private VariableReference object = null;
    private String functionName;

    public FunctionCall() {
        super();
    }

    FunctionCall(VariableReference object, String methodName) {
        super();
        this.object = object;
        this.functionName = methodName;
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
        sb.append(functionName);
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
        if (expr instanceof QualifierExpression) {
            QualifierExpression qe = (QualifierExpression) expr;
            object = (VariableReference) qe.getObjectRef();
            functionName = qe.getMethodRef().getName();
        } else if (expr instanceof VariableReference) {
            functionName = ((VariableReference) expr).getName();
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
                addParameter(paramExpr);
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
        Value retValue = null;
        BlockContext callContext = ctx;

        // parameters
        List<Expression> params = getParameters();
        List<Value> values = new ArrayList<>(params.size());
        params.forEach((expr) -> values.add(expr.evaluate(ctx).asParameter()));
        
        if (object != null) {
            // method call on an object
            if (VariableReference.isSelfReference(object.getName())) {
                // self object reference?
                funcBlock = callContext.retrieveLocalFunction(functionName);
                throw new RockstarRuntimeException("self reference");

            } else if (VariableReference.isParentReference(object.getName())) {
                // parent object reference
                // find the caller object context
                RockObject callerObj = ctx.getThisObjectCtx()
                        .orElseThrow(() -> new RockstarRuntimeException("parent reference in a non-object context"));

                // get the parent object, if exists
                RockObject parentObj = callerObj.getSuperObject();
                if (parentObj != null) {
                    // find the context that contains the function starting the parent
                    callContext = parentObj.getContextForFunction(functionName);
                    // the call context must be the same object as the caller object
                    if ((callContext != null)
                            && (callContext instanceof RockObject)
                            && (((RockObject) callContext).getObjId() == callerObj.getObjId())) {
                        // get the method from that context
                        funcBlock = callContext.retrieveLocalFunction(functionName);
                    }
                } else {
                    throw new RockstarRuntimeException("parent reference in non-inherited class");
                }
            } else {
                // object reference
                ctx.beforeExpression(object);
                Value objValue = ctx.afterExpression(object, ctx.getVariableValue(object));
                if (objValue == null) {
                    throw new RockstarRuntimeException("Object not found: " + object);
                }
                if (objValue.isObject()) {
                    // get the object itself
                    RockObject objContext = objValue.getObject();
                    // find the context that contains the function
                    callContext = objContext.getContextForFunction(functionName);
                    if (callContext == null) {
                        throw new RockstarRuntimeException("Invalid method call " + functionName + " on a " + objValue.getType().name() + " type variable " + object);
                    }
                    // get the method from the object
                    funcBlock = callContext.retrieveLocalFunction(functionName);
                } else if (objValue.isNative()) {
                	NativeObject nativeObject = objValue.getNative();
                	retValue = nativeObject.callMethod(functionName, values);
                } else {
                    throw new RockstarRuntimeException("Invalid method call " + functionName + " on a " + objValue.getType().name() + " type variable " + object);
                }
            }
        } else {
            // simple method call syntax
            if (VariableReference.isParentReference(functionName) && ctx.getThisObjectCtx().isPresent()) {
                // unqualified "parent" function: must be a parent constructor reference

                // find the caller object context
                RockObject callerObj = ctx.getThisObjectCtx()
                        .orElseThrow(() -> new RockstarRuntimeException("parent constructor call in a non-object context"));

                // get the parent object, if exists
                RockObject parentObj = callerObj.getSuperObject();
                // TODO check if it is called in a constructor

                if (parentObj != null) {
                    // context is the parent object
                    callContext = parentObj;
                    // get the constructor from the parent context
                    funcBlock = parentObj.getConstructor(values);
                } else {
                    throw new RockstarRuntimeException("parent constructor reference in non-inherited class");
                }
            } else {
                // pure function call or unqualified call in an object context?
                BlockContext funcCtx = ctx.getContextForFunction(functionName);
                if (funcCtx == null) {
                    // function not found, or function exists only in subcontexts
                    throw new RockstarRuntimeException("Undefined function: " + functionName);
                }
                // we found the function, now we need to find the overrides, if it is on an object
                if (funcCtx instanceof RockObject) {
                    // search the function from the top of the object levels
                    funcCtx = ((RockObject) funcCtx).getTopObject().getContextForFunction(functionName);
                } else {
                    // find the containing context by name
                    funcCtx = ctx.getContextForFunction(functionName);
                }
                // retrieve the function code
                funcBlock = funcCtx.retrieveLocalFunction(functionName);
            }
        }

        if (funcBlock != null) {
            // call the functon
            retValue = funcBlock.call(callContext, values);
        } else if (retValue == null) {
            if (object == null) {
                throw new RockstarRuntimeException("Undefined function: " + functionName);
            }
            throw new RockstarRuntimeException("Undefined method: " + functionName + " on class " + object.getName());
        }
        // return the return value
        return ctx.afterExpression(this, retValue == null ? Value.NULL : retValue);
    }

    @Override
    public String getASTNodeText() {
        return super.getASTNodeText() + (object != null ? " on object" : "") + " " + functionName;
    }

    @Override
    public List<ASTAware> getASTChildren() {
        List<ASTAware> astParams = ASTValues.of(object);
        astParams.addAll(super.getASTChildren());
        return astParams;
    }

}
