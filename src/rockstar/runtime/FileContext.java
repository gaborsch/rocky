/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.HashMap;
import java.util.Map;
import rockstar.expression.VariableReference;
import rockstar.statement.FunctionBlock;

/**
 *
 * @author Gabor
 */
public class FileContext extends BlockContext {

    protected final Map<String, QualifiedClassName> imports = new HashMap<>();
    private PackagePath packagePath = PackagePath.DEFAULT;

    public FileContext(BlockContext parent, String ctxName) {
        super(parent.getRootCtx(), ctxName, false);
    }

    public FileContext(Environment env) {
        super(new ProgramContext(env), "RockStar", false);
    }

    public void setPackagePath(PackagePath packagePath) {
        this.packagePath = packagePath;
    }

    @Override
    public PackagePath getPackagePath() {
        return packagePath;
    }

    public void defineImport(String alias, QualifiedClassName qcn) {
        imports.put(alias, qcn);
    }

    protected QualifiedClassName getQualifiedClassNameByAlias(String alias) {
        return imports.get(alias);
    }

    @Override
    public void setLocalVariable(VariableReference vref, Value value) {
        // FileContext behaves like if it was the Root context
        getRootCtx().setLocalVariable(vref, value);
    }

    @Override
    public void defineFunction(String name, FunctionBlock function) {
        // FileContext behaves like if it was the Root context    
        getRootCtx().defineFunction(name, function);
    }

    @Override
    public String toString() {
        return getName();
    }
    
    

}
