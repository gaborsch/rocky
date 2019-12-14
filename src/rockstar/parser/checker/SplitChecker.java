/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.expression.QualifierExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.SplitStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class SplitChecker extends Checker {

    @Override
    public Statement check() {
        if (match("Split", 1, "with", 2, "into", 3) // 0: targeted, with separator
                || match("Split", 1, "into", 3, "with", 2) // 1: targeted, with separator
                || match("Split", 1, "with", 2) // 2: in-place, with separator
                || match("Split", 1, "into", 3) // 3: targeted, separatorless
                || match("Split", 1)) {                     // 4: in-place, separatorless

            // Value first
            Expression valueExpr = ExpressionFactory.getExpressionFor(getResult()[1], line);
            if (valueExpr != null) {
                int matchIdx = getMatchCounter() - 1;

                // target check
                Expression targetReference;
                if (matchIdx == 0 || matchIdx == 1 || matchIdx == 3) {
                    // if targeted, check if the target reference is variable or array ref
                    Expression targetExpr = ExpressionFactory.getExpressionFor(getResult()[3], line);
                    if (targetExpr == null
                            || !(targetExpr instanceof VariableReference
                            || targetExpr instanceof QualifierExpression)) {
                        return null;
                    }
                    targetReference = targetExpr;
                } else {
                    // if no target, the value must be variable or array ref
                    if (!(valueExpr instanceof VariableReference)
                            || valueExpr instanceof QualifierExpression) {
                        return null;
                    }
                    // use the default target (the value expr)
                    targetReference = null;
                }

                // separator check
                Expression separatorExpr;
                if (matchIdx == 0 || matchIdx == 1 || matchIdx == 2) {
                    // if has separator, check separator reference
                    separatorExpr = ExpressionFactory.getExpressionFor(getResult()[2], line);
                    if (separatorExpr == null) {
                        return null;
                    }
                } else {
                    // if separatorless, use the default separator
                    separatorExpr = null;
                }
                return new SplitStatement(valueExpr, separatorExpr, targetReference);
            }
        }
        return null;
    }

}
