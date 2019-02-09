/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Gabor
 */
public class Dec64 {

    private final long mantissa;
    private final int exponent;

    public static Dec64 getValue(long l) {
        return getFromCache(l, 0);
    }

    public static Dec64 getValue(long digits, int fractionCount) {
        return getFromCache(digits, -fractionCount);
    }

    private static final long MAX_VALUE = 0x7fffffffffffffffL;
    private static final long MAX_PARSED_MANTISSA = MAX_VALUE / 10;

    public static Dec64 parse(String s) {
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

    // caches for zero exponent and one mantissa
    private static final Map<Long, Dec64> ZeroExpCache = new HashMap<>();
    private static final Map<Integer, Dec64> OneMantCache = new HashMap<>();

    // static values
    public static final Dec64 ZERO = new Dec64(0, 0);
    public static final Dec64 ONE = new Dec64(1, 0);
    public static final Dec64 TEN = new Dec64(10, 0);
    public static final Dec64 MINUS_ONE = new Dec64(-1, 0);

    private static Dec64 getFromCache(long mantissa, int exponent) {
        Dec64 n = null;
        if (exponent == 0) {
            n = ZeroExpCache.get(mantissa);
        } else if (mantissa == 1) {
            n = OneMantCache.get(exponent);
        }
        if (n == null) {
            n = new Dec64(mantissa, exponent);
            if (exponent == 0 && mantissa >= -127 && (mantissa < 128)) {
                ZeroExpCache.put(mantissa, n);
            } else if (mantissa == 1) {
                OneMantCache.put(exponent, n);
            }
        }
        return n;
    }

    private Dec64(long mantissa, int exponent) {
        this.mantissa = mantissa;
        this.exponent = exponent;
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

    private static int normalE(Dec64 a) {
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

    private static int maxE(Dec64 a) {
        long m = a.mantissa > 0 ? a.mantissa : -a.mantissa;
        int e = a.exponent;
        while (m < MAX_PARSED_MANTISSA && (TEN_POWERS.length - 1 > -e)) {
            m = m * 10;
            e--;
        }
        return e;
    }

    private static long transformM(Dec64 d, int targetE) {
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

    public static Dec64 add(Dec64 a, Dec64 b) {
        return a.add(b);
    }

    public Dec64 add(Dec64 b) {
        int commonE = Integer.min(normalE(this), normalE(b));
        long am = transformM(this, commonE);
        long bm = transformM(b, commonE);
        return getFromCache(am + bm, commonE);
    }

    public static Dec64 subtract(Dec64 a, Dec64 b) {
        return a.subtract(b);
    }

    public Dec64 subtract(Dec64 b) {
        int commonE = Integer.min(normalE(this), normalE(b));
        long am = transformM(this, commonE);
        long bm = transformM(b, commonE);
        return getFromCache(am - bm, commonE);
    }

    public static Dec64 multiply(Dec64 a, Dec64 b) {
        return a.multiply(b);
    }

    public Dec64 multiply(Dec64 b) {
        int ae = normalE(this);
        int be = normalE(b);
        long m = transformM(this, ae) * transformM(b, be);
        return getFromCache(m, ae + be);
    }

    public static Dec64 intDivide(Dec64 a, Dec64 b) {
        return a.intDivide(b);
    }

    public Dec64 intDivide(Dec64 b) {
        int ae = normalE(this);
        int be = normalE(b);
        long m = transformM(this, ae) / transformM(b, be);
        return getFromCache(m, ae - be);
    }

    public static Dec64 divide(Dec64 a, Dec64 b) {
        return a.divide(b);
    }

    public Dec64 divide(Dec64 b) {
        int ae = maxE(this);
        int be = normalE(b);
        long m = transformM(this, ae) / transformM(b, be);
        return getFromCache(m, ae - be);
    }

    public static Dec64 negate(Dec64 a) {
        return getFromCache(-a.mantissa, a.exponent);
    }

    public Dec64 negate() {
        return getFromCache(-mantissa, exponent);
    }

    public static Dec64 floor(Dec64 a) {
        return getFromCache(transformM(a, 0), 0);
    }

    public Dec64 floor() {
        return getFromCache(transformM(this, 0), 0);
    }

    public static int compareTo(Dec64 a, Dec64 b) {
        return a.compareTo(b);
    }

    public int compareTo(Dec64 b) {
        int commonE = Integer.min(normalE(this), normalE(b));
        return Long.compare(transformM(this, commonE), transformM(b, commonE));
    }

    public static long asLong(Dec64 a) {
        return transformM(a, 0);
    }

    public long asLong() {
        return transformM(this, 0);
    }

    public static int asInt(Dec64 a) {
        return (int) transformM(a, 0);
    }

    public int asInt() {
        return (int) transformM(this, 0);
    }

    public String asString() {
        int e = normalE(this);
        long m = transformM(this, e);
        String rawM = Long.toString(m);
        if (e == 0) {
            return rawM;
        } else if (e > 0) {
            return rawM + "0".repeat(e);
        } else {
            int i = rawM.length() + e;
            return (i == 0 ? "0" : "") + rawM.substring(0, i) + "." + rawM.substring(i);
        }
    }

    @Override
    public String toString() {
        return asString();
    }

    public String rawString() {
        return "(" + mantissa + " E" + exponent + ")";
    }

}
