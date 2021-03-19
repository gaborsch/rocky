/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.IncrementStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class BuildUpChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Build", 1, "up", 2)};

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
                if ("up".equals(s)) {
                    count++;
                    isAndPossible = true;
                } else if (isAndPossible && s.equals(",")) {
                    isAndPossible = false;
                } else {
                    return null;
                }
            }
            return new IncrementStatement(varRef, count);
        }
        return null;
    }

}
