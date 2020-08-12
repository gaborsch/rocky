package rockstar.runtime;

import java.util.Objects;

/**
 * RockNumber is the number abstraction layer for Rockstar
 *
 * @author Gabor
 */
public class RockNumberNew extends Number {

    static final RockNumberNew ZERO = new RockNumberNew(0.0D);
    static final RockNumberNew ONE = new RockNumberNew(1.0D);
    
    public final Double dblValue;

    private RockNumberNew(Double dblValue) {
        this.dblValue = dblValue;
    }

    @Override
    Number getZERO() {
        return ZERO;
    }

    @Override
    Number getONE() {
        return ONE;
    }

    @Override
    Number doParse(String stringValue, int radix) {
        if (radix == 10) {
            try {
                // parse as double
                return new RockNumberNew(Double.parseDouble(stringValue));
            } catch (NumberFormatException nfe) {
            }
        }
        
        
        try {
            // parse as long
            return new RockNumberNew((double)Long.parseLong(stringValue, radix));
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    @Override
    Number getValue(Double dblValue) {
        return new RockNumberNew(dblValue);
    }

    @Override
    Number getValue(long l) {
        return new RockNumberNew((double) l);
    }

    private double convertToDbl(Number n) {
        if (n instanceof RockNumberNew) {
            return ((RockNumberNew) n).dblValue;
        }
        throw new RockstarRuntimeException("Mixed number types");
    }    
    
    @Override
    public int compareTo(Number rn) {
        return dblValue.compareTo(convertToDbl(rn));
    }

    @Override
    public RockNumberNew add(Number rn) {
        return new RockNumberNew(dblValue + convertToDbl(rn));
    }

    @Override
    public RockNumberNew subtract(Number rn) {
        return new RockNumberNew(dblValue - convertToDbl(rn));
    }

    @Override
    public RockNumberNew multiply(Number rn) {
        return new RockNumberNew(dblValue * convertToDbl(rn));
    }

    @Override
    public RockNumberNew divide(Number rn) {
        return new RockNumberNew(dblValue / convertToDbl(rn));
    }

    @Override
    public int asInt() {
        return dblValue.intValue();
    }

    @Override
    public long asLong() {
        return dblValue.longValue();
    }

    @Override
    public String toString() {
        double rounded = (double)Math.round(dblValue);
        if (dblValue.equals(rounded)) {
            // integral value
            return Long.toString(dblValue.longValue());
        }
        // fractional value
        String s = dblValue.toString();
        if (s.endsWith(".0")) {
            // chop fraction if integral value
            return s.substring(0, s.length() - 2);
        }
        return s;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof RockNumberNew) {
            RockNumberNew o = (RockNumberNew) obj;
            return Objects.equals(this.dblValue, o.dblValue);
        }
        return false;
    }    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.dblValue);
        return hash;
    }

    @Override
    public Number ceil() {
        return new RockNumberNew(Math.ceil(dblValue));
    }

    @Override
    public Number floor() {
        return new RockNumberNew(Math.floor(dblValue));
    }

    @Override
    public Number round() {
        return new RockNumberNew((double)Math.round(dblValue));
    }

    
}
