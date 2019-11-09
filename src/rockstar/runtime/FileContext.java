/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

/**
 *
 * @author Gabor
 */
public class FileContext extends BlockContext {

    private PackagePath packagePath = null;

    public FileContext(BlockContext parent, String ctxName) {
        super(parent, ctxName);
    }

    protected FileContext(Environment env) {
        super(env);
        this.packagePath = PackagePath.DEFAULT;
    }

    public void setPackagePath(PackagePath packagePath) {
        this.packagePath = packagePath;
    }

    @Override
    public PackagePath getPackagePath() {
        return packagePath;
    }
    
    

}
