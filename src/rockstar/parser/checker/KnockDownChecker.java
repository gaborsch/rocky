/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.List;
import rockstar.expression.VariableReference;
import rockstar.parser.Token;
import rockstar.statement.DecrementStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class KnockDownChecker extends Checker<VariableReference, List<Token>, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Knock", variableAt(1), "down", textAt(2).opt())};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference varRef = getE1();
        List<Token> text = getE2();
        int count = 1;
        if (text != null) {
            boolean isAndPossible = true;
            for (Token t : text) {
                if ("down".equals(t.getValue())) {
                    count++;
                    isAndPossible = true;
                } else if (isAndPossible && t.getValue().equals(",")) {
                    isAndPossible = false;
                } else {
                    return null;
                }
            }
        }
        return new DecrementStatement(varRef, count);
    }

}
