/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.ArrayList;
import java.util.List;
import rockstar.statement.BreakStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class BreakItDownChecker extends Checker {
    
    private static final List<String> BREAK_IT_DOWN = new ArrayList<String>();
    
    static {
        BREAK_IT_DOWN.add("Break");
        BREAK_IT_DOWN.add("it");
        BREAK_IT_DOWN.add("down");
    }
    
    @Override
    public Statement check() {
        if (match(BREAK_IT_DOWN) || match("Break")) {
            return new BreakStatement();
        }
        return null;
    }
    
}
