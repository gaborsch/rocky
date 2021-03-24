/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.statement.IterateStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class IterateChecker extends Checker<Expression, Expression, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("While", expressionAt(1), "as", at(2, PlaceholderType.VARIABLE_OR_QUALIFIER)),
        new ParamList("While", expressionAt(1), "alike", at(2, PlaceholderType.VARIABLE_OR_QUALIFIER))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        Expression arrayExpr = getE1();
        Expression asExpr = getE2();
        return new IterateStatement(arrayExpr, asExpr);
    }

}
