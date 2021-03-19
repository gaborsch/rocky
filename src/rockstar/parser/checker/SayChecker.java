/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.OutputStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class SayChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Say", 1),
        new ParamList("Shout", 1),
        new ParamList("Whisper", 1),
        new ParamList("Scream", 1)};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        Expression expr = ExpressionFactory.getExpressionFor(get1(), line, block);
        if (expr != null) {
            return new OutputStatement(expr);
        }
        return null;
    }

}
