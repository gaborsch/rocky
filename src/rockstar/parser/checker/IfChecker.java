/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.IfStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class IfChecker extends Checker {
    
    @Override
    public Statement check() {
        if (match("If", 1) || match("When", 1)) {
            Expression condition = ExpressionFactory.getExpressionFor(getResult()[1], line);
            if (condition != null) {
                return new IfStatement(condition);
            }
        }
        return null;
    }
    
}
