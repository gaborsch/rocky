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
public class WhileStatement extends Block {

    private final Expression condition;

    public WhileStatement(Expression condition) {
        this.condition = condition;
    }

    
    public Expression getCondition() {
        return condition;
    }
    
     @Override
    public String toString() {
        return super.toString() + 
                "\n    COND: " + condition ; 
    }   

}
