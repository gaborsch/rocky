/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.Objects;

/**
 * RockNumber is the number abstraction layer for Rockstar
 *
 * @author Gabor
 */
public class RockNumber {

    private static boolean IS_DEC64 = false;

    public static void setDec64(boolean isDec64) {
        if (ZERO == null || (isDec64 != IS_DEC64)) {
            if (isDec64) {
                ZERO = Dec64.ZERO;
                ONE = Dec64.ONE;
            } else {
                ZERO = new RockNumber(0.0d);
                ONE = new RockNumber(1.0d);
            }
            IS_DEC64 = isDec64;
        }
    }

    public static RockNumber ONE;
    public static RockNumber ZERO;

    static {
        setDec64(false);
    }

    public static RockNumber parse(String stringValue) {
        if (IS_DEC64) {
            return Dec64.parse(stringValue);
        } else {
            try {
                return new RockNumber(Double.parseDouble(stringValue));
            } catch (NumberFormatException nfe) {
                return null;
            }
        }
    }

    public Double dblValue;

    public RockNumber() {
    }

    public RockNumber(Double dblValue) {
        this.dblValue = dblValue;
    }

    public static RockNumber getValue(long l) {
        if (IS_DEC64) {
            return Dec64.getValue(l);
        } else {
            return new RockNumber((double) l);
        }
    }

    public int compareTo(RockNumber rn) {
        return dblValue.compareTo(rn.dblValue);
    }

    public RockNumber add(RockNumber rn) {
        return new RockNumber(dblValue + rn.dblValue);
    }

    public RockNumber subtract(RockNumber rn) {
        return new RockNumber(dblValue - rn.dblValue);
    }

    public RockNumber multiply(RockNumber rn) {
        return new RockNumber(dblValue * rn.dblValue);
    }

    public RockNumber divide(RockNumber rn) {
        return new RockNumber(dblValue / rn.dblValue);
    }

    public int asInt() {
        return dblValue.intValue();
    }

    public long asLong() {
        return dblValue.longValue();
    }

    @Override
    public String toString() {
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
        if (obj instanceof RockNumber) {
            RockNumber o = (RockNumber) obj;
            if (o instanceof Dec64) {
                return false;
            }
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

    public RockNumber ceil() {
        return new RockNumber(Math.ceil(dblValue));
    }

    public RockNumber floor() {
        return new RockNumber(Math.floor(dblValue));
    }

    public RockNumber round() {
        return new RockNumber((double)Math.round(dblValue));
    }
    
}
