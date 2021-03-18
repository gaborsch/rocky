/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.Statement;
import rockstar.statement.WhileStatement;

/**
 *
 * @author Gabor
 */
public class UntilChecker extends Checker {
    
    private static final ParamList[] PARAM_LIST
            = new ParamList[]{
                new ParamList()};

    @Override
    public Statement check() {
        if (match("Until", 1)) {
            Expression condition = ExpressionFactory.getExpressionFor(getResult()[1], line, block);
            if (condition != null) {
                return new WhileStatement(condition, true);
            }
        }
        return null;
    }
    
}
