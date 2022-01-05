package rockstar.runtime;

import java.util.Objects;

/**
 * RockNumber is the number abstraction layer for Rockstar
 *
 * @author Gabor
 */
public class RockNumberDouble extends RockNumber {

    static final RockNumberDouble ZERO = new RockNumberDouble(0.0D);
    static final RockNumberDouble ONE = new RockNumberDouble(1.0D);
    
    public final Double dblValue;

    private RockNumberDouble(Double dblValue) {
        this.dblValue = dblValue;
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
    protected RockNumber doParse(String stringValue, int radix) {
        if (radix == 10) {
            try {
                // parse as double
                return new RockNumberDouble(Double.parseDouble(stringValue));
            } catch (NumberFormatException nfe) {
            }
        }
        
        
        try {
            // parse as long
            return new RockNumberDouble((double)Long.parseLong(stringValue, radix));
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    @Override
    public RockNumber getValue(Double dblValue) {
        return new RockNumberDouble(dblValue);
    }

    @Override
    public RockNumber getValue(long l) {
        return new RockNumberDouble((double) l);
    }

    private double convertToDbl(RockNumber n) {
        if (n instanceof RockNumberDouble) {
            return ((RockNumberDouble) n).dblValue;
        }
        throw new RockstarRuntimeException("Mixed number types");
    }    
    
    @Override
    public int compareTo(RockNumber rn) {
        return dblValue.compareTo(convertToDbl(rn));
    }

    @Override
    public RockNumberDouble add(RockNumber rn) {
        return new RockNumberDouble(dblValue + convertToDbl(rn));
    }

    @Override
    public RockNumberDouble subtract(RockNumber rn) {
        return new RockNumberDouble(dblValue - convertToDbl(rn));
    }

    @Override
    public RockNumberDouble multiply(RockNumber rn) {
        return new RockNumberDouble(dblValue * convertToDbl(rn));
    }

    @Override
    public RockNumberDouble divide(RockNumber rn) {
        return new RockNumberDouble(dblValue / convertToDbl(rn));
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
    public double asDouble() {
    	return dblValue;
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
        if (obj instanceof RockNumberDouble) {
            RockNumberDouble o = (RockNumberDouble) obj;
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
    public RockNumber ceil() {
        return new RockNumberDouble(Math.ceil(dblValue));
    }

    @Override
    public RockNumber floor() {
        return new RockNumberDouble(Math.floor(dblValue));
    }

    @Override
    public RockNumber round() {
        return new RockNumberDouble((double)Math.round(dblValue));
    }

    
}
