/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.BlockContext;
import rockstar.runtime.NumericValue;
import rockstar.runtime.RockstarRuntimeException;

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

    public NumericValue getNumericValue() {
        return numericValue;
    }

    public String getStringValue() {
        switch (getType()) {
            case STRING:
                return stringValue;
            case NUMBER:
                return numericValue.toString();
            case BOOLEAN:
                return boolValue.toString();
            case MYSTERIOUS:
                return "mysterious";
            case NULL:
                return "null";
        }
        throw new RockstarRuntimeException("unknown string value");
    }

    public Boolean getBoolValue() {
        switch (getType()) {
            case BOOLEAN:
                return boolValue;
            case NUMBER:
                return numericValue.compareTo(NumericValue.ZERO) != 0;
            case STRING:
                return true;
            case MYSTERIOUS:
                return false;
            case NULL:
                return false;
        }
        throw new RockstarRuntimeException("unknown bool value");
    }

    @Override
    public String toString() {
        switch (this.type) {
            case NUMBER:
                return numericValue.toString();
            case STRING:
                return "\"" + stringValue + "\"";
            case BOOLEAN:
                return Boolean.toString(boolValue);
        }
        return this.type.toString();
    }

    @Override
    public ConstantValue evaluate(BlockContext ctx) {
        // constants represent themselves
        return this;
    }

}
