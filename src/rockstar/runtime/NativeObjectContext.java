/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import rockstar.statement.FunctionBlock;

/**
 *
 * @author Gabor
 */
public class NativeObjectContext extends BlockContext {

    private static int objIdSeq = 1;
    
    private final NativeObject nativeObject;
    private final int objId;

    /**
     * Constructor for base object
     *
     * @param rootCtx
     * @param classBlock
     */
    public NativeObjectContext(BlockContext rootCtx, NativeObject nativeObject) {
        super(rootCtx, nativeObject.getNativeClass().getName());
        this.nativeObject = nativeObject;
        this.objId = objIdSeq++;
    }
    
    public NativeObject getNativeObject() {
		return nativeObject;
	}

    public int getObjId() {
        return objId;
    }

    @Override
    public String getName() {
        return super.getName() + "##" + objId;
    }

    @Override
    public void defineFunction(String name, FunctionBlock function) {
    }

    /**
     * Defines the visit order: first the subcontexts, then the parent contexts
     *
     * @return
     */
    @Override
    public BlockContext getParent() {
        return this;
    }

    @Override
    public String toString() {
        return getName();
    }
    
}
