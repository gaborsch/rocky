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
public class WhileChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("While", 1)};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        Expression condition = ExpressionFactory.getExpressionFor(get1(), line, block);
        if (condition != null) {
            return new WhileStatement(condition);
        }
        return null;
    }

}
