/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.MutationExpression;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.JoinStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class JoinChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("join", 1),
        new ParamList("unite", 1)};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        MutationExpression expr = ExpressionFactory.tryMutationExpressionFor(get1(), line, block);
        if (expr != null) {
            return new JoinStatement(expr);
        }
        return null;
    }

}
