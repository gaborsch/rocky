/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.statement.ContinueStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class TakeItToTheTopChecker extends Checker {
    
    private static final ParamList[] PARAM_LIST
            = new ParamList[]{
                new ParamList()};

    @Override
    public Statement check() {
        if (match("Take", "it", "to", "the", "top") || match("Continue")) {
            return new ContinueStatement();
        }
        return null;
    }
    
}
