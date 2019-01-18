package rockstar.expression;

import java.util.List;

/**
 *
 * @author Gabor
 */
public class FunctionCall extends Expression {

    private String name;
    private List<SimpleExpression> callParameters;

    public FunctionCall(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CALL "+name+":/");
        callParameters.forEach((paramExpr) -> {
            sb.append(paramExpr).append("/");
        });
        return sb.toString(); 
    }
}
