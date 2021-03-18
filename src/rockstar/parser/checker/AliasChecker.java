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
    
     private static final ParamList[] PARAM_LIST
            = new ParamList[]{
                new ParamList()};

    @Override
    public Statement check() {
        if (match("for", "me", 1, "means", 2)
            || match(1, "means", 2)) {

            List<String> alias = getResult()[1];
            List<String> keyword = getResult()[2];
            if (!alias.isEmpty() && !keyword.isEmpty()) {
                return new AliasStatement(alias, keyword);
            }
        }
        return null;
    }

}
