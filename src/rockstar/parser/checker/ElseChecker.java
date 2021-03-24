/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.statement.ElseStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class ElseChecker extends Checker<Object, Object, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Else"),
        new ParamList("Otherwise")};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        return new ElseStatement();
    }

}
