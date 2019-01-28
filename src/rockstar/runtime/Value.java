/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import rockstar.expression.ExpressionType;

/**
 *
 * @author Gabor
 */
public class Value {

    public static Value MYSTERIOUS = new Value(ExpressionType.MYSTERIOUS);
    public static Value NULL = new Value(ExpressionType.NULL);
    public static Value BOOLEAN_TRUE = new Value(true);
    public static Value BOOLEAN_FALSE = new Value(false);

    private ExpressionType type = null;
    private String stringValue;
    private NumericValue numericValue;
    private Boolean boolValue;

    public static Value getValue(String s) {
        return new Value(s);
    }

    public static Value getValue(NumericValue n) {
        return new Value(n);
    }

    public static Value getValue(boolean b) {
        return b ? BOOLEAN_TRUE : BOOLEAN_FALSE;
    }

    private Value(ExpressionType type) {
        this.type = type;
    }

    private Value(String stringValue) {
        this.stringValue = stringValue;
        this.type = ExpressionType.STRING;
    }

    private Value(NumericValue numericValue) {
        this.numericValue = numericValue;
        this.type = ExpressionType.NUMBER;
    }

    private Value(Boolean boolValue) {
        this.boolValue = boolValue;
        this.type = ExpressionType.BOOLEAN;
    }

    public ExpressionType getType() {
        return type;
    }

    public boolean isNumeric() {
        return type == ExpressionType.NUMBER;
    }

    public boolean isNull() {
        return type == ExpressionType.NULL;
    }

    public boolean isMysterious() {
        return type == ExpressionType.MYSTERIOUS;
    }

    public boolean isBoolean() {
        return type == ExpressionType.BOOLEAN;
    }

    public boolean isString() {
        return type == ExpressionType.STRING;
    }

    private NumericValue getNumeric() {
        return numericValue;
    }

    private String getString() {
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

    private boolean getBool() {
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

    public Value asBoolean() {
        return getValue(getBool());
    }

    public Value asString() {
        return getValue(getString());
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

    public Value negate() {
        return getBool() ? BOOLEAN_FALSE : BOOLEAN_TRUE;
    }

    public Value plus(Value other) {
        if (isNumeric()) {
            if (other.isNumeric()) {
                // numeric addition
                return Value.getValue(getNumeric().plus(other.getNumeric()));
            }
        } else if (isString()) {
            // String concatenation
            return Value.getValue(getString() + other.getString());
        }
        throw new RockstarRuntimeException(getType() + " plus " + other.getType());
    }

    public Value minus(Value other) {
        if (isNumeric()) {
            if (other.isNumeric()) {
                // numeric subtraction
                return Value.getValue(getNumeric().minus(other.getNumeric()));
            }
        }
        throw new RockstarRuntimeException(getType() + " minus " + other.getType());
    }

    public Value multiply(Value other) {
        if (isNumeric()) {
            if (other.isNumeric()) {
                // numeric multiplication
                return Value.getValue(getNumeric().multiply(other.getNumeric()));
            }
        } else if (isString()) {
            if (other.isNumeric()) {
                // String repeating
                return Value.getValue(getString().repeat(other.getNumeric().asInt()));
            }
        }
        throw new RockstarRuntimeException(getType() + " times " + other.getType());    }

    public Value divide(Value other) {
        if (isNumeric()) {
            if (other.isNumeric()) {
                // numeric subtraction
                return Value.getValue(getNumeric().divide(other.getNumeric()));
            }
        }
        throw new RockstarRuntimeException(getType() + " over " + other.getType());    }


}
