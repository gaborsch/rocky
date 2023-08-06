/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.LinkedList;
import java.util.List;
import rockstar.runtime.*;

/**
 *
 * @author Gabor
 */
public class ClassBlock extends Block {

    final String name;
    final String parentName;

    private QualifiedClassName qualifiedName;
    private QualifiedClassName qualifiedParentName;

    private FileContext definingContext;

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
                throw new RockstarRuntimeException("Can't find parent class " + parentName);
            }
            parentClass = ctx.getRootCtx().retrieveClass(qualifiedParentName);
        }
        this.qualifiedName = new QualifiedClassName(ctx.getPackagePath(), name);
        this.definingContext = ctx.getFileCtx();
        // define current class in the context
        ctx.getRootCtx().defineClass(qualifiedName, this);
        // collect abstract methods, based on superclass abstract methods
        abstractMethodNames = new LinkedList<>();
        if (parentClass != null) {
            abstractMethodNames.addAll(parentClass.abstractMethodNames);
        }
        getStatements().forEach(statement -> {
            if (statement instanceof FunctionBlock) {
                FunctionBlock function = (FunctionBlock) statement;
                if (function.isAbstract()) {
                    abstractMethodNames.add(function.getName());
                } else {
                    abstractMethodNames.remove(function.getName());
                }
            }
        });
    }

    public boolean isAbstract() {
        return !abstractMethodNames.isEmpty();
    }

    /**
     * Instantiate a class
     *
     * @param ctorParams
     * @return
     */
    public Value instantiate(List<Value> ctorParams) {
        if (isAbstract()) {
            throw new RockstarRuntimeException("Cannot instantiate abstract class " + qualifiedName);
        }

        RockObject instance = create(definingContext);

        // call the constructor
        FunctionBlock constructor = instance.getConstructor(ctorParams);
        if (constructor != null) {
            constructor.call(instance, ctorParams);
        }
        Value v = Value.getValue(instance);
        return v;
    }

    /**
     * Create and initialize an instance
     *
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
    public String getASTNodeText() {
        return "Class " + name + (parentName == null ? "" : " extends " + parentName);
    }
    
    @Override
    public void accept(StatementVisitor visitor) {
    	visitor.visit(this);
    }

}
