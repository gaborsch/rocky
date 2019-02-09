/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser;

import java.util.List;
import rockstar.expression.ConstantExpression;
import rockstar.expression.DummyExpression;
import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.runtime.Dec64;

/**
 *
 * @author Gabor
 */
public class ExpressionFactory {

    /**
     * tries to parse an expression, returns null if failed
     *
     * @param tokens
     * @param line
     * @return
     */
    public static Expression tryExpressionFor(List<String> tokens, Line line) {
        Expression parsed = new ExpressionParser(tokens, line).parse();
        if (parsed != null && !(parsed instanceof DummyExpression)) {
            return parsed;
        }
        return null;
    }

    /**
     * Parse an expression, throw exception if failed
     *
     * @param tokens
     * @param line
     * @return
     */
    public static Expression getExpressionFor(List<String> tokens, Line line) {
        Expression parsed = new ExpressionParser(tokens, line).parse();
        if (parsed != null) {
            return parsed;
        }
        return new DummyExpression(tokens, line);
    }

    static VariableReference lastVariable = null;

    /**
     * Try a variable reference, returns null if failed
     *
     * @param list
     * @param line
     * @return
     */
    public static VariableReference tryVariableReferenceFor(List<String> list, Line line) {
        ExpressionParser parser = new ExpressionParser(list, line);
        VariableReference varRef = parser.parseVariableReference();
        if (varRef != null && parser.isFullyParsed()) {
            // has valid value and parsed through the list
            return varRef;
        }
        return null;
    }

    /**
     * Try to parse a literal, returns null if failed
     *
     * @param list
     * @param line
     * @return
     */
    public static ConstantExpression tryLiteralFor(List<String> list, Line line) {
        ExpressionParser parser = new ExpressionParser(list, line);
        ConstantExpression literal = parser.parseLiteral();
        if (literal != null && parser.isFullyParsed()) {
            // has valid value and parsed through the list
            return literal;
        }
        return null;
    }

    public static ConstantExpression getPoeticLiteralFor(List<String> list, Line line, String orig) {
        // if a literal word like "nothing", then use that
        ConstantExpression literal = tryLiteralFor(list, line);
        if (literal != null) {
            return literal;
        }

        // parse the orig String
        Dec64 v = Dec64.ZERO;
        Dec64 frac = Dec64.ONE;
        boolean isFraction = false;
        int digit = 0;
        int pos = 0;
        while (pos <= orig.length()) {
            char c = (pos < orig.length()) ? orig.charAt(pos) : ' ';
            if (Character.isLetter(c)) {
                digit++;
            } else if (c == '.' || c == ' ') {
                if (digit > 0) {
                    if (!isFraction) {
                        // integer part
                        v = v.multiply(Dec64.TEN).add(Dec64.getValue(digit % 10));
                    } else {
                        // fraction part
                        frac = frac.divide(Dec64.TEN);
                        v = v.add(frac.multiply(Dec64.getValue(digit % 10)));
                    }
                }
                if (c == '.') {
                    isFraction = true;
                }
                digit = 0;
            }
            pos++;
        }
        return new ConstantExpression(v);
    }
}
