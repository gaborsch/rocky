/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.statement.Statement;
import rockstar.statement.WhileStatement;

/**
 *
 * @author Gabor
 */
public class UntilChecker extends Checker<Expression, Object, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Until", expressionAt(1))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        Expression condition = getE1();
        return new WhileStatement(condition, true);
    }

}
