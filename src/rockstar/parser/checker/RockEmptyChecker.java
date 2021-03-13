/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.ConstantExpression;
import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.AssignmentStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class RockEmptyChecker extends Checker {

    @Override
    public Statement check() {
        if (match("Rock", 1) || match("Push", 1)) {
            Expression varExpr = ExpressionFactory.getExpressionFor(getResult()[1], line, block);
            if (varExpr != null && varExpr instanceof VariableReference) {
                return new AssignmentStatement(varExpr, ConstantExpression.CONST_EMPTY_ARRAY);
            }
        }
        return null;
    }

}
