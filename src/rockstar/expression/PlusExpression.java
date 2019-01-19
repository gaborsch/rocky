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
public class PlusExpression extends CompoundExpression {

    public PlusExpression(Expression value1, Expression value2) {
        super(value1, value2);
    }
    
    @Override
    protected String getFormat() {
        return "(%s + %s)";
    }
    
    
}
