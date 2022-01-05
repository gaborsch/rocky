/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.Stack;
import rockstar.statement.Block;
import rockstar.statement.ContinuingBlockStatementI;
import rockstar.statement.FunctionBlock;

/**
 *
 * @author Gabor
 */
public class BlockStack extends Stack<Block> {
	private static final long serialVersionUID = -2826489946713232467L;

	private final boolean isStrictMode;

    public BlockStack(Environment env) {
        this.isStrictMode = env.isStrictMode();
    }

    public Block removeBlock() {
        Block stmt = null;
        if (size() > 1) {
            stmt = pop();
            if (isStrictMode && stmt instanceof ContinuingBlockStatementI) {
                if (peek() instanceof FunctionBlock) {
                    stmt = pop();
                }
            }
        }
        return stmt;
    }

}
