/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.ConstantExpression;
import rockstar.expression.VariableReference;
import rockstar.statement.RockStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class RockPoeticChecker extends Checker<VariableReference, ConstantExpression, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Rock", variableAt(1), "like", poeticLiteralAt(2)),
        new ParamList("Push", variableAt(1), "like", poeticLiteralAt(2))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference varRef = getE1();
        ConstantExpression constValue = getE2();
        return new RockStatement(varRef, constValue);
    }

}
