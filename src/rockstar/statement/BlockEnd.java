/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.runtime.ASTAware;
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
    protected String explain() {
        return "";
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return null;
    }

}
