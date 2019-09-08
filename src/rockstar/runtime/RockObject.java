/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import rockstar.statement.ClassBlock;

/**
 *
 * @author Gabor
 */
public class RockObject extends BlockContext {

    private static int objIdSeq = 1;

    private final ClassBlock classBlock;
    private final int objId;

    public RockObject(BlockContext rootCtx, ClassBlock classBlock) {
        // object instances can access the root context
        super(rootCtx, classBlock.getName());
        this.classBlock = classBlock;
        this.objId = objIdSeq++;
    }

    @Override
    public String toString() {
        return classBlock.getName() + "#" + objId;
    }
    
    
}
