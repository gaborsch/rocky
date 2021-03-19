/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import rockstar.statement.BreakStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class BreakItDownChecker extends Checker {

    private static final List<String> BREAK_IT_DOWN = Arrays.asList("Break", "it", "down");

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(BREAK_IT_DOWN),
        new ParamList("Break")};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        return new BreakStatement();
    }

}
