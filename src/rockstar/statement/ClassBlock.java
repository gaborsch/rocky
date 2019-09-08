/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.List;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockObject;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class ClassBlock extends Block {

    private final String name;
    private final String parentName;
    private ClassBlock parentClass;
    private FunctionBlock constructor;

    public ClassBlock(String name, String parentName) {
        this.name = name;
        this.parentName = parentName;
    }

    public String getName() {
        return name;
    }

    public ClassBlock getParentClass() {
        return parentClass;
    }

    public FunctionBlock getConstructor() {
        return constructor;
    }

    /**
     * Define a class
     *
     * @param ctx
     */
    @Override
    public void execute(BlockContext ctx) {
        // bind parent class body
        if (parentName != null) {
            parentClass = ctx.retrieveClass(parentName);
        }
        // define current class in the context
        ctx.defineClass(name, this);
    }

    /**
     * instantiate a class
     *
     * @param ctx Context for execution
     * @param ctorParams constructor parameters
     * @return
     */
    public Value instantiate(BlockContext ctx, List<Value> ctorParams) {
        BlockContext rootCtx = ctx.getRoot();
        RockObject instance = new RockObject(rootCtx, this);
        initialize(instance);
        if (constructor != null) {
            constructor.call(instance, ctorParams);
        }
        Value v = Value.getValue(instance);
        return v;
    }
    
    protected void initialize(RockObject instance) {
        if (parentClass != null) {
            // initilize parent class
            parentClass.initialize(instance);
        }
        // define local variables and functions
        super.execute(instance);
    }

    @Override
    protected String explain() {
        return "class " + name + (parentName == null ? "" : " extends " + parentName);
    }

}
