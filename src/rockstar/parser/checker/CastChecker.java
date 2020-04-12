/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.expression.MutationExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.CastStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class CastChecker extends Checker {

    @Override
    public Statement check() {
        if (match("cast", 1) || match("burn", 1)) {
            MutationExpression expr = ExpressionFactory.tryMutationExpressionFor(getResult()[1], line);
            if (expr != null) {
                return new CastStatement(expr);
            }
        }
        return null;
    }

}
