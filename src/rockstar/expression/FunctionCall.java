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

    private String name;
    
    @Override
    public int getPrecedence() {
        return 200;
    }
     
    @Override
    public int getParameterCount() {
        // FunctionCall takes the name
        return 1;
    }   
    

    @Override
    public String getFormat() {
        StringBuilder sb = new StringBuilder();
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
    public void setupFinished() {
        Expression nameExpr = getParameters().remove(0);
        name = ((VariableReference)nameExpr).getFunctionName();
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        FunctionBlock funcBlock = ctx.retrieveFunction(name);
        
        List<Expression> params = getParameters();
        List<Value> values = new ArrayList<>(params.size());
        params.forEach((expr) -> {
            values.add(expr.evaluate(ctx));
        });
        // call the functon
        Value retValue = funcBlock.call(ctx, values);
        // return the return value
        return ctx.afterExpression(this, retValue == null ? Value.NULL : retValue);
 }
    
    

}
