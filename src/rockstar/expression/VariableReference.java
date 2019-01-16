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
public class VariableReference extends Expression {
    
    private String name;

    public String getName() {
        return name;
    }

    public VariableReference(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    
    
}
