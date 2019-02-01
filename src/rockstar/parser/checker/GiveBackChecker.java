/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.ReturnStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class GiveBackChecker extends Checker {
    
    @Override
    public Statement check() {
        if (match("Give", "back", 1)) {
            Expression expression = ExpressionFactory.getExpressionFor(getResult()[1], line);
            if (expression != null) {
                return new ReturnStatement(expression);
            }
        }
        return null;
    }
    
}
