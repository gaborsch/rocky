/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

/**
 * Marker class for 'with' clause. 
 * It works just like 'plus', except that it has a special role for MutationExpressions
 * 
 * @author Gabor
 */
public class WithExpression extends PlusExpression {
    
    @Override
    public void accept(ExpressionVisitor visitor) {
    	visitor.visit(this);
    }
    
}
