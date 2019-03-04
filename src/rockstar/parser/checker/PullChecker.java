/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.expression.ReferenceExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.PullStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class PullChecker extends Checker {

    @Override
    public Statement check() {
        if (match("Pull", 1, "from", 2) || match("Roll", 1, "into", 2) ) {
            VariableReference valueExpr = ExpressionFactory.tryVariableReferenceFor(getResult()[1], line);
            VariableReference varExpr = ExpressionFactory.tryVariableReferenceFor(getResult()[2], line);
            if (varExpr != null && valueExpr != null) {
                return new PullStatement(varExpr, valueExpr);
            }
        }
        return null;
    }

}
