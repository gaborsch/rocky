/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

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

    static VariableReference lastVariable = null;

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

}
