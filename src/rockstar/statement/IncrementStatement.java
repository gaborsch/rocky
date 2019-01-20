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
    
    private final VariableReference variable;
    private final int count;

    public IncrementStatement(VariableReference variable, int count) {
        this.variable = variable;
        this.count = count;
    }

    @Override
    public String toString() {
        return super.toString() + 
                "\n    " + variable + " ++".repeat(count); 
    }
    
    
    
    
}
