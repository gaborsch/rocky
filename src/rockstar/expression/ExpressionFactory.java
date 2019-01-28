/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.List;
import rockstar.parser.Line;
import rockstar.runtime.NumericValue;

/**
 *
 * @author Gabor
 */
public class ExpressionFactory {

    /**
     * tries to parse an expression, returns null if failed
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

    public static ConstantExpression getPoeticLiteralFor(List<String> list, Line line) {
        ConstantExpression literal = tryLiteralFor(list, line);
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
        return new ConstantExpression(v);
    }

}
