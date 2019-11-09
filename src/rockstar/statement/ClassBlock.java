/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.LinkedList;
import java.util.List;
import rockstar.runtime.BlockContext;
import rockstar.runtime.QualifiedClassName;
import rockstar.runtime.RockObject;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class ClassBlock extends Block {
    private final String name;
    private final String parentName;
    
    private QualifiedClassName qualifiedName;
    private QualifiedClassName qualifiedParentName;
    
    private ClassBlock parentClass;
    private List<String> abstractMethodNames = new LinkedList<>();

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

    public List<String> getAbstractMethodNames() {
        return abstractMethodNames;
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
            qualifiedParentName = ctx.findClass(parentName);
            if (qualifiedParentName == null) {
                throw new RockstarRuntimeException("Can't find parent class "+parentName);
            }
            parentClass = ctx.retrieveClass(qualifiedParentName);
        }
        qualifiedName = new QualifiedClassName(ctx.getPackagePath(), name);
        // define current class in the context
        ctx.defineClass(qualifiedName, this);
        // collect abstract methods, based on superclass abstract methods
        abstractMethodNames = new LinkedList<>();
        if (parentClass != null) {
            abstractMethodNames.addAll(parentClass.abstractMethodNames);
        }
        getStatements().forEach(statement -> {
            if (statement instanceof FunctionBlock) {
                FunctionBlock function = (FunctionBlock)statement;
                if (function.isAbstract()) {
                    abstractMethodNames.add(function.getName());
                } else {
                    abstractMethodNames.remove(function.getName());
                }
            }
        });
    }
    
    public boolean isAbstract() {
        return ! abstractMethodNames.isEmpty();
    }

    /**
     * Instantiate a class
     * @param ctx
     * @param ctorParams
     * @return 
     */
    public Value instantiate(BlockContext ctx, List<Value> ctorParams) {
        if (isAbstract()) {
            throw new RockstarRuntimeException("Cannot instantiate abstract class " + qualifiedName);
        }
        
        RockObject instance = create(ctx);

        // call the constructor
        FunctionBlock constructor = instance.getConstructor();
        if (constructor != null) {
            constructor.call(instance, ctorParams);
        }
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


    @Override
    protected String explain() {
        return "class " + name + (parentName == null ? "" : " extends " + parentName);
    }

}
