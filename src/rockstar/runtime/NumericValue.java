/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.math.BigDecimal;

/**
 * This will hold a numeric value, according to the required specification of Rockstar
 * @author Gabor
 */
public class NumericValue {
    
    public static final NumericValue ZERO = new NumericValue(0);
    public static final NumericValue ONE = new NumericValue(1);
    public static final NumericValue TEN = new NumericValue(10);
    
    private static final NumericValue[] CACHED_VALUES = {
        ZERO,
        ONE,
        new NumericValue(2),
        new NumericValue(3),
        new NumericValue(4),
        new NumericValue(5),
        new NumericValue(6),
        new NumericValue(7),
        new NumericValue(8),
        new NumericValue(9),
        TEN
    };
    
    public static NumericValue getValueFor(long l) {
        if (l >= 0 && l < CACHED_VALUES.length) {
            return CACHED_VALUES[(int)l];
        }
        return new NumericValue(l);
    }

    public static NumericValue parse(String token) {
        try {
            double d = Double.parseDouble(token);        
            return new NumericValue(d);
        } catch (NumberFormatException e) {
            // not a double, no problem
        }
        try {
            long l = Long.parseLong(token);
            return new NumericValue(l);
        } catch (NumberFormatException e) {
            // not a long, no problem
        }
        return null;
    }
    
    private final BigDecimal value; // TODO: DEC64 representation

    public NumericValue(long value) {
        this.value = new BigDecimal(value);
    }
    
    public NumericValue(double value) {
        this.value = new BigDecimal(value);
    }

    private NumericValue(BigDecimal value) {
        this.value = value;
    }

    public NumericValue plus (NumericValue other) {
        return new NumericValue(value.add(other.value));
    }

    public NumericValue minus (NumericValue other) {
        return new NumericValue(value.subtract(other.value));
    }
    
    public NumericValue multiply (NumericValue other) {
        return new NumericValue(value.multiply(other.value));
    }    
    public NumericValue divide (NumericValue other) {
        return new NumericValue(value.divide(other.value));
    }

    public NumericValue power (NumericValue other) {
        // TODO: fractional power
        return new NumericValue(value.pow(other.value.intValue()));
    }
    
    public int compareTo(NumericValue other) {
        return value.compareTo(other.value);
    }
    
    @Override
    public String toString() {
        return this.value.toPlainString();
    }
    
    public long asLong() {
        return value.longValue();
    }
    
    public int asInt() {
        return value.intValue();
    }
    
    public BigDecimal asBigDecimal() {
        return value;
    }
    
}
