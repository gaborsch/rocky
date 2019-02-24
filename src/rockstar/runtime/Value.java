/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import rockstar.parser.ExpressionParser;
import rockstar.expression.ExpressionType;
import rockstar.expression.Ref;

/**
 *
 * @author Gabor
 */
public class Value {

    public static Value MYSTERIOUS = new Value(ExpressionType.MYSTERIOUS);
    public static Value NULL = new Value(ExpressionType.NULL);
    public static Value BOOLEAN_TRUE = new Value(true);
    public static Value BOOLEAN_FALSE = new Value(false);

    private final ExpressionType type;
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

    private static Value getValue(Ref.Type refType) {
        return new Value(refType == Ref.Type.LIST ? ExpressionType.LIST_ARRAY : ExpressionType.ASSOC_ARRAY);
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
        if (type == ExpressionType.LIST_ARRAY) {
            listArrayValue = new LinkedList<>();
        } else if (type == ExpressionType.ASSOC_ARRAY) {
            assocArrayValue = new HashMap<>();
        }
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

    public boolean isListArray() {
        return type == ExpressionType.LIST_ARRAY;
    }

    public boolean isAssocArray() {
        return type == ExpressionType.ASSOC_ARRAY;
    }

    RockNumber getNumeric() {
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
            case LIST_ARRAY:
                return RockNumber.getValue(listArrayValue.size());
            case ASSOC_ARRAY:
                return RockNumber.getValue(assocArrayValue.size());
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
            case LIST_ARRAY: {
                StringBuilder sb = new StringBuilder();
                listArrayValue.forEach(v -> {
                    sb.append(sb.length() == 0 ? "" : ",")
                            .append(v);
                });
                return sb.toString();
            }
            case ASSOC_ARRAY: {
                StringBuilder sb = new StringBuilder();
                assocArrayValue.forEach((k, v) -> {
                    sb.append(sb.length() == 0 ? "" : ",")
                            .append(k)
                            .append(":")
                            .append(v);
                });
                return sb.toString();
            }
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
            case LIST_ARRAY:
                return listArrayValue.size() > 0;
            case ASSOC_ARRAY:
                return assocArrayValue.size() > 0;
        }
        throw new RockstarRuntimeException("unknown bool value");
    }

    public Value asBoolean() {
        return getValue(getBool());
    }

    public List<Value> asListArray() {
        if (type == ExpressionType.LIST_ARRAY) {
            return this.listArrayValue;
        }
        return new LinkedList<>();
    }

    public Map<Value, Value> asAssocArray() {
        if (type == ExpressionType.ASSOC_ARRAY) {
            return this.assocArrayValue;
        }
        return new HashMap<>();
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
            case LIST_ARRAY:
                return listArrayValue.toString();
            case ASSOC_ARRAY:
                return assocArrayValue.toString();
        }
        return this.type.toString();
    }

    public Value negate() {
        // bool negation
        return getBool() ? BOOLEAN_FALSE : BOOLEAN_TRUE;
    }

    public Value plus(Value other) {
        if (isAssocArray() && other.isAssocArray()) {
            // merge assoc arrays
            Value v = Value.getValue(Ref.Type.ASSOC_ARRAY);
            v.assocArrayValue.putAll(asAssocArray());
            v.assocArrayValue.putAll(other.asAssocArray());
        }
        if (isListArray() && !other.isAssocArray()) {
            // append to list or concatenate
            Value v = Value.getValue(Ref.Type.LIST);
            v.listArrayValue.addAll(asListArray());
            if (other.isListArray() || other.isNull()) {
                // concatenate list
                v.listArrayValue.addAll(other.asListArray());
            } else {
                v.listArrayValue.add(other);
            }
            return v;
        }
        if (other.isListArray() && !isAssocArray()) {
            // prepend to list
            Value v = Value.getValue(Ref.Type.LIST);
            v.listArrayValue.add(this);
            v.listArrayValue.addAll(other.listArrayValue);
            return v;
        }

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
        if (isAssocArray()) {
            // remove subset of assoc arrays
            Value v = Value.getValue(Ref.Type.ASSOC_ARRAY);
            v.assocArrayValue.putAll(asAssocArray());
            if (other.isAssocArray()) {
                // remove all matching keys (ignoring values)
                other.asAssocArray().forEach((k, x) -> {
                    v.assocArrayValue.remove(k);
                });
            } else if (other.isListArray()) {
                // remove all list elements as keys
                other.asListArray().forEach(k -> {
                    v.assocArrayValue.remove(k);
                });
            } else {
                // remove by key expression
                v.assocArrayValue.remove(other);
            }
            return v;
        }

        if (isListArray() && !other.isAssocArray()) {
            // remove element by index
            Value v = Value.getValue(Ref.Type.LIST);
            v.listArrayValue.addAll(asListArray());
            int idx = other.getNumeric().asInt();
            v.listArrayValue.remove(idx);
            return v;
        }

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
                case ASSOC_ARRAY:
                    return Integer.compare(this.asAssocArray().size(), other.asAssocArray().size());
                case LIST_ARRAY:
                    return Integer.compare(this.asListArray().size(), other.asListArray().size());
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
        if (obj == this) {
            return true;
        }
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
                    return Utils.isListEquals(listArrayValue, o.listArrayValue);
                case ASSOC_ARRAY:
                    return Utils.isMapEquals(asAssocArray(), o.asAssocArray());
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
        hash = 89 * hash + Objects.hashCode(this.listArrayValue);
        hash = 89 * hash + Objects.hashCode(this.assocArrayValue);
        return hash;
    }

    public Value dereference(Value refValue) {
        if (isAssocArray()) {
            return this.assocArrayValue.getOrDefault(refValue, Value.MYSTERIOUS);
        } else if (isListArray()) {
            int idx = refValue.getNumeric().asInt();
            if (listArrayValue != null && idx >= 0 && idx < listArrayValue.size()) {
                return listArrayValue.get(idx);
            } else {
                return MYSTERIOUS;
            }
        }
        throw new RockstarRuntimeException("Dereferencing " + getType() + " type");
    }

    public Value assign(Ref.Type refType, Value refValue, Value setValue) {
        // NULL or MYSTERIOUS are treated as empty array
        Value v = (getType() == ExpressionType.NULL || getType() == ExpressionType.MYSTERIOUS)
                ? getValue(refType) : this;
        if (refType == Ref.Type.LIST) {
            int idx = refValue.getNumeric().asInt();
            if (idx >= 0) {
                if (v.getType() == ExpressionType.LIST_ARRAY) {
                    if (idx < v.listArrayValue.size()) {
                        v.listArrayValue.set(idx, setValue);
                    } else {
                        v.listArrayValue.add(setValue);
                    }
                    return this;
                } else {
                    throw new RockstarRuntimeException("Indexing " + getType() + " type");
                }
            } else {
                throw new RockstarRuntimeException("Negative array index: " + idx);
            }
        } else if (refType == Ref.Type.ASSOC_ARRAY) {
            if (getType() == ExpressionType.ASSOC_ARRAY) {
                v.assocArrayValue.put(refValue, setValue);
                return v;
            } else {
                throw new RockstarRuntimeException("Referencing " + getType() + " type");
            }
        }
        // should not reach here
        throw new RuntimeException("Unknown reference type");
    }
}
