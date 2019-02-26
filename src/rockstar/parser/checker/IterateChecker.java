/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.Statement;
import rockstar.statement.IterateStatement;

/**
 *
 * @author Gabor
 */
public class IterateChecker extends Checker {
    
    @Override
    public Statement check() {
        if (match("While", 1, "as", 2)
             || match("While", 1, "alike", 2)) {
            Expression arrayExpr = ExpressionFactory.getExpressionFor(getResult()[1], line);
            if (arrayExpr != null) {
                Expression asExpr = ExpressionFactory.getExpressionFor(getResult()[2], line);
                if (asExpr != null) {
                    return new IterateStatement(arrayExpr, asExpr);
                }
            }
        }
        return null;
    }
    
}
