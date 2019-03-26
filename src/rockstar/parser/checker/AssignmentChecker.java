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
import rockstar.statement.AssignmentStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class AssignmentChecker extends Checker {

    @Override
    public Statement check() {
        if (match("Let", 2, "be", 1) || match("Put", 1, "into", 2) || match(2, "thinks", 1)) {
            Expression varExpr = ExpressionFactory.getExpressionFor(getResult()[2], line);
            Expression valueExpr = ExpressionFactory.getExpressionFor(getResult()[1], line, varExpr);
            if (varExpr != null && valueExpr != null) {
                if (varExpr instanceof VariableReference) {
                    return new AssignmentStatement((VariableReference) varExpr, valueExpr);
                } else if (varExpr instanceof ReferenceExpression) {
                    return new AssignmentStatement((ReferenceExpression) varExpr, valueExpr);
                }
            }
        }
        return null;
    }

}
