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
    public boolean appendTo(Block finishedBlock) {
        if (finishedBlock instanceof IfStatement) {
            // 'else' block following an 'if' block
            ((IfStatement) finishedBlock).setElseStatement(this);
            return false;
        } else if (finishedBlock instanceof ElseStatement) {
            // 'else' block following another 'else' block: must belong to the enclosing 'if' block
            Block parentBlock = finishedBlock.getParent();
            if (parentBlock != null && parentBlock instanceof IfStatement) {
                ((IfStatement) parentBlock).setElseStatement(this);
                // must close the parent block
                return true;
            }
//        } else { // strict mode: ELSE only after IF
//            throw new ParseException("Else statement after " + finishedBlock.getClass().getSimpleName(), getLine());
        }
        return false;
    }

    @Override
    public void execute(BlockContext ctx) {
        // ELSE statement is skipped in normal execution flow
    }

    /**
     * Executes an Else block.
     *
     * @param ctx
     */
    public void executeElse(BlockContext ctx) {
        super.execute(ctx);
    }

}
