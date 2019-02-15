/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

/**
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
        setDec64(true);
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

    public int compareTo(RockNumber b) {
        return 0;
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
        return dblValue.toString();
    }
    
    

}
