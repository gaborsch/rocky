package rockstar.runtime;

import java.util.Objects;

/**
 * RockNumber is the number abstraction layer for Rockstar
 *
 * @author Gabor
 */
public abstract class Number {

    private static boolean IS_DEC64 = false;
    
    // an instance to delegate the static methods
    private static Number instance;
    
    static {
        setDec64(false);
    }

    public static void setDec64(boolean isDec64) {
        if (IS_DEC64 != isDec64) {
            IS_DEC64 = isDec64;
            instance = isDec64 ? NumberDec64.ZERO : RockNumberNew.ZERO;
        }
    }

    abstract Number getZERO();
    abstract Number getONE();

    abstract Number doParse(String stringValue, int radix);
    
    public static Number parse(String stringValue) {
        return instance.doParse(stringValue, 10);
    }

    public static Number parseWithRadix(String stringValue, Number radix) {
        int r = radix.asInt();
        return instance.doParse(stringValue, r);
    }

    abstract Number getValue(Double dblValue);
    abstract Number getValue(long l);
            
    public abstract int compareTo(Number rn);

    public abstract Number add(Number rn);
    public abstract Number subtract(Number rn);
    public abstract Number multiply(Number rn);
    public abstract Number divide(Number rn);

    public abstract int asInt();
    public abstract long asLong();

    public abstract Number ceil();
    public abstract Number floor();
    public abstract Number round();

}
