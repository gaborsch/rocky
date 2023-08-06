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
public class BlockEnd extends Statement {

    @Override
    public void execute(BlockContext ctx) {
    }
    
    @Override
    public void accept(StatementVisitor visitor) {
    	visitor.visit(this);
    }

}
