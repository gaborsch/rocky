/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

/**
 *
 * @author Gabor
 */
public class ComparisonExpression extends CompoundExpression {
    
    public enum ComparisonType {
        EQUALS("=="),
        NOT_EQUALS("!="),
        GREATER_THAN(">"),
        LESS_THAN("<"),
        GREATER_OR_EQUALS(">="),
        LESS_OR_EQUALS("<=");
        
        private final String sign;
        ComparisonType(String sign) {
            this.sign = sign;
        }

        public String getSign() {
            return sign;
        }
        
    }
    
    private ComparisonType type;

    public ComparisonExpression(ComparisonType type) {
        super();
        this.type = type;
    }
    
    
            
    @Override
    protected String getFormat() {
        return "(%s "+type.getSign()+" %s)";
    }
    
    @Override
    public int getPrecedence() {
        return 700;
    }
    
    @Override
    public int getParameterCount() {
        return 2;
    }
   
}
