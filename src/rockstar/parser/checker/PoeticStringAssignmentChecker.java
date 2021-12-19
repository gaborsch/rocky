/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.List;
import rockstar.expression.ConstantExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.Token;
import rockstar.statement.AssignmentStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class PoeticStringAssignmentChecker extends Checker<VariableReference, List<String>, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(variableAt(1), "says", textAt(2).opt()),
        new ParamList(variableAt(1), "say", textAt(2).opt()),
        new ParamList(variableAt(1), "said", textAt(2).opt())};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference varRef = getE1();
        @SuppressWarnings("unchecked")
        String verb = ((List<String>) params.getParams()[1]).get(0);
        String poeticLiteralString = "";
        for (Token token : line.getTokens()) {
            if (token.getValue().toLowerCase().equals(verb)) {
                poeticLiteralString = line.getOrigLine().substring(token.getPos() + token.getLen() + 1);
                break;
            }
        }
        ConstantExpression value = new ConstantExpression(poeticLiteralString);
        return new AssignmentStatement(varRef, value);
    }

}
