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
public class IfStatement extends Block {

    private Expression condition;

    public IfStatement(Expression condition) {
        this.condition = condition;
    }

    
    public Expression getCondition() {
        return condition;
    }

}
