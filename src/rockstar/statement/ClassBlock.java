/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.LinkedList;
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
    private final ClassBlock parentClass;
    private FunctionBlock constructor;
    private final List<FunctionBlock> methods = new ArrayList<>();

    public ClassBlock(String name, ClassBlock parentClass) {
        this.name = name;
        this.parentClass = parentClass;
        if (parentClass != null) {
            // all methods are inherited
            methods.addAll(parentClass.getMethods());
        }
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

    public void addMethod(FunctionBlock method) {
        if (method.getName().equals(name)) {
            // constructor
            this.constructor = method;
        } else {
            // normal method
            methods.add(method);
        }
    }

    public List<FunctionBlock> getMethods() {
        return methods;
    }

    /**
     * Define a class
     *
     * @param ctx
     */
    @Override
    public void execute(BlockContext ctx) {
        // define function
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
        RockObject instance = new RockObject(this);
        if (constructor != null) {
            constructor.call(instance, ctorParams);
        }
        Value v = Value.getValue(instance);
        return v;
    }

    @Override
    protected String explain() {
        return "class " + name + (parentClass == null ? "" : " extends " + parentClass.getName());
    }

}
