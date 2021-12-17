/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import static rockstar.parser.checker.Checker.PlaceholderType.VARIABLE_OR_QUALIFIER;
import rockstar.statement.AssignmentStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class AssignmentChecker extends Checker<Expression, Expression, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Let", VARIABLE_OR_QUALIFIER.at(2), "be", expressionAt(1).withDefaultExprAt(2)),
        new ParamList("Put", expressionAt(1), "into", VARIABLE_OR_QUALIFIER.at(2)),
        new ParamList("Put", expressionAt(1), "in", VARIABLE_OR_QUALIFIER.at(2)),
        new ParamList(VARIABLE_OR_QUALIFIER.at(2), "=", expressionAt(1)),
        new ParamList(VARIABLE_OR_QUALIFIER.at(2), "thinks", expressionAt(1))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        Expression varExpr = getE2();
        Expression valueExpr = getE1();
        return new AssignmentStatement(varExpr, valueExpr);
    }

}
