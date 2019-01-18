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

    private static VariableReference lastVariable = null;

    public static VariableReference getVariableReferenceFor(List<String> list) {
        ExpressionFactory factory = new ExpressionFactory(list);
        VariableReference varRef = factory.parseVariableReference();
        if (varRef != null && factory.isFullyParsed()) {
            // has valid value and parsed through the list
            return varRef;
        }
        return null;
    }

    public static ConstantValue getLiteralFor(List<String> list) {
        ExpressionFactory factory = new ExpressionFactory(list);
        ConstantValue value = factory.parseLiteral();
        if (value != null && factory.isFullyParsed()) {
            // has valid value and parsed through the list
            return value;
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
            if (!isFraction) {
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

    // tokens of the whole expression
    private List<String> list;
    // next position in the list
    private int idx;
    
    private ExpressionFactory(List<String> list) {
        this.list = list;
        idx = 0;
    }
    
    private boolean isFullyParsed() {
        return idx == list.size();
    }
    private boolean containsAtLeast(int count) {
        return list.size() >= idx + count;
    }
    private String getCurrent() {
        return list.get(idx);
    }
    private String peekNext() {
        return list.get(idx+1);
    }
    
    
    private static final List<String> MYSTERIOUS_KEYWORDS = Arrays.asList(new String[]{
        "mysterious"
    });
    private static final List<String> NULL_KEYWORDS = Arrays.asList(new String[]{
        "null", "nothing", "nowhere", "nobody", "empty", "gone"
    });
    private static final List<String> BOOLEAN_TRUE_KEYWORDS = Arrays.asList(new String[]{
        "true", "right", "yes", "ok"
    });
    private static final List<String> BOOLEAN_FALSE_KEYWORDS = Arrays.asList(new String[]{
        "false", "wrong", "no", "lies"
    });

    private ConstantValue parseLiteral() {
        if (containsAtLeast(1)) {
            String token = getCurrent();
            if (MYSTERIOUS_KEYWORDS.contains(token)) {
                idx++;
                return new ConstantValue(Expression.Type.MYSTERIOUS);
            }
            if (NULL_KEYWORDS.contains(token)) {
                idx++;
                return new ConstantValue(Expression.Type.NULL);
            }
            if (BOOLEAN_TRUE_KEYWORDS.contains(token)) {
                idx++;
                return new ConstantValue(true);
            }
            if (BOOLEAN_FALSE_KEYWORDS.contains(token)) {
                idx++;
                return new ConstantValue(false);
            }
            NumericValue nv = NumericValue.parse(token);
            if (nv != null) {
                idx++;
                return new ConstantValue(nv);
            }
        }
        return null;
    }

    private static final List<String> COMMON_VARIABLE_KEYWORDS = Arrays.asList(new String[]{
        "a", "an", "the", "my", "your", "A", "An", "The", "My", "Your"
    });
    private static final List<String> LAST_NAMED_VARIABLE_REFERENCE_KEYWORDS = Arrays.asList(new String[]{
        "it", "he", "she", "him", "her", "they", "them", "ze", "hir", "zie", "zir", "xe", "xem", "ve", "ver"
    });

    private VariableReference parseVariableReference() {
        String name = null;
        if (! containsAtLeast(1)) {
            return null;
        }
        String token0 = getCurrent();
        if (COMMON_VARIABLE_KEYWORDS.contains(token0) && containsAtLeast(2)) {
            // common variable
            String token1 = peekNext();
            if (token1.toLowerCase().equals(token1)) {
                name = token0.toLowerCase() + " " + token1;
                idx += 2;
            }
        }
        if (name == null && Character.isUpperCase(token0.charAt(0))) {
            // proper variable
            idx ++; // first part processed
            StringBuilder sb = new StringBuilder(token0);
            
            while (! isFullyParsed()) {
                String token = getCurrent();
                // all parts of a Proper Name must start with capital letter
                if (Character.isUpperCase(token.charAt(0))) {
                    idx ++; // next part processed
                    sb.append(" ").append(token);
                }
            }
            name = sb.toString();
        }
        if (name == null && containsAtLeast(1)) {
            // Variable backreference
// TODO start of the line capitalization
            if (LAST_NAMED_VARIABLE_REFERENCE_KEYWORDS.contains(token0)) {
                idx++;
                return lastVariable;
            }
        }
        if (name != null) {
            lastVariable = new VariableReference(name);
            return lastVariable;
        }
        return null;
    }

    
    

}
