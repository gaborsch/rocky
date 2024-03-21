/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Gabor
 */
public class RockNumberBigDecimal extends RockNumber {

    // static values
    public static final RockNumberBigDecimal ZERO = create(BigDecimal.ZERO);
    public static final RockNumberBigDecimal ONE = create(BigDecimal.ONE);
    public static final RockNumberBigDecimal TEN = create(BigDecimal.TEN);

    private final BigDecimal value;

    private RockNumberBigDecimal(BigDecimal value) {
    	this.value = value;
    }
    
    private static RockNumberBigDecimal create(BigDecimal value) {
    	return new RockNumberBigDecimal(value.stripTrailingZeros());
    }

    @Override
    public RockNumber getZERO() {
        return ZERO;
    }

    @Override
    public RockNumber getONE() {
        return ONE;
    }

    
    @Override
    public RockNumberBigDecimal getValue(long l) {
        return create(BigDecimal.valueOf(l));
    }
    
    @Override
    protected RockNumber getValue(BigDecimal bigValue) {
    	return create(bigValue);
    }

    @Override
    protected RockNumber doParse(String stringValue, int radix) {
    	try {
	    	if (radix == 10) {
	    		return create(new BigDecimal(stringValue));
	    	}
            // parse as long
            long value = Long.parseLong(stringValue, radix);
            return getValue(value);
        } catch (NumberFormatException nfe) {
        }
        return null;
    }


    private RockNumberBigDecimal convert(RockNumber bn) {
         if (bn instanceof RockNumberBigDecimal) {
            return (RockNumberBigDecimal) bn;
        }
        throw new RockstarRuntimeException("Mixed number types");
    }
    
    @Override
    public RockNumber add(RockNumber bn) {
        return create(value.add(convert(bn).value)); 
    }

    @Override
    public RockNumber subtract(RockNumber bn) {
        return create(value.subtract(convert(bn).value)); 
    }

    @Override
    public RockNumber multiply(RockNumber bn) {
        return create(value.multiply(convert(bn).value));
    }

    @Override
    public RockNumber divide(RockNumber bn) {
        return create(value.divide(convert(bn).value));
    }

    public boolean isNegative() {
        return value.compareTo(ZERO.value) < 0;
    }
    
    @Override
    public RockNumberBigDecimal negate() {
        return create(value.negate());
    }
    
    @Override
    public int compareTo(RockNumber bn) {
        return value.compareTo(convert(bn).value);
    }
    
    @Override
    public long asLong() {
        return value.longValueExact();
    }

    @Override
    public int asInt() {
        return value.intValueExact();
    }

	@Override
	public double asDouble() {
        return value.doubleValue();
	}

	@Override
	public BigDecimal asBigDecimal() {
        return value;
	}

    public String asString() {
    	return value.toPlainString();
    }

    @Override
    public String toString() {
        return asString();
    }

    public String rawString() {
        return "(" + value.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RockNumberBigDecimal) {
        	return value.equals(((RockNumberBigDecimal)obj).value);
        }
        return false;
    }

    @Override
    public int hashCode() {
    	return value.hashCode();
    }

    @Override
    public RockNumber getValue(Double dblValue) {
        return create(new BigDecimal(dblValue));
    }

    @Override
    public RockNumberBigDecimal floor() {
        return create(value.setScale(0, RoundingMode.FLOOR));
    }

    @Override
    public RockNumber ceil() {
        return create(value.setScale(0, RoundingMode.CEILING));
    }

    @Override
    public RockNumber round() {
        return create(value.setScale(0, RoundingMode.HALF_UP));
    }
    
}
