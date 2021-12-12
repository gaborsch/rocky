/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.FileContext;
import rockstar.runtime.PackagePath;
import rockstar.runtime.RockstarRuntimeException;

/**
 *
 * @author Gabor
 */
public class PkgDefStatement extends Statement {

    private final PackagePath path;

    public PkgDefStatement(PackagePath path) {
        this.path = path;
    }

    @Override
    public void execute(BlockContext ctx) {
        if (ctx instanceof FileContext) {
            FileContext fc = (FileContext) ctx;
            fc.setPackagePath(path);
        } else {
            throw new RockstarRuntimeException("Package can be set on file level only: " + path);
        }
    }

    @Override
    protected String explain() {
        return "print " + path;
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return ASTValues.of(path.toString());
    }
}
