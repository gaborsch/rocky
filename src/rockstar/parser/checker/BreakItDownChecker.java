/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.statement.BreakStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class BreakItDownChecker extends Checker {
    
    @Override
    public Statement check() {
        if (match("Break", "it", "down") || match("Break")) {
            return new BreakStatement();
        }
        return null;
    }
    
}
