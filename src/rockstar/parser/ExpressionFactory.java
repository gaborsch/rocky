/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser;

import java.util.List;
import rockstar.expression.ConstantExpression;
import rockstar.expression.ExpressionError;
import rockstar.expression.Expression;
import rockstar.expression.IntoExpression;
import rockstar.expression.MutationExpression;
import rockstar.expression.VariableReference;
import rockstar.expression.WithExpression;
import rockstar.runtime.RockNumber;
import rockstar.statement.Block;

/**
 *
 * @author Gabor
 */
public class ExpressionFactory {

    /**
     * Parse an expression, throw exception if failed
     *
     * @param tokens the tokens to parse
     * @param line the Line
     * @param block
     * @return
     */
    public static Expression getExpressionFor(List<String> tokens, Line line, Block block) {
        return getExpressionFor(tokens, line, null, block);
    }

    /**
     * Parse an expression, throw exception if failed
     *
     * @param tokens the tokens to parse
     * @param line the Line
     * @param defaultExpr
     * @param block
     * @return
     */
    public static Expression getExpressionFor(List<String> tokens, Line line, Expression defaultExpr, Block block) {
        Expression parsed = new ExpressionParser(tokens, line, block)
                .parse(defaultExpr);
        if (parsed != null) {
            return parsed;
        }
        return new ExpressionError(tokens, line);
    }

    /**
     * Parses a simple expression (literal or variable reference)
     *
     * @param list
     * @param line
     * @param block
     * @return
     */
    public static Expression tryExpressionFor(List<String> list, Line line, Block block) {
        return tryExpressionFor(list, line, null, block);
    }

    /**
     * Parses a simple expression (literal or variable reference)
     *
     * @param list
     * @param line
     * @param defaultExpr
     * @param block
     * @return
     */
    public static Expression tryExpressionFor(List<String> list, Line line, Expression defaultExpr, Block block) {
        ExpressionParser parser = new ExpressionParser(list, line, block);
        Expression expr = parser.parse(defaultExpr);
        if (expr != null && parser.isFullyParsed()) {
            // has valid value and parsed through the list
            return expr;
        }
        return null;
    }    
    /**
     * Try a variable reference, returns null if failed
     *
     * @param list
     * @param line
     * @param block
     * @return
     */
    public static VariableReference tryVariableReferenceFor(List<String> list, Line line, Block block) {
        ExpressionParser parser = new ExpressionParser(list, line, block);
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
     * @param block
     * @return
     */
    public static ConstantExpression tryLiteralFor(List<String> list, Line line, Block block) {
        ExpressionParser parser = new ExpressionParser(list, line, block);
        ConstantExpression literal = parser.parseLiteral();
        if (literal != null && parser.isFullyParsed()) {
            // has valid value and parsed through the list
            return literal;
        }
        return null;
    }
    
    /**
     * Try to parse a MutationExpression, returns null if failed
     * @param list
     * @param line
     * @param block
     * @return 
     */
    public static MutationExpression tryMutationExpressionFor(List<String> list, Line line, Block block) {
        ExpressionParser parser = new ExpressionParser(list, line, block);
        Expression expr = parser.parse(null);
        if (expr != null && parser.isFullyParsed()) {
            // has valid value and parsed through the list
            if (expr instanceof VariableReference) {
                return new MutationExpression((VariableReference) expr);
            } else if (expr instanceof IntoExpression) {
                return new MutationExpression((IntoExpression) expr);
            } else if (expr instanceof WithExpression) {
                return new MutationExpression((WithExpression) expr);
            }
            // invalid setup
        }
        return null;
    }

    /**
     * Parses a poetic number literal
     *
     * @param list
     * @param line
     * @param orig
     * @param block
     * @return
     */
    public static ConstantExpression getPoeticLiteralFor(List<String> list, Line line, String orig, Block block) {
        // if a literal word like "nothing", then use that
        ConstantExpression literal = tryLiteralFor(list, line, block);
        if (literal != null) {
            return literal;
        }

        // parse the orig String
        int digit = 0;
        StringBuilder sb = new StringBuilder();

        int pos = 0;
        boolean inComment = false;
        while (pos <= orig.length()) {
            char c = (pos < orig.length()) ? orig.charAt(pos) : ' ';
            if (!inComment) {
                // letters and hyphens are poetic text
                if (Character.isLetter(c) || c == '-') {
                    digit++;
                } else if (c == '.' || c == ' ' || c == '(') {
                    if (digit > 0) {
                        sb.append((char) ('0' + (digit % 10)));
                    }
                    if (c == '.') {
                        sb.append(c);
                    }
                    digit = 0;
                    if (c == '(') {
                        inComment = true;
                    }
                }
            } else {
                // in comment
                if (c == ')') {
                    inComment = false;
                }
            }

            pos++;
        }
        // parse the concatenated string
        return new ConstantExpression(RockNumber.parse(sb.toString()));
    }
}
