/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.statement.OutputStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class SayChecker extends Checker<Expression, Object, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Say", expressionAt(1)),
        new ParamList("Shout", expressionAt(1)),
        new ParamList("Whisper", expressionAt(1)),
        new ParamList("Scream", expressionAt(1))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        Expression expr = getE1();
        return new OutputStatement(expr);
    }

}
