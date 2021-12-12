/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.parser.ParserError;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;

/**
 *
 * @author Gabor
 */
public class StatementError extends Statement {

    private int pos;
    private String msg;

    public StatementError(int pos, String msg) {
        this.pos = pos;
        this.msg = msg;
    }

    @Override
    public void execute(BlockContext ctx) {
        throw new RockstarRuntimeException("Executing invalid statement on line " + getLine().getLnum());
    }

    @Override
    protected String explain() {
        return "!!! Error on position " + (pos + 1) + ": " + msg;
    }

    @Override
    boolean applyTo(Block block) {
        while (block != null && !(block instanceof Program)) {
            block = block.getParent();
        }
        if (block != null && block instanceof Program) {
            Program prg = (Program) block;
            prg.addError(new ParserError(this.getLine(), this.pos, this.msg));
        }
        return true;
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return ASTValues.of(getLine().getOrigLine());
    }
}
