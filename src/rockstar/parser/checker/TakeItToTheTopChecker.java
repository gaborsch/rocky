/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.Arrays;
import java.util.List;
import rockstar.statement.ContinueStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class TakeItToTheTopChecker extends Checker {

    private static final List<String> TAKE_IT_TO_THE_TOP = Arrays.asList("Take", "it", "to", "the", "top");

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(TAKE_IT_TO_THE_TOP),
        new ParamList("Continue")};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        return new ContinueStatement();
    }

}
