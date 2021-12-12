/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.LinkedList;
import java.util.List;
import rockstar.runtime.ASTAware;
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

    private final PackagePath path;
    private final List<String> names;

    public ImportStatement(PackagePath path, List<String> names) {
        this.path = path;
        this.names = new LinkedList<>(names);
    }

    @Override
    public void execute(BlockContext ctx) {
        FileContext fileCtx = ctx.getFileCtx();
        ProgramContext root = ctx.getRootCtx();
        // the path is either given, or comes from the file, or the default package
        PackagePath p = this.path;
        p = (p == null) ? fileCtx.getPackagePath() : p;
        p = (p == null) ? PackagePath.DEFAULT : p;

        for (String name : names) {
            QualifiedClassName qcn = new QualifiedClassName(p, name);
            fileCtx.defineImport(name, qcn);
            root.importClass(qcn);
        }

    }

    @Override
    public List<ASTAware> getASTChildren() {
        List<ASTAware> astValues = ASTValues.of(path.toString());
        astValues.addAll(ASTValues.of((String[]) names.toArray()));
        return astValues;
    }

}
