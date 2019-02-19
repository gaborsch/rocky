/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import rockstar.parser.ExpressionParser;
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
    private RockNumber numericValue;
    private Boolean boolValue;
    private List<Value> listArrayValue;
    private Map<Value, Value> assocArrayValue;

    public static Value getValue(String s) {
        return new Value(s);
    }

    public static Value getValue(RockNumber n) {
        return new Value(n);
    }

    public static Value getValue(boolean b) {
        return b ? BOOLEAN_TRUE : BOOLEAN_FALSE;
    }

    public static Value parse(String s) {
        if (ExpressionParser.MYSTERIOUS_KEYWORDS.contains(s)) {
            return MYSTERIOUS;
        } else if (ExpressionParser.NULL_KEYWORDS.contains(s)) {
            return NULL;
        } else if (ExpressionParser.BOOLEAN_TRUE_KEYWORDS.contains(s)) {
            return BOOLEAN_TRUE;
        } else if (ExpressionParser.BOOLEAN_FALSE_KEYWORDS.contains(s)) {
            return BOOLEAN_FALSE;
        } else {
            RockNumber numericValue = RockNumber.parse(s);
            if (numericValue != null) {
                return getValue(numericValue);
            }
        }
        if (s.length() >= 2 && s.matches("\".*\"")) {
            s = s.substring(1, s.length() - 1);
        }
        return getValue(s);
    }

    private Value(ExpressionType type) {
        this.type = type;
    }

    private Value(String stringValue) {
        this.stringValue = stringValue;
        this.type = ExpressionType.STRING;
    }

    private Value(RockNumber numericValue) {
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

    private RockNumber getNumeric() {
        switch (getType()) {
            case NUMBER:
                return numericValue;
            case STRING:
                try {
                    return RockNumber.parse(stringValue);
                } catch (NumberFormatException nfe) {
                    return null;
                }
            case BOOLEAN:
                return boolValue ? RockNumber.ONE : RockNumber.ZERO;
            case MYSTERIOUS:
                return RockNumber.ZERO;
            case NULL:
                return RockNumber.ZERO;
        }
        throw new RockstarRuntimeException("unknown numeric value");
    }

    public String getString() {
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

    public boolean getBool() {
        switch (getType()) {
            case BOOLEAN:
                return boolValue;
            case NUMBER:
                return numericValue.compareTo(RockNumber.ZERO) != 0;
            case STRING:
                return this.stringValue.length() > 0;
            case MYSTERIOUS:
                return false;
            case NULL:
                return false;
        }
        throw new RockstarRuntimeException("unknown bool value");
    }

    private static Boolean getBoolFromStringAliases(String s) {
        if (ExpressionParser.BOOLEAN_TRUE_KEYWORDS.contains(s.toLowerCase())) {
            return Boolean.TRUE;
        }
        if (ExpressionParser.BOOLEAN_FALSE_KEYWORDS.contains(s.toLowerCase())) {
            return Boolean.FALSE;
        }
        return null;
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
        // bool negation
        return getBool() ? BOOLEAN_FALSE : BOOLEAN_TRUE;
    }

    public Value plus(Value other) {
        if (isString() || other.isString()) {
            // String concatenation
            return Value.getValue(getString() + other.getString());
        } else {
            RockNumber v1 = getNumeric();
            RockNumber v2 = other.getNumeric();
            if (v1 != null && v2 != null) {
                // numeric addition (cannot be String)
                return Value.getValue(v1.add(v2));
            }
        }
        throw new RockstarRuntimeException(getType() + " plus " + other.getType());
    }

    public Value minus(Value other) {
        RockNumber v1 = getNumeric();
        RockNumber v2 = other.getNumeric();
        if (v1 != null && v2 != null) {
            // numeric subtraction
            return Value.getValue(v1.subtract(v2));
        }
        throw new RockstarRuntimeException(getType() + " minus " + other.getType());
    }

    public Value multiply(Value other) {
        RockNumber v2 = other.getNumeric();
        if (isString()) {
            if (v2 != null) {
                // String repeating (STRING times NUMBER)
                return Value.getValue(Utils.repeat(getString(), v2.asInt()));
            }
        } else if (other.isString()) {
            RockNumber v1 = getNumeric();
            if (v1 != null) {
                // String repeating (NUMBER times STRING)
                return Value.getValue(Utils.repeat(other.getString(), v1.asInt()));
            }
        } else {
            if (v2 != null) {
                // numeric multiplication (cannot be String)
                return Value.getValue(getNumeric().multiply(v2));
            }
        }
        throw new RockstarRuntimeException(getType() + " times " + other.getType());
    }

    public Value divide(Value other) {
        RockNumber v1 = getNumeric();
        RockNumber v2 = other.getNumeric();
        if (v1 != null && v2 != null) {
            // numeric division
            return Value.getValue(v1.divide(v2));
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
                    return (Objects.equals(boolValue, other.boolValue)) ? 0 : 1;
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
//                    Boolean b = getBoolFromStringAliases(stringValue);
                    boolean b = stringValue != null;
                    return (b == other.getBool()) ? 0 : 1;
                case NUMBER:
                    RockNumber v1 = getNumeric();
                    return (v1 == null) ? 1 : v1.compareTo(other.getNumeric());

            }
        }
        if (other.isString()) {
            switch (getType()) {
                case NULL:
                    return 1;
                case BOOLEAN:
                    // convert String to bool
                    boolean b = other.stringValue != null;
                    return (getBool() == b) ? 0 : 1;
                case NUMBER:
                    RockNumber v2 = other.getNumeric();
                    return (v2 == null) ? -1 : getNumeric().compareTo(v2);
            }
        }
        // booleans compare as truthiness values with number and null
        if (isBoolean() || other.isBoolean()) {
            return (getBool() == other.getBool()) ? 0 : 1;
        }
        // number vs null
        return getNumeric().compareTo(other.getNumeric());
    }

    public Value isEquals(Value other) {
        return getValue(compareTo(other) == 0);
    }

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Value) {
            Value o = (Value) obj;
            if (this.type != o.type) {
                return false;
            }
            switch (this.type) {
                case NUMBER:
                    return numericValue.equals(o.numericValue);
                case STRING:
                    return stringValue.equals(o.stringValue);
                case MYSTERIOUS:
                    return true;
                case NULL:
                    return true;
                case BOOLEAN:
                    return Objects.equals(boolValue, o.boolValue);
                case LIST_ARRAY:
                    return isListEquals(listArrayValue, o.listArrayValue);
                case ASSOC_ARRAY:
                    return isMapEquals(assocArrayValue, o.assocArrayValue);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.type);
        hash = 89 * hash + Objects.hashCode(this.stringValue);
        hash = 89 * hash + Objects.hashCode(this.numericValue);
        hash = 89 * hash + Objects.hashCode(this.boolValue);
        return hash;
    }

    private boolean isListEquals(List<Value> l1, List<Value> l2) {
        if (l1 == null && l2 == null) {
            return true;
        }
        if ((null == l1 || null == l2) || (l1.size() != l2.size())) {
            return false;
        }
        Iterator<Value> it1 = l1.iterator();
        Iterator<Value> it2 = l2.iterator();
        while (it1.hasNext()) {
            Value v1 = it1.next();
            Value v2 = it2.next();
            if (!v1.equals(v2)) {
                return false;
            }
        }
        return true;
    }

    private boolean isMapEquals(Map<Value, Value> m1, Map<Value, Value> m2) {
        if (m1 == null && m2 == null) {
            return true;
        }
        if ((null == m1 || null == m2) || (m1.size() != m2.size())) {
            return false;
        }
        Iterator<Value> it1 = m1.keySet().iterator();
        while (it1.hasNext()) {
            Value key1 = it1.next();
            Value v1 = m2.get(key1);
            Value v2 = m2.get(key1);
            if (v2 == null || !v1.equals(v2)) {
                return false;
            }
        }
        return true;
    }

}
