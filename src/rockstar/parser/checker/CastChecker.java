/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.MutationExpression;
import rockstar.statement.CastStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class CastChecker extends Checker<MutationExpression, Object, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("cast", mutationExpressionAt(1)),
        new ParamList("burn", mutationExpressionAt(1))
    };

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        MutationExpression expr = getE1();
        return new CastStatement(expr);
    }

}
