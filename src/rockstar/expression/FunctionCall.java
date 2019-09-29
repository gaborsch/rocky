package rockstar.expression;

import java.util.ArrayList;
import java.util.List;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;
import rockstar.statement.FunctionBlock;

/**
 *
 * @author Gabor
 */
public class FunctionCall extends CompoundExpression {

    private VariableReference object = null;
    private String name;

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
            name = oqe.getQualifierRef().getFunctionName();
        } else if (expr instanceof VariableReference) {
            name = ((VariableReference) expr).getFunctionName();
        } else {
            throw new RuntimeException("Invalid function name: " + expr);
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
            // method call
            ctx.beforeExpression(object);
            Value objValue = ctx.afterExpression(object, ctx.getVariableValue(object));
            if (objValue.isObject()) {
                // get the object itself
                callContext = objValue.getObject();
                // get the method from the object
                funcBlock = callContext.retrieveFunction(name);
            } else {
                throw new RuntimeException("Invalid method call "+name+" on a "+objValue.getType().name()+" type variable " + object);
            }
        } else {
            // pure function
            funcBlock = ctx.retrieveFunction(name);
        }

        List<Expression> params = getParameters();
        List<Value> values = new ArrayList<>(params.size());
        params.forEach((expr) -> {
            values.add(expr.evaluate(ctx));
        });
        // call the functon
        Value retValue = funcBlock.call(callContext, values);
        // return the return value
        return ctx.afterExpression(this, retValue == null ? Value.NULL : retValue);
    }

}
