/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.BlockContext;

/**
 *
 * @author Gabor
 */
public abstract class Expression {

    
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
  
    public ConstantValue evaluate(BlockContext ctx) {
        return new ConstantValue(this.toString());
    }
    
    public boolean isNumeric() {
        return type == Type.NUMBER;
    }
    public boolean isNull() {
        return type == Type.NULL;
    }
    public boolean isMysterious() {
        return type == Type.MYSTERIOUS;
    }
    public boolean isBoolean() {
        return type == Type.BOOLEAN;
    }
    public boolean isString() {
        return type == Type.STRING;
    }
}

