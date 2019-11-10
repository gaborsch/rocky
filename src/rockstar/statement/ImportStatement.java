/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.LinkedList;
import java.util.List;
import rockstar.runtime.BlockContext;
import rockstar.runtime.FileContext;
import rockstar.runtime.PackagePath;
import rockstar.runtime.ProgramContext;
import rockstar.runtime.QualifiedClassName;

/**
 *
 * @author Gabor
 */
public class ImportStatement extends Statement {

    private PackagePath path;
    private final List<String> names;

    public ImportStatement(PackagePath path, List<String> names) {
        this.path = path;
        this.names = new LinkedList<>(names);
    }
    
    @Override
    public void execute(BlockContext ctx) {
        FileContext fileCtx = ctx.getFileCtx();
        ProgramContext root = ctx.getRootCtx();
        for (String name : names) {
            QualifiedClassName qcn = new QualifiedClassName(path, name);
            fileCtx.defineImport(name, qcn);
            root.importClass(qcn);
        }
        
    }

    @Override
    protected String explain() {
        StringBuilder sb = new StringBuilder();
        sb.append("from ")
                .append(path)
                .append(" import ");
// TODO classes        
        return sb.toString();
    }
    
}
