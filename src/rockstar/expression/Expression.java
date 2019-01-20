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
public class Expression {
    
    public enum Type {
        MYSTERIOUS,
        NULL,
        BOOLEAN,
        NUMBER,
        STRING
    }
    
    protected Type type = null;

    public Type getType() {
        return type;
    }
  
}

