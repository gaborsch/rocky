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
    private final RockObject superObject;

    // The next (extended) level of an object
    private RockObject subObject;

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
        this.superObject = null;
        this.subObject = null;
    }

    /**
     * Constructor for extended objects
     * @param superObject
     * @param classBlock 
     */
    public RockObject(RockObject superObject, ClassBlock classBlock) {
        // object instances can access the root context
        super(superObject.getRoot(), classBlock.getName());
        this.classBlock = classBlock;
        this.objId = superObject.objId;
        this.superObject = superObject;
        this.subObject = null;
        superObject.subObject = this;
    }

    @Override
    public String getName() {
        return classBlock.getName() + "#" + objId + "-" + getLevel();
    }

    public int getObjId() {
        return objId;
    }

    public RockObject getSuperObject() {
        return superObject;
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
        if (superObject != null) {
            return superObject;
        }
        return super.getParent();
    }

    public RockObject getTopObject() {
        RockObject obj = this;
        while(obj.subObject != null) {
            obj = obj.subObject;
        }
        return obj;
    }
    
    public boolean checkInstanceof(String className) {
        RockObject obj = this;
        while (obj != null) {
            if (obj.classBlock.getName().equals(className)) {
                return true;
            }
            obj = obj.superObject;
        }
        return false;
    }

}
