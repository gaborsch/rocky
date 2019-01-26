/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;

/**
 *
 * @author Gabor
 */
public class ElseStatement extends Block implements ContinuingBlockStatementI {

    @Override
    boolean applyTo(Block block) {
        List<Statement> blockStmts = block.getStatements();
        if (blockStmts.size() >= 1) {
            Statement lastStmt = blockStmts.get(blockStmts.size()-1);
            if(lastStmt instanceof IfStatement) {
                return true;
            }
        }
        return false;
    }

//    @Override
//    public boolean applyBlock(Block finishedBlock) {
//        if (finishedBlock instanceof IfStatement) {
//            return true;
//        } 
//        return false;
//    }
    
    
 
}
