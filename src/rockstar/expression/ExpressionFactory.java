/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import rockstar.runtime.NumericValue;

/**
 *
 * @author Gabor
 */
public class ExpressionFactory {

    public static Expression getExpressionFor(List<String> tokens) {
        Expression parsed = new ExpressionParser(tokens).parse();
        if (parsed != null) {
            return parsed;
        }
        return new DummyExpression(tokens);
    }

    private static VariableReference lastVariable = null;

    public static VariableReference getVariableReferenceFor(List<String> list) {
        ExpressionParser parser = new ExpressionParser(list);
        VariableReference varRef = parser.parseVariableReference();
        if (varRef != null && parser.isFullyParsed()) {
            // has valid value and parsed through the list
            return varRef;
        }
        return null;
    }

    public static ConstantValue getLiteralFor(List<String> list) {
        ExpressionParser parser = new ExpressionParser(list);
        ConstantValue value = parser.parseLiteral();
        if (value != null && parser.isFullyParsed()) {
            // has valid value and parsed through the list
            return value;
        }
        return null;
    }

    public static SimpleExpression getParameterFor(List<String> list) {
        ExpressionParser parser = new ExpressionParser(list);
        VariableReference varRef = parser.parseVariableReference();
        if (varRef != null && parser.isFullyParsed()) {
            // has valid value and parsed through the list
            return varRef;
        }
        ConstantValue value = parser.parseLiteral();
        if (value != null && parser.isFullyParsed()) {
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

    private static class ExpressionParser {
        // tokens of the whole expression

        private final List<String> list;
        // next position in the list
        private int idx;

        private ExpressionParser(List<String> list) {
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

        private void next() {
            idx++;
        }

        private void next(int count) {
            idx += count;
        }

        private String peekNext() {
            return list.get(idx + 1);
        }

        private String peekNext(int offset) {
            return list.get(idx + offset);
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
            if (!isFullyParsed()) {
                String token = getCurrent();
                if (MYSTERIOUS_KEYWORDS.contains(token)) {
                    next();
                    return new ConstantValue(Expression.Type.MYSTERIOUS);
                }
                if (NULL_KEYWORDS.contains(token)) {
                    next();
                    return new ConstantValue(Expression.Type.NULL);
                }
                if (BOOLEAN_TRUE_KEYWORDS.contains(token)) {
                    next();
                    return new ConstantValue(true);
                }
                if (BOOLEAN_FALSE_KEYWORDS.contains(token)) {
                    next();
                    return new ConstantValue(false);
                }
                NumericValue nv = NumericValue.parse(token);
                if (nv != null) {
                    next();
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
            if (isFullyParsed()) {
                return null;
            }
            String token0 = getCurrent();
            if (COMMON_VARIABLE_KEYWORDS.contains(token0) && containsAtLeast(2)) {
                // common variable
                String token1 = peekNext();
                if (token1.toLowerCase().equals(token1)) {
                    name = token0.toLowerCase() + " " + token1;
                    next(2);
                }
            }
            if (name == null && Character.isUpperCase(token0.charAt(0))) {
                // proper variable
                next(); // first part processed
                StringBuilder sb = new StringBuilder(token0);

                while (!isFullyParsed()) {
                    String token = getCurrent();
                    // all parts of a Proper Name must start with capital letter
                    if (Character.isUpperCase(token.charAt(0))) {
                        next(); // next part processed
                        sb.append(" ").append(token);
                    } else {
                        break;
                    }
                }
                name = sb.toString();
            }
            if (name == null && containsAtLeast(1)) {
                // Variable backreference
// TODO start of the line capitalization
                if (LAST_NAMED_VARIABLE_REFERENCE_KEYWORDS.contains(token0)) {
                    next();
                    return lastVariable;
                }
            }
            if (name != null) {
                lastVariable = new VariableReference(name);
                return lastVariable;
            }
            return null;
        }

        public SimpleExpression parseSimpleExpression() {
            SimpleExpression expr = parseLiteral();
            if (expr == null) {
                expr = parseVariableReference();
            }
            return expr;
        }
/*
        public Expression parseExpression1() {
            if (isFullyParsed()) {
                return null;
            }

            List<Expression> valueList = new LinkedList<>();
            List<CompoundExpression> operatorStack = new LinkedList<>();

            while (!isFullyParsed()) {
                // get a value
                SimpleExpression value = parseLiteral();
                if (value != null) {
                    value = parseVariableReference();
                }
                if (value != null) {
                    valueList.add(value);
                }

            }

            if (!isFullyParsed()) {
                return null;
            }
            return valueList.get(0);
        }
*/
        public Expression parse() {
            if (isFullyParsed()) {
                return null;
            }
            String token = this.getCurrent();
            if ("not".equals(token)) {
                next();
                Expression expr = parse();
                return new NotExpression(expr);
            }

            Expression value1 = parseSimpleExpression();
            Expression value2;
            if (isFullyParsed() || value1 == null) {
                return value1;
            }

            String operator = this.getCurrent();

            if ("times".equals(operator) || "of".equals(operator)) {
                next();
                value2 = parse();
                return new MultiplyExpression(value1, value2);
            }

            if ("plus".equals(operator) || "with".equals(operator)) {
                next();
                value2 = parse();
                
                return new PlusExpression(value1, value2);
            }
            /*            if("taking".equals(operator)) {
                
            }
             */

            return null;
        }

    }
}

/*
   1 + 2 * 3


    add(1,mul(2,3)) ~
    

   1 * 2 + 3

   add(mul(1,2),3)

 */
