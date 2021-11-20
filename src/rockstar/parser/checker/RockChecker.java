/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.statement.RockStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class RockChecker extends Checker<VariableReference, Expression, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Rock", variableAt(1), "with", expressionAt(2)),
        new ParamList("Push", variableAt(1), "with", expressionAt(2)),
        new ParamList("Rock", expressionAt(2), "into", variableAt(1)),
        new ParamList("Push", expressionAt(2), "into", variableAt(1))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference varExpr = getE1();
        Expression valueExpr = getE2();
        return new RockStatement((VariableReference) varExpr, valueExpr);
    }
}
