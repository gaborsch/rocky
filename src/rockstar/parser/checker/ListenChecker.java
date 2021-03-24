/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.Arrays;
import java.util.List;
import rockstar.expression.VariableReference;
import rockstar.statement.InputStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class ListenChecker extends Checker<VariableReference, Object, Object> {

    public static final List<String> LISTEN_TO = Arrays.asList("Listen", "to");

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Listen"),
        new ParamList(LISTEN_TO, variableAt(1))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference varRef = getE1();
        if (varRef == null) {
            return new InputStatement();
        }
        return new InputStatement(varRef);
    }

}
