/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.expression.Expression;

/**
 *
 * @author Gabor
 */
public class ReturnStatement extends Statement {
    
    private final Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return super.toString() + 
                "\n    RETURN " + expression ; 
    }
    
    
    
    
}
