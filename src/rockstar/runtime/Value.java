/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import rockstar.parser.ExpressionParser;
import rockstar.expression.ExpressionType;
import rockstar.statement.FunctionBlock;

/**
 *
 * @author Gabor
 */
public class Value implements Comparable<Value> {

    public static Value MYSTERIOUS = new Value(ExpressionType.MYSTERIOUS);
    public static Value NULL = new Value(ExpressionType.NULL);
    public static Value BOOLEAN_TRUE = new Value(true);
    public static Value BOOLEAN_FALSE = new Value(false);
    public static Value EMPTY_ARRAY = new Value(ExpressionType.ARRAY);

    private final ExpressionType type;
    private String stringValue;
    private RockNumber numericValue;
    private Boolean boolValue;
    private RockObject objectValue;
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

    public static Value getValue(Collection<Value> coll) {
        Value v = newArrayValue();
        v.listArrayValue.addAll(coll);
        return v;
    }

    public static Value getValue(RockObject instance) {
        return new Value(instance);
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

    private static Value newArrayValue() {
        return new Value(ExpressionType.ARRAY);
    }

    private Value(ExpressionType type) {
        this.type = type;
        if (type == ExpressionType.ARRAY) {
            listArrayValue = new ArrayList<>();
            assocArrayValue = new TreeMap<>();
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

    private Value(RockObject objectValue) {
        this.objectValue = objectValue;
        this.type = ExpressionType.OBJECT;
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

    public boolean isObject() {
        return type == ExpressionType.OBJECT;
    }

    public boolean isArray() {
        return type == ExpressionType.ARRAY;
    }

    public boolean isEmptyArray() {
        return (this.listArrayValue == null || this.listArrayValue.isEmpty())
                && (this.assocArrayValue == null || this.assocArrayValue.isEmpty());
    }

    public RockNumber getNumeric() {
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
            case OBJECT:
                return RockNumber.ZERO;
            case MYSTERIOUS:
                return RockNumber.ZERO;
            case NULL:
                return RockNumber.ZERO;
            case ARRAY:
                return RockNumber.getValue(listArrayValue.size());
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
            case OBJECT: {
                // if we have "describe" method
                BlockContext descContext = objectValue.getContextForFunction(METHOD_NAME_DESCRIPTION);
                // the call context must be the same object as the caller object
                if ((descContext != null)
                        && (descContext instanceof RockObject)
                        && (((RockObject) descContext).getObjId() == objectValue.getObjId())) {
                    // get the method from that context
                    FunctionBlock funcBlock = descContext.retrieveLocalFunction(METHOD_NAME_DESCRIPTION);
                    // call the method
                    Value desc = funcBlock.call(objectValue, new LinkedList<>());
                    // fetch the string value (hopefully it is already a string)
                    return desc.getString();
                } else {
                    return "object " + objectValue.getName();
                }
            }
            case MYSTERIOUS:
                return "mysterious";
            case NULL:
                return "null";
            case ARRAY: {
                StringBuilder sb = new StringBuilder();
                listArrayValue.forEach(v -> {
                    sb.append(sb.length() == 0 ? "" : ",")
                            .append(v);
                });
                assocArrayValue.forEach((k, v) -> {
                    sb.append(sb.length() == 0 ? "" : ",")
                            .append(v)
                            .append(" at ")
                            .append(k);
                });
                return sb.toString();
            }
        }
        throw new RockstarRuntimeException("unknown string value");
    }
    private static final String METHOD_NAME_DESCRIPTION = "description";

    public boolean getBool() {
        switch (getType()) {
            case BOOLEAN:
                return boolValue;
            case NUMBER:
                return numericValue.compareTo(RockNumber.ZERO) != 0;
            case STRING:
                return this.stringValue.length() > 0;
            case OBJECT:
                return true;
            case MYSTERIOUS:
                return false;
            case NULL:
                return false;
            case ARRAY:
                return listArrayValue.size() + assocArrayValue.size() > 0;
        }
        throw new RockstarRuntimeException("unknown bool value");
    }

    public RockObject getObject() {
        if (getType() == ExpressionType.OBJECT) {
            return objectValue;
        }
        throw new RockstarRuntimeException("unknown object value");
    }

    public Value asBoolean() {
        return getValue(getBool());
    }

    public List<Value> asListArray() {
        if (type == ExpressionType.ARRAY) {
            return this.listArrayValue;
        }
        return new LinkedList<>();
    }

    public Map<Value, Value> asAssocArray() {
        if (type == ExpressionType.ARRAY) {
            return this.assocArrayValue;
        }
        return new TreeMap<>();
    }

    @Override
    public String toString() {
        switch (this.type) {
            case NUMBER:
                return numericValue.toString();
            case STRING:
                return /*"\"" +*/ stringValue /*+ "\""*/;
            case BOOLEAN:
                return Boolean.toString(boolValue);
            case ARRAY:
                return ((listArrayValue.size() > 0) ? ("[" + listArrayValue.toString() + "]") : "")
                        + (assocArrayValue.size() > 0 ? ("{" + assocArrayValue.toString() + "}") : "");
            case OBJECT:
                return "Object(" + objectValue + ")";
        }
        return this.type.toString();
    }

    public String describe() {
        switch (this.type) {
            case NUMBER:
                return numericValue.toString();
            case STRING:
                return /*"\"" +*/ stringValue /*+ "\""*/;
            case BOOLEAN:
                return Boolean.toString(boolValue);
            case ARRAY:
                return toString();
            case OBJECT:
                return "Object(" + objectValue + ")\n" + objectValue.describe();
        }
        return this.type.toString();
    }

    public Value negate() {
        // bool negation
        return getBool() ? BOOLEAN_FALSE : BOOLEAN_TRUE;
    }

    public Value plus(Value other) {

        // merge assoc arrays
        if (this.isArray() && other.isArray()) {
            Value v = newArrayValue();
            v.assocArrayValue.putAll(assocArrayValue);
            v.assocArrayValue.putAll(other.assocArrayValue);
            // concatenate list values, too
            v.listArrayValue.addAll(listArrayValue);
            v.listArrayValue.addAll(other.listArrayValue);
            return v;
        }

        // append to list or concatenate
        if (this.isArray() && (!other.isArray())) {
            Value v = newArrayValue();
            // it is changed
            v.listArrayValue.addAll(other.listArrayValue);
            // append value
            v.listArrayValue.add(other);
            // it is not changed, assigned by reference
            v.assocArrayValue = assocArrayValue;
            return v;
        }

        // append to list or concatenate
        if (other.isArray() && (!this.isArray())) {
            Value v = newArrayValue();
            // prepend value
            v.listArrayValue.add(this);
            // it is changed
            v.listArrayValue.addAll(other.listArrayValue);
            // it is not changed, assigned by reference
            v.assocArrayValue = other.assocArrayValue;
            return v;
        }

        // mysterious remains mysterious
        if (this.isMysterious() || other.isMysterious()) {
            return MYSTERIOUS;
        }

        // null + null remains null
        if (this.isNull() && other.isNull()) {
            return NULL;
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
        if (this.isArray()) {
            // remove subset of assoc arrays
            if (other.isString()) {
                // remove element from hash by key
                Value v = newArrayValue();
                // hash is modified
                v.assocArrayValue.putAll(this.assocArrayValue);
                // remove by key
                v.assocArrayValue.remove(other);
                // list is not changed
                v.listArrayValue = this.listArrayValue;
                return v;
            }
            if (other.isNumeric()) {
                // remove element from list by index
                Value v = newArrayValue();
                // hash is not changed
                v.assocArrayValue= this.assocArrayValue;
                // list is modified
                v.listArrayValue.addAll(asListArray());
                // remove by index
                int idx = other.getNumeric().asInt();
                v.listArrayValue.remove(idx);
                return v;
            }
            throw new RockstarRuntimeException("Invalid subtraction from array: type "+other.getType());
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
            if (other.isString()) {
                // String times String is mysterious
                return MYSTERIOUS;
            } else if (v2 != null) {
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
    @Override
    public int compareTo(Value other) {
        if (getType() == other.getType()) {
            // Equal types: compare them without conversion
            switch (getType()) {
                case STRING:
                    return stringValue.compareTo(other.stringValue);
                case NUMBER:
                    return numericValue.compareTo(other.numericValue);
                case BOOLEAN:
                    return (Objects.equals(boolValue, other.boolValue)) ? 0 : 1;
                case OBJECT:
                    return Integer.compare(objectValue.getObjId(), other.objectValue.getObjId());
                case ARRAY:
                    return Integer.compare(assocArrayValue.size() + listArrayValue.size(),
                            other.assocArrayValue.size() + other.listArrayValue.size());
                default:
                    // null, mysterious are equal to themselves
                    return 0;
            }
        }

        // nothing values are equal
        if (isNothing() && other.isNothing()) {
            return 0;
        }

        // mysterious is not equal to anything else
        if (isMysterious() || other.isMysterious()) {
            return 1;
        }

        // object is not equal to anything else
        if (isObject() || other.isObject()) {
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

    private boolean isNothing() {
        switch (this.type) {
            case NULL:
                return true;
            case MYSTERIOUS:
                return true;
            case BOOLEAN:
                return !boolValue;
            case NUMBER:
                return numericValue.equals(RockNumber.ZERO);
            case STRING:
                return stringValue.isEmpty();
            case OBJECT:
                return false;
            case ARRAY:
                return assocArrayValue.size() + listArrayValue.size() == 0;
        }
        return false;
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
                case ARRAY:
                    return Utils.isListEquals(listArrayValue, o.listArrayValue)
                            && Utils.isMapEquals(asAssocArray(), o.asAssocArray());
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

    public Value reference(Value refValue) {

        if (isArray()) {
            if (refValue.isString()) {
                return assocArrayValue.getOrDefault(refValue, MYSTERIOUS);
            } else if (refValue.isNumeric()) {
                int idx = refValue.getNumeric().asInt();
                if (idx >= 0 && idx < listArrayValue.size()) {
                    return listArrayValue.get(idx);
                } else if (idx >= 0) {
                    return MYSTERIOUS;
                } else {
                    throw new RockstarRuntimeException("Negative array index: " + idx);
                }
            }
            throw new RockstarRuntimeException("Invalid array index type: " + refValue.getType());
        }
        throw new RockstarRuntimeException("Dereferencing " + getType() + " type");

    }

    public Value assign(Value refValue, Value setValue) {
        if (isNull() || isArray()) {
            // Empty array handling
            Value v = (isNull() || isEmptyArray()) ? newArrayValue() : this;
            if (refValue.isString()) {
                // set hash value
                v.assocArrayValue.put(refValue, setValue);
                return v;
            } else if (refValue.isNumeric()) {
                // set array index
                int idx = refValue.getNumeric().asInt();
                if (idx >= 0) {
                    List<Value> la = v.listArrayValue;
                    if (idx < la.size()) {
                        // if we have the index, set it
                        la.set(idx, setValue);
                    } else {
                        // if we don't have it, add MYSTERIOUS as the skipped ones
                        while (idx > la.size()) {
                            la.add(MYSTERIOUS);
                        }
                        // finally add the value as last one
                        la.add(setValue);
                    }
                    return v;
                } else {
                    throw new RockstarRuntimeException("Negative array index: " + idx);
                }
            } else {
                throw new RockstarRuntimeException("Invalid array index type: " + refValue.getType());
            }
        }
        throw new RockstarRuntimeException("Indexing a non-array type: " + getType());
    }

}
