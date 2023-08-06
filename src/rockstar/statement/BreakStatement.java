/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Environment;
import rockstar.runtime.RockstarBreakException;

/**
 *
 * @author Gabor
 */
public class BreakStatement extends Statement {

	@Override
	boolean applyTo(Block block) {
		if (Environment.get().isStrictMode()) {
			Block b = block;
			while (b != null) {
				if (b instanceof WhileStatement) {
					return true;
				}
				b = b.getParent();
			}
			return false;
		} else {
			return true;
		}
	}

    @Override
    public void execute(BlockContext ctx) {
		if (Environment.get().isStrictMode()) {
			throw new RockstarBreakException();
		}
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return null;
    }
    
    @Override
    public void accept(StatementVisitor visitor) {
    	visitor.visit(this);
    }

}
