/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import rockstar.expression.ExpressionParser;
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
        switch (getType()) {
            case NUMBER:
                return numericValue;
            case STRING:
                return NumericValue.parse(stringValue);
            case BOOLEAN:
                return boolValue ? NumericValue.ONE : NumericValue.ZERO;
            case MYSTERIOUS:
                return NumericValue.ZERO;
            case NULL:
                return NumericValue.ZERO;
        }
        throw new RockstarRuntimeException("unknown numeric value");
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

    private static boolean getBoolFromStringAliases(String s) {
        if (ExpressionParser.BOOLEAN_TRUE_KEYWORDS.contains(s)) {
            return true;
        }
        if (ExpressionParser.BOOLEAN_FALSE_KEYWORDS.contains(s)) {
            return false;
        }
        throw new RockstarRuntimeException("unknown bool value: " + s);
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
//        return Value.getValue(getNumeric().plus(other.getNumeric()));
        throw new RockstarRuntimeException(getType() + " plus " + other.getType());
    }

    public Value minus(Value other) {
        if (isNumeric()) {
            if (other.isNumeric()) {
                // numeric subtraction
                return Value.getValue(getNumeric().minus(other.getNumeric()));
            }
        }
//        return Value.getValue(getNumeric().minus(other.getNumeric()));
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
        throw new RockstarRuntimeException(getType() + " times " + other.getType());
    }

    public Value divide(Value other) {
        if (isNumeric()) {
            if (other.isNumeric()) {
                // numeric subtraction
                return Value.getValue(getNumeric().divide(other.getNumeric()));
            }
        }
        throw new RockstarRuntimeException(getType() + " over " + other.getType());
    }

    public Value and(Value other) {
        return getValue(getBool() && other.getBool());
    }

    public Value or(Value other) {
        return getValue(getBool() || other.getBool());
    }

    public Value nor(Value other) {
        return getValue((!getBool()) && (!other.getBool()));

    }

    /**
     * General comparison. For String and Number, -1/0/1 comparators work For
     * other types: 0 (equals) / 1 (non-equals)
     *
     * @param other
     * @return
     */
    private int compareTo(Value other) {
        if (getType() == other.getType()) {
            // Equal types: compare them without conversion
            switch (getType()) {
                case STRING:
                    return stringValue.compareTo(other.stringValue);
                case NUMBER:
                    return numericValue.compareTo(other.numericValue);
                case BOOLEAN:
                    return (boolValue == other.boolValue) ? 0 : 1;
                default:
                    // null, mysterious
                    return 0;
            }
        }

        // Mysterious == Mysterious only
        if (isMysterious() || other.isMysterious()) {
            return 1;
        }
        // String with conversions
        if (isString()) {
            switch (other.getType()) {
                case NULL:
                    return 1;
                case BOOLEAN:
                    // convert String to bool
                    return (getBoolFromStringAliases(stringValue) == other.getBool()) ? 0 : 1;
                case NUMBER:
                    return getNumeric().compareTo(other.getNumeric());
            }
        }
        if (other.isString()) {
            switch (getType()) {
                case NULL:
                    return 1;
                case BOOLEAN:
                    // convert String to bool
                    return (getBool() == getBoolFromStringAliases(other.stringValue)) ? 0 : 1;
                case NUMBER:
                    return getNumeric().compareTo(other.getNumeric());
            }
        }
        // booleans compare as truthiness values with number and null
        if (isBoolean() || other.isBoolean()) {
            return (getBool() == other.getBool()) ? 0 : 1;
        }
        // number vs null
        return (getNumeric().equals(other.getNumeric())) ? 0 : 1;
    }

    public Value isEquals(Value other) {
        return getValue(compareTo(other) == 0);
    }

//    public Value isEquals(Value other) {
//        if (getType() == other.getType()) {
//            // Equal types: compare them without conversion
//            switch (getType()) {
//                case STRING:
//                    return getValue(stringValue.equals(other.stringValue));
//                case NUMBER:
//                    return getValue(numericValue.equals(other.numericValue));
//                case BOOLEAN:
//                    return getValue(boolValue == other.boolValue);
//                default:
//                    // null, mysterious
//                    return BOOLEAN_TRUE;
//            }
//        }
//
//        // Mysterious == Mysterious only
//        if (isMysterious() || other.isMysterious()) {
//            return BOOLEAN_FALSE;
//        }
//        // String with conversions
//        if (isString()) {
//            switch (other.getType()) {
//                case NULL:
//                    return BOOLEAN_FALSE;
//                case BOOLEAN:
//                    // convert String to bool
//                    return getValue(getBoolFromStringAliases(stringValue) == other.getBool());
//                case NUMBER:
//                    return getValue(getNumeric().equals(other.getNumeric()));
//            }
//        }
//        if (other.isString()) {
//            switch (getType()) {
//                case NULL:
//                    return BOOLEAN_FALSE;
//                case BOOLEAN:
//                    // convert String to bool
//                    return getValue(getBool() == getBoolFromStringAliases(other.stringValue));
//                case NUMBER:
//                    return getValue(getNumeric().equals(other.getNumeric()));
//            }
//        }
//        // booleans compare as truthiness values with number and null
//        if (isBoolean() || other.isBoolean()) {
//            return getValue(getBool() == other.getBool());
//        }
//        // number vs null
//        return getValue(getNumeric().equals(other.getNumeric()));
//    }
    public Value isNotEquals(Value other) {
        return getValue(compareTo(other) != 0);
    }

    public Value isLessThan(Value other) {
        return getValue(compareTo(other) < 0);
    }

    public Value isGreaterOrEquals(Value other) {
        return getValue(compareTo(other) >= 0);
    }

    public Value isGreaterThan(Value other) {
        return getValue(compareTo(other) > 0);
    }

    public Value isLessOrEquals(Value other) {
        return getValue(compareTo(other) <= 0);
    }

}
