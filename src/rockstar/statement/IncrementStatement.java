/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.expression.VariableReference;

/**
 *
 * @author Gabor
 */
public class IncrementStatement extends Statement {
    
    private VariableReference variable;

    public IncrementStatement(VariableReference variable) {
        this.variable = variable;
    }

    @Override
    public String toString() {
        return super.toString() + 
                "\n    variable: " + variable ; 
    }
    
    
    
    
}
