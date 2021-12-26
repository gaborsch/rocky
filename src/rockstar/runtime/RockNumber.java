package rockstar.runtime;

/**
 * RockNumber is the number abstraction layer for Rockstar
 *
 * @author Gabor
 */
public abstract class RockNumber {

    private static boolean IS_DEC64 = false;
    
    // an instance to delegate the static methods
    private static RockNumber instance = RockNumberDouble.ZERO;
    
    public static void setDec64(boolean isDec64) {
        if (IS_DEC64 != isDec64) {
            IS_DEC64 = isDec64;
            instance = isDec64 ? RockNumberDec64.ZERO : RockNumberDouble.ZERO;
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

    protected abstract RockNumber getValue(Double dblValue);
    protected abstract RockNumber getValue(long l);
    
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

    public abstract RockNumber ceil();
    public abstract RockNumber floor();
    public abstract RockNumber round();

}
