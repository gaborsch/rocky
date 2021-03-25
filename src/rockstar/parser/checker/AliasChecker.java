/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.List;
import rockstar.statement.AliasStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class AliasChecker extends Checker<List<String>, List<String>, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("for", "me", textAt(1), "means", textAt(2)),
        new ParamList(textAt(1), "means", textAt(2))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        List<String> alias = getE1();
        List<String> keyword = getE2();
        return new AliasStatement(alias, keyword);
    }

}
