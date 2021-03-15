/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.expression.ListExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.RockStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class RockChecker extends Checker {

    @Override
    public Statement check() {
        if (match("Rock", 1, "with", 2)
                || match("Push", 1, "with", 2)
                || match("Rock", 2, "into", 1)
                || match("Push", 2, "into", 1)) {
            Expression varExpr = ExpressionFactory.getExpressionFor(getResult()[1], line, block);
            Expression valueExpr = ExpressionFactory.getExpressionFor(getResult()[2], line, block);
            if (varExpr != null && valueExpr != null) {
                if (varExpr instanceof VariableReference) {
                    return new RockStatement((VariableReference) varExpr, valueExpr);
                }
            }
        }
        if (match("Rock", 1) || match("Push", 1)) {
            Expression varExpr = ExpressionFactory.getExpressionFor(getResult()[1], line, block);
            if (varExpr != null) {
                if(varExpr instanceof VariableReference) {
                    // rock <variable>
                    return new RockStatement((VariableReference) varExpr);
                } else if (varExpr instanceof ListExpression) {
                    ListExpression listExpr = (ListExpression) varExpr;
                    // the first item should be treated as a variable reference, the remaining will be the list
                    varExpr = listExpr.getParameters().remove(0);
                    if (varExpr instanceof VariableReference) {
                    return new RockStatement((VariableReference) varExpr, listExpr);
                }
                }
            }
        }
        return null;
    }

}
