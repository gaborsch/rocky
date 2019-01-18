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
public class DecrementStatement extends Statement {
    
    private VariableReference variable;
    private int count;

    public DecrementStatement(VariableReference variable, int count) {
        this.variable = variable;
        this.count = count;
    }

    @Override
    public String toString() {
        return super.toString() + 
                "\n    " + variable  + " --".repeat(count); 
    }
    
    
    
    
}
