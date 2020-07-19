/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.runtime.BlockContext;

/**
 *
 * @author Gabor
 */
public class ElseStatement extends Block implements ContinuingBlockStatementI {

    @Override
    public void appendTo(Block finishedBlock) {
        if (finishedBlock instanceof IfStatement) {
            ((IfStatement) finishedBlock).setElseStatement(this);
//        } else { // strict mode: ELSE only after IF
//            throw new ParseException("Else statement after " + finishedBlock.getClass().getSimpleName(), getLine());
        }
    }

    @Override
    public void execute(BlockContext ctx) {
        // ELSE statement is skipped in normal execution flow
    }
    /**
     * Executes an Else block. 
     * @param ctx 
     */
    public void executeElse(BlockContext ctx) {
        super.execute(ctx);
    }
    
    @Override
    protected String explain() {
        return "else";
    }
    
}
