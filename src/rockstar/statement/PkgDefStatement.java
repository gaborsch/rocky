/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.LinkedList;
import rockstar.runtime.BlockContext;
import rockstar.runtime.PackagePath;

/**
 *
 * @author Gabor
 */
public class PkgDefStatement extends Statement {
    
    private PackagePath path;

    public PkgDefStatement(PackagePath path) {
        this.path = path;
    }

    
    @Override
    public void execute(BlockContext ctx) {
        ctx.getEnv().getOutput().println(path);
    }

    @Override
    protected String explain() {
        return "print " + path;
    }
    
}
