package rockstar.expression;

import java.util.List;

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
    protected String getFormat() {
        StringBuilder sb = new StringBuilder("CALL ");
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
        name = ((VariableReference)nameExpr).getName();
    }
    
    

}
