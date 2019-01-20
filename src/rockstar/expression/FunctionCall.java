package rockstar.expression;

import java.util.List;

/**
 *
 * @author Gabor
 */
public class FunctionCall extends CompoundExpression {

    private final String name;

    public FunctionCall(String name, Expression ... params) {
        super(params);
        this.name = name;
    }

    @Override
    public int getPrecedence() {
        // TODO
        return 0;
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

}
