/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.parser.Line;
import rockstar.runtime.BlockContext;

/**
 *
 * @author Gabor
 */
public abstract class Statement {

    private Line line;

    protected Statement() {
    }

    public final Line getLine() {
        return line;
    }

    @Override
    public String toString() {
        return explain();
    }

    public void setDebugInfo(Line line) {
        this.line = line;
    }

    /**
     * This statement is applied to the block. Statements could implement
     * specific checks if they can be applied to the block or not
     *
     * @param block
     */
    boolean applyTo(Block block) {
        return true;
    }

    /**
     * Execute the statement within the given context
     *
     * @param ctx
     */
    public abstract void execute(BlockContext ctx);

    protected abstract String explain();

}
