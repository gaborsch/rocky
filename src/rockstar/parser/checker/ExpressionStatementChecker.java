/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.expression.FunctionCall;
import rockstar.expression.QualifierExpression;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.ExpressionStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class ExpressionStatementChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(1)};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        try {
            Expression expression = ExpressionFactory.getExpressionFor(get1(), line, block);
            if (((expression != null) && (expression instanceof FunctionCall)) || (expression instanceof QualifierExpression)) {
                return new ExpressionStatement(expression);
            }
        } catch (Exception e) {
            // if expession is not parsed properly, we must continue
        }
        return null;
    }

}
