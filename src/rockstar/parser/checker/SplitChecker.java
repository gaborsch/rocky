/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.MutationExpression;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.SplitStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class SplitChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("split", 1),
        new ParamList("cut", 1),
        new ParamList("shatter", 1)};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        MutationExpression expr = ExpressionFactory.tryMutationExpressionFor(get1(), line, block);
        if (expr != null) {
            return new SplitStatement(expr);
        }
        return null;
    }

}
