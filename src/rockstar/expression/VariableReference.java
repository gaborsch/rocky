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
public class VariableReference extends SimpleExpression {
    
    private String name;
    private boolean isFunctionName = false;

    public String getName() {
        return name;
    }

    public boolean isFunctionName() {
        return isFunctionName;
    }
    
    

    public VariableReference(String name) {
        this.name = name;
    }

    public VariableReference(String name, boolean isFunctionName) {
        this.name = name;
        this.isFunctionName = isFunctionName;
    }

    @Override
    public String toString() {
        return "`" + name + "`";
    }

}
