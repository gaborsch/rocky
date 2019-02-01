/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.statement.BlockEnd;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class BlockEndChecker extends Checker {
    
    @Override
    public Statement check() {
        if (line.getTokens().isEmpty()) {
            return new BlockEnd();
        }
        return null;
    }
    
}
