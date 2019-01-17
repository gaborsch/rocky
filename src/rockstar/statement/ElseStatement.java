/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

/**
 *
 * @author Gabor
 */
public class ElseStatement extends Block implements ContinuingBlockStatementI {

    @Override
    public boolean applyBlock(Block finishedBlock) {
        if (finishedBlock instanceof IfStatement) {
            return true;
        } 
        return false;
    }
 
}
