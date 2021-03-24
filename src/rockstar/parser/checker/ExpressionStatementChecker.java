/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.expression.FunctionCall;
import rockstar.expression.QualifierExpression;
import rockstar.statement.ExpressionStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class ExpressionStatementChecker extends Checker<Expression, Object, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(expressionAt(1))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        Expression expression = getE1();
        if ((expression instanceof FunctionCall) || (expression instanceof QualifierExpression)) {
            return new ExpressionStatement(expression);
        }
        return null;
    }

}
