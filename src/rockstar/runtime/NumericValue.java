/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

/**
 * This will hold a numeric value, according to the required definition of Rockstar
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
    
    private final double value; // TODO: DEC64 representation

    public NumericValue(long value) {
        this.value = value;
    }
    
    public NumericValue(double value) {
        this.value = value;
    }

    public NumericValue plus (NumericValue other) {
        return new NumericValue(value + other.value);
    }

    public NumericValue minus (NumericValue other) {
        return new NumericValue(value - other.value);
    }
    
    public NumericValue multiply (NumericValue other) {
        return new NumericValue(value * other.value);
    }    
    public NumericValue divide (NumericValue other) {
        return new NumericValue(value / other.value);
    }

    public NumericValue power (NumericValue other) {
        return new NumericValue(Math.pow(value, other.value));
    }
    @Override
    public String toString() {
        if (Math.abs(value - Math.round(value)) < 0.000000001) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }
    
    
    
}
