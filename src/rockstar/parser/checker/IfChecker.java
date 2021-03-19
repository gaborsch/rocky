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

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("If", 1),
        new ParamList("When", 1)};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        Expression condition = ExpressionFactory.getExpressionFor(get1(), line, block);
        if (condition != null) {
            return new IfStatement(condition);
        }
        return null;
    }

}
