/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.DecrementStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class KnockDownChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Knock", 1, "down", 2)};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference varRef = ExpressionFactory.tryVariableReferenceFor(get1(), line, block);
        if (varRef != null) {
            int count = 1;
            boolean isAndPossible = true;
            for (String s : get2()) {
                if ("down".equals(s)) {
                    count++;
                    isAndPossible = true;
                } else if (isAndPossible && s.equals(",")) {
                    isAndPossible = false;
                } else {
                    return null;
                }
            }
            return new DecrementStatement(varRef, count);
        }
        return null;
    }

}
