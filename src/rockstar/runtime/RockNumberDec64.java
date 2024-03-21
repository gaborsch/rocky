/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Gabor
 */
public class RockNumberDec64 extends RockNumber {

    // caches for zero exponent and one mantissa
    private static final Map<Long, RockNumberDec64> CACHE_ZERO_EXPONENT = new HashMap<>();
    private static final Map<Integer, RockNumberDec64> CACHE_ONE_MANTISSA = new HashMap<>();

    // static values
    public static final RockNumberDec64 ZERO = new RockNumberDec64(0, 0);
    public static final RockNumberDec64 ONE = new RockNumberDec64(1, 0);
    public static final RockNumberDec64 TEN = new RockNumberDec64(10, 0);
    public static final RockNumberDec64 MINUS_ONE = new RockNumberDec64(-1, 0);
    public static final RockNumberDec64 ONE_HALF = new RockNumberDec64(5, -1);


    private final long mantissa;
    private final int exponent;

    private RockNumberDec64(long mantissa, int exponent) {
        this.mantissa = mantissa;
        this.exponent = exponent;
    }

    private RockNumberDec64() {
        this(0L, 0);
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
    public RockNumberDec64 getValue(long l) {
        return getFromCache(l, 0);
    }

    private static final long MAX_VALUE = 0x7fffffffffffffffL;
    private static final long MAX_PARSED_MANTISSA = MAX_VALUE / 10;

    @Override
    protected RockNumber doParse(String stringValue, int radix) {
        if (radix == 10) {
            return doParseBase10(stringValue);
        }
        try {
            // parse as long
            long value = Long.parseLong(stringValue, radix);
            return getValue(value);
        } catch (NumberFormatException nfe) {
        }
        return null;
    }

    private RockNumberDec64 doParseBase10(String s) {
        boolean fraction = false;
        boolean exp = false;
        boolean negativeM = false;
        boolean negativeE = false;
        int e = 0;
        long m = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                if (exp) {
                    e = e * 10 + (c - '0');
                } else {
                    if (m < MAX_PARSED_MANTISSA) {
                        m = m * 10 + (c - '0');
                        if (fraction) {
                            e--;
                        }
                    } else if (!fraction) {
                        e++;
                    }
                }
            } else if (c == '-') {
                if (exp) {
                    negativeE = true;
                } else {
                    negativeM = true;
                }
            } else if (c == 'e' || c == 'E') {
                exp = true;
            } else if (c == '.') {
                fraction = true;
            } else {
                // unknown character
                return null;
            }
        }
        if (negativeE) {
            e = -e;
        }
        if (negativeM) {
            m = -m;
        }
        return getFromCache(m, e);
    }



    private static RockNumberDec64 getFromCache(long mantissa, int exponent) {
        RockNumberDec64 n = null;
        if (exponent == 0) {
            n = CACHE_ZERO_EXPONENT.get(mantissa);
        } else if (mantissa == 1) {
            n = CACHE_ONE_MANTISSA.get(exponent);
        }
        if (n == null) {
            n = new RockNumberDec64(mantissa, exponent);
            if (exponent == 0 && mantissa >= -127 && (mantissa < 128)) {
                CACHE_ZERO_EXPONENT.put(mantissa, n);
            } else if (mantissa == 1) {
                CACHE_ONE_MANTISSA.put(exponent, n);
            }
        }
        return n;
    }

    private static final long[] TEN_POWERS = {
        1L,
        10L,
        100L,
        1000L,
        10000L,
        100000L,
        1000000L,
        10000000L,
        100000000L,
        1000000000L,
        10000000000L,
        100000000000L,
        1000000000000L,
        10000000000000L,
        100000000000000L,
        1000000000000000L,
        10000000000000000L,
        100000000000000000L
    };

    private static int normalE(RockNumberDec64 a) {
        long m = a.mantissa;
        int e = a.exponent;
        if (m == 0L) {
            return 0;
        }
        while (e != 0 && m != 0L && (m % 10 == 0)) {
            e++;
            m = m / 10;
        }
        return e;
    }

    private static int maxE(RockNumberDec64 a) {
        long m = a.mantissa > 0 ? a.mantissa : -a.mantissa;
        int e = a.exponent;
        while (m < MAX_PARSED_MANTISSA && (TEN_POWERS.length - 1 > -e)) {
            m = m * 10;
            e--;
        }
        return e;
    }

    private static long transformM(RockNumberDec64 d, int targetE) {
        int e = d.exponent - targetE;
        long m = d.mantissa;
        if (e < 0) {
            if (TEN_POWERS.length > -e) {
                return m / TEN_POWERS[-e];
            }
            return 0L;
        } else if (e > 0) {
            if (TEN_POWERS.length > e) {
                return m * TEN_POWERS[e];
            }
            throw new ArithmeticException("Value too large");
        }
        return m;
    }

    private RockNumberDec64 convert(RockNumber bn) {
         if (bn instanceof RockNumberDec64) {
            return (RockNumberDec64) bn;
        }
        throw new RockstarRuntimeException("Mixed number types");
    }
    
    @Override
    public RockNumber add(RockNumber bn) {
        RockNumberDec64 b = convert(bn);
        int commonE = Integer.min(normalE(this), normalE(b));
        long am = transformM(this, commonE);
        long bm = transformM(b, commonE);
        return getFromCache(am + bm, commonE);
    }

    @Override
    public RockNumber subtract(RockNumber bn) {
        RockNumberDec64 b = convert(bn);
        int commonE = Integer.min(normalE(this), normalE(b));
        long am = transformM(this, commonE);
        long bm = transformM(b, commonE);
        return getFromCache(am - bm, commonE);
    }

    @Override
    public RockNumber multiply(RockNumber bn) {
        RockNumberDec64 b = convert(bn);
        int ae = normalE(this);
        int be = normalE(b);
        long m = transformM(this, ae) * transformM(b, be);
        return getFromCache(m, ae + be);
    }

    public RockNumber intDivide(RockNumber bn) {
        RockNumberDec64 b = convert(bn);
        int ae = normalE(this);
        int be = normalE(b);
        long m = transformM(this, ae) / transformM(b, be);
        return getFromCache(m, ae - be);
    }

    @Override
    public RockNumber divide(RockNumber bn) {
        RockNumberDec64 b = convert(bn);
        int ae = maxE(this);
        int be = normalE(b);
        long m = transformM(this, ae) / transformM(b, be);
        return getFromCache(m, ae - be);
    }

    public boolean isNegative() {
        return mantissa>=0;
    }
    
    @Override
    public RockNumberDec64 negate() {
        return getFromCache(-mantissa, exponent);
    }

    
    @Override
    public int compareTo(RockNumber bn) {
        RockNumberDec64 b = convert(bn);
        int commonE = Integer.min(normalE(this), normalE(b));
        return Long.compare(transformM(this, commonE), transformM(b, commonE));
    }
    
    @Override
    public long asLong() {
        return transformM(this, 0);
    }

    @Override
    public int asInt() {
        return (int) transformM(this, 0);
    }

	@Override
	public double asDouble() {
		return Double.valueOf(asString());
	}

	@Override
	public BigDecimal asBigDecimal() {
		return new BigDecimal(asString());
	}

    public String asString() {
        int e = normalE(this);
        long m = transformM(this, e);
        String rawM = Long.toString(m);
        if (e == 0) {
            return rawM;
        } else if (e > 0) {
            return rawM + Utils.repeat("0", e);
        } else {
            boolean isPositive = (m >= 0);
            int i = rawM.length() + e;
            if (isPositive) {
                return (i == 0 ? "0" : "") + rawM.substring(0, i) + "." + rawM.substring(i);
            } else {
                return (i == 1 ? "-0" : "-") + rawM.substring(1, i) + "." + rawM.substring(i);
            }
        }
    }

    @Override
    public String toString() {
        return asString();
    }

    public String rawString() {
        return "(" + mantissa + " E" + exponent + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if(obj instanceof RockNumberDec64) {
            RockNumberDec64 o = (RockNumberDec64) obj;
            int commonE = Integer.min(normalE(this), normalE(o));
            return transformM(this, commonE) == transformM(o, commonE);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (int) (this.mantissa ^ (this.mantissa >>> 32));
        hash = 29 * hash + this.exponent;
        return hash;
    }

    @Override
    public RockNumber getValue(Double dblValue) {
        return parse(Double.toString(dblValue));
    }

    @Override
    protected RockNumber getValue(BigDecimal bigValue) {
    	return parse(bigValue.toPlainString());
    }

    @Override
    public RockNumberDec64 floor() {
        if (isNegative()) {
            return getFromCache(transformM(this, 0), 0);
        } else {
            return getFromCache(-transformM(negate(), 0) - 1, 0);
        }
    }

    @Override
    public RockNumber ceil() {
        RockNumber floor = floor();
        if (equals(floor)) {
            return floor;
        }
        return floor.add(ONE);
    }

    @Override
    public RockNumber round() {
        RockNumber floor = floor();
        if ((compareTo(floor.add(ONE_HALF)) < 0)) {
            return floor;
        }
        return floor.add(ONE);
    }
    
}
