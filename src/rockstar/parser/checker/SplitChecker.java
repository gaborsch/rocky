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

    private static final ParamList[] PARAM_LIST
            = new ParamList[]{
                new ParamList()};

    @Override
    public Statement check() {
        if (match("split", 1) || match("cut", 1) || match("shatter", 1)) {
            MutationExpression expr = ExpressionFactory.tryMutationExpressionFor(getResult()[1], line, block);
            if (expr != null) {
                return new SplitStatement(expr);
            }
        }
        return null;
    }

}
