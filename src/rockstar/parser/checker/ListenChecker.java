/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.List;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.InputStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class ListenChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Listen", 1)};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        List<String> rest = get1();
        if (rest.isEmpty()) {
            return new InputStatement();
        }
        if (rest.size() >= 2) {
            if (rest.get(0).equals("to")) {
                VariableReference varRef = ExpressionFactory.tryVariableReferenceFor(rest.subList(1, rest.size()), line, block);
                if (varRef != null) {
                    return new InputStatement(varRef);
                }
            }
        }
        return null;
    }

}
