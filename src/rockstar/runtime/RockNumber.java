package rockstar.runtime;

import java.math.BigDecimal;

/**
 * RockNumber is the number abstraction layer for Rockstar
 *
 * @author Gabor
 */
public abstract class RockNumber {

    private static NumericMode mode = NumericMode.IEEE754;

    // an instance to delegate the static methods
    private static RockNumber instance = RockNumberDouble.ZERO;

    public static void setMode(NumericMode newMode) {
    	if (mode != newMode) {
    		mode = newMode;
    		switch (newMode) {
				case IEEE754: 
					instance = RockNumberDec64.ZERO;
					break;
				case DEC64: 
					instance = RockNumberDec64.ZERO;
					break;
				case UNLIMITED: 
					instance = RockNumberBigDecimal.ZERO;
					break;
			}
    	}
    }

    protected abstract RockNumber getZERO();

    protected abstract RockNumber getONE();

    public static RockNumber ZERO() {
        return instance.getZERO();
    }
    public static RockNumber ONE() {
        return instance.getONE();
    }
    
    protected abstract RockNumber doParse(String stringValue, int radix);
    
    public static RockNumber parse(String stringValue) {
        return instance.doParse(stringValue, 10);
    }

    public static RockNumber parseWithRadix(String stringValue, RockNumber radix) {
        int r = radix.asInt();
        return instance.doParse(stringValue, r);
    }

    public static RockNumber fromDouble(double dblValue) {
        return instance.getValue(dblValue);
    }

    public static RockNumber fromBigDecimal(BigDecimal bigValue) {
    	return instance.getValue(bigValue);
    }
    

	public static RockNumber fromLong(long longValue) {
        return instance.getValue(longValue);
    }

    protected abstract RockNumber getValue(Double dblValue);
    protected abstract RockNumber getValue(long l);
    protected abstract RockNumber getValue(BigDecimal bigValue);
    
    public static RockNumber getValueFromLong(long l) {
        return instance.getValue(l);
    }
    
    public abstract int compareTo(RockNumber rn);

    public abstract RockNumber add(RockNumber rn);
    public abstract RockNumber subtract(RockNumber rn);
    public abstract RockNumber multiply(RockNumber rn);
    public abstract RockNumber divide(RockNumber rn);

    public abstract int asInt();
    public abstract long asLong();
    public abstract double asDouble();
	protected abstract BigDecimal asBigDecimal();

    public abstract RockNumber ceil();
    public abstract RockNumber floor();
    public abstract RockNumber round();

}
