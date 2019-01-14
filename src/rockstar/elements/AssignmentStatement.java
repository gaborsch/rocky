/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.elements;

/**
 *
 * @author Gabor
 */
public class AssignmentStatement extends Statement {
    
    private VariableReference variable;
    private Expression expression;

    public AssignmentStatement(VariableReference variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }
    
    
}
