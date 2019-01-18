/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.NumericValue;

/**
 *
 * @author Gabor
 */
public class ConstantValue extends SimpleExpression {
    
    private String stringValue;
    private NumericValue numericValue;
    private Boolean boolValue;

    public ConstantValue(Type type) {
        this.type = type;
    }

    public ConstantValue(String stringValue) {
        this.stringValue = stringValue;
        this.type = Type.STRING;
    }

    public ConstantValue(NumericValue numericValue) {
        this.numericValue = numericValue;
        this.type = Type.NUMBER;
    }
    
    public ConstantValue(Boolean boolValue) {
        this.boolValue = boolValue;
        this.type = Type.BOOLEAN;
    }

    @Override
    public String toString() {
        switch (this.type) {
            case NUMBER: return numericValue.toString();
            case STRING: return "\"" + stringValue + "\"";
            case BOOLEAN: return Boolean.toString(boolValue);
        }
        return this.type.toString();
    }
    
    
    
    
    
}
