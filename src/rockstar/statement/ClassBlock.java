/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

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
     * Instantiate a class
     * @param ctx
     * @param ctorParams
     * @return 
     */
    public Value instantiate(BlockContext ctx, List<Value> ctorParams) {
        RockObject instance = create(ctx);

        // call the constructor
        FunctionBlock constructor = instance.getConstructor();
        if (constructor != null) {
            constructor.call(instance, ctorParams);
        }
        // TODO parent constructor?
        Value v = Value.getValue(instance);
        return v;
    }

    /**
     * Create and initialize an instance
     * @param ctx
     * @return 
     */
    private RockObject create(BlockContext ctx) {
        RockObject instance;
        if (this.parentClass == null) {
            // base object
            instance = new RockObject(ctx, this);
        } else {
            // inherited object is created first
            RockObject parentInstance = this.parentClass.create(ctx);
            // then create the next layer
            instance = new RockObject(parentInstance, this);
        }
        
        // initialize the variables
        super.execute(instance);
        
        return instance;
    }

//    /**
//     * instantiate a class
//     *
//     * @param ctx Context for execution
//     * @param ctorParams constructor parameters
//     * @return
//     */
//    public Value instantiate(BlockContext ctx, List<Value> ctorParams) {
//        // Objects are created in the root context
//        BlockContext rootCtx = ctx.getRoot();
//        // Create the instance
//        RockObject instance = new RockObject(rootCtx, this);
//        // iniitialize the instance
//        initialize(instance);
//        // call the constructor
//        FunctionBlock constructor = instance.getConstructor();
//        if (constructor != null) {
//            constructor.call(instance, ctorParams);
//        }
//        // TODO parent constructor?
//        Value v = Value.getValue(instance);
//        return v;
//    }

//    protected void initialize(RockObject instance) {
//        if (parentClass != null) {
//            // initilize parent class
//            parentClass.initialize(instance);
//        }
//        // define local variables and functions
//        super.execute(instance);
//    }

    @Override
    protected String explain() {
        return "class " + name + (parentName == null ? "" : " extends " + parentName);
    }

}
