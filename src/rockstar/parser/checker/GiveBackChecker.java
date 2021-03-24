/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.Arrays;
import java.util.List;
import rockstar.expression.Expression;
import rockstar.statement.ReturnStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class GiveBackChecker extends Checker<Expression, Object, Object> {

    private static final List<String> GIVE_BACK = Arrays.asList("give", "back");
    private static final List<String> SEND_BACK = Arrays.asList("send", "back");

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(GIVE_BACK, expressionAt(1)),
        new ParamList("give", expressionAt(1), "back"),
        new ParamList(SEND_BACK, expressionAt(1)),
        new ParamList("send", expressionAt(1), "back")};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        Expression expression = getE1();
        return new ReturnStatement(expression);
    }

}
