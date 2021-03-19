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
public class AliasChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("for", "me", 1, "means", 2),
        new ParamList(1, "means", 2)};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        List<String> alias = get1();
        List<String> keyword = get2();
        if (!alias.isEmpty() && !keyword.isEmpty()) {
            return new AliasStatement(alias, keyword);
        }
        return null;
    }

}
