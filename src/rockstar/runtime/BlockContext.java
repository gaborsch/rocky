/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.HashMap;
import java.util.Map;
import rockstar.expression.ConstantValue;
import rockstar.statement.FunctionBlock;

/**
 *
 * @author Gabor
 */
public class BlockContext {

    private final BlockContext parent;
    private final BlockContext root;
    private final Map<String, ConstantValue> vars = new HashMap<>();
    private final Map<String, FunctionBlock> funcs = new HashMap<>();

    /**
     * Context initialization
     *
     * @param parent
     */
    public BlockContext(BlockContext parent) {
        this.parent = parent;
        this.root = parent == null ? this : parent.root;
    }

    public BlockContext getParent() {
        return parent;
    }

    public void setVariable(String name, ConstantValue value) {
        vars.put(name, value);
    }

    public ConstantValue getVariable(String name) {
        ConstantValue value = vars.get(name);
        if (value == null && parent != null) {
            value = parent.getVariable(name);
        }
        return value;
    }

    public FunctionBlock retrieveFunction(String name) {
        return root.funcs.get(name);
    }

    public void defineFunction(String name, FunctionBlock function) {
        root.funcs.put(name,function);
    }

}
