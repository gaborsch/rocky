/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import rockstar.statement.ClassBlock;
import rockstar.statement.FunctionBlock;

/**
 *
 * @author Gabor
 */
public class RockObject extends BlockContext {

    private static int objIdSeq = 1;

    private final ClassBlock classBlock;
    private FunctionBlock constructor;
    private final int objId;
    
    // the previous (inherited) level of an object
    private final RockObject subContext;

    /**
     * Constructor for base object
     * @param rootCtx
     * @param classBlock 
     */
    public RockObject(BlockContext rootCtx, ClassBlock classBlock) {
        // object instances can access the root context
        super(rootCtx, classBlock.getName());
        this.classBlock = classBlock;
        this.objId = objIdSeq++;
        this.subContext = null;
    }

    /**
     * Constructor for extended objects
     * @param subCtx
     * @param classBlock 
     */
    public RockObject(RockObject subCtx, ClassBlock classBlock) {
        // object instances can access the root context
        super(subCtx.getRoot(), classBlock.getName());
        this.classBlock = classBlock;
        this.objId = subCtx.objId;
        this.subContext = subCtx;
    }

    @Override
    public String getName() {
        return classBlock.getName() + "#" + objId + "-" + getLevel();
    }
    
    @Override
    public String toString() {
        return classBlock.getName() + "#" + objId;
    }

    @Override
    public void defineFunction(String name, FunctionBlock function) {
        if (name.equals(classBlock.getName())) {
            // constructor
            constructor = function;
        } else {
            super.defineFunction(name, function);
        }
    }

    public FunctionBlock getConstructor() {
        return constructor;
    }

    /**
     * Defines the visit order: first the subcontexts, then the parent contexts
     * @return 
     */
    @Override
    public BlockContext getParent() {
        if (subContext != null) {
            return subContext;
        }
        return super.getParent();
    }
    
    
    
}
