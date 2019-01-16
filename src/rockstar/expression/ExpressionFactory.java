/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.Arrays;
import java.util.List;
import rockstar.runtime.NumericValue;

/**
 *
 * @author Gabor
 */
public class ExpressionFactory {
    
    public static Expression getExpressionFor(List<String> tokens) {
        return new DummyExpression(tokens);
    }

    private static final List<String> COMMON_VARIABLE_KEYWORDS = Arrays.asList(new String[] {
        "a", "an", "the", "my", "your", "A", "An", "The", "My", "Your"
    });
    private static final List<String> LAST_NAMED_VARIABLE_REFERENCE_KEYWORDS = Arrays.asList(new String[] {
        "it", "he", "she", "him", "her", "they", "them", "ze", "hir", "zie", "zir", "xe", "xem", "ve", "ver"
    });
    
    private static final List<String> MYSTERIOUS_KEYWORDS = Arrays.asList(new String[] {
        "mysterious"
    });
    private static final List<String> NULL_KEYWORDS = Arrays.asList(new String[] {
        "null", "nothing", "nowhere", "nobody", "empty", "gone"
    });
    private static final List<String> BOOLEAN_TRUE_KEYWORDS = Arrays.asList(new String[] {
        "true", "right", "yes", "ok"
    });
    private static final List<String> BOOLEAN_FALSE_KEYWORDS = Arrays.asList(new String[] {
        "false", "wrong", "no", "lies"
    });
    private static VariableReference lastVariable = null;
    
    public static VariableReference getVariableReferenceFor(List<String> tokens) {
        String name = null;
        if (tokens.isEmpty()) {
            return null;
        }
        String token0 = tokens.get(0);
        if (tokens.size() == 2){
            // common variable
            if (COMMON_VARIABLE_KEYWORDS.contains(token0)) {
                String token1 = tokens.get(1);
                if (token1.toLowerCase().equals(token1)) {
                    name = token0.toLowerCase() + " " + token1;
                }
            }
        } 
        if (name == null && Character.isUpperCase(token0.charAt(0))) {
            // proper variable
            StringBuilder sb = new StringBuilder();
            for(String token : tokens) {
                // all parts of a Proper Name must start with capital letter
                if (Character.isUpperCase(token.charAt(0))) {
                    sb.append(sb.length() > 0 ? " " : "").append(token);
                } else {
                    return null;
                }
            }
            name = sb.toString();
        } 
        if (name == null && tokens.size() == 1) {
            // Variable backreference
// TODO stat of the line capitalization
            if (LAST_NAMED_VARIABLE_REFERENCE_KEYWORDS.contains(token0)) {
                return lastVariable;
            }
        }
        if (name != null) {
            lastVariable = new VariableReference(name);
            return lastVariable;
        }
        return null;
    }
    
    public static ConstantValue getPoeticLiteralFor(List<String> list) {
        ConstantValue literal = getLiteralFor(list);
        if (literal != null) {
            return literal;
        }
        NumericValue v = NumericValue.ZERO;
        boolean isFraction = false;
        NumericValue frac = NumericValue.ONE;
        for (String token : list) {
            int len = token.replace(".", "").length();
            if (! isFraction) {
                // integer part
                v = v.multiply(NumericValue.TEN).plus(NumericValue.getValueFor(len % 10));
                isFraction = token.endsWith(".");
            } else {
                // fraction part
                frac = frac.divide(NumericValue.TEN);
                v = v.plus(frac.multiply(NumericValue.getValueFor(len % 10)));
            }
        }
        return new ConstantValue(v);
    }

    public static ConstantValue getLiteralFor(List<String> list) {
        if(list.size() == 1) {
            String token = list.get(0);
            if (MYSTERIOUS_KEYWORDS.contains(token)) {
                return new ConstantValue(Expression.Type.MYSTERIOUS);
            }
            if (NULL_KEYWORDS.contains(token)) {
                return new ConstantValue(Expression.Type.NULL);
            }
            if (BOOLEAN_TRUE_KEYWORDS.contains(token)) {
                return new ConstantValue(true);
            }
            if (BOOLEAN_FALSE_KEYWORDS.contains(token)) {
                return new ConstantValue(false);
            }
            NumericValue nv = NumericValue.parse(token);
            if(nv != null) {
                return new ConstantValue(nv);
            }
        }

        return null;
    }
    
    
    
}
