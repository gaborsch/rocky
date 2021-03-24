/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.List;
import rockstar.expression.VariableReference;
import rockstar.statement.IncrementStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class BuildUpChecker extends Checker<VariableReference, List<String>, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Build", variableAt(1), "up", textAt(2).opt())};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference varRef = getE1();
        List<String> text = getE2();
        int count = 1;
        if (text != null) {
            boolean isAndPossible = true;
            for (String s : text) {
                if ("up".equals(s)) {
                    count++;
                    isAndPossible = true;
                } else if (isAndPossible && s.equals(",")) {
                    isAndPossible = false;
                } else {
                    return null;
                }
            }
        }
        return new IncrementStatement(varRef, count);
    }

}
