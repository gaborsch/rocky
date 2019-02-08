/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.runtime.RockstarReturnException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class FunctionBlock extends Block {

    private final String name;
    private final List<String> parameterNames = new ArrayList<>();

    public FunctionBlock(String name) {
        this.name = name;
    }

    public void addParameterName(String paramName) {
        parameterNames.add(paramName);
    }

    public String getName() {
        return name;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    @Override
    public String toString() {
        return super.toString()
                + "\n    FUNCDEF: " + name + "(" + Arrays.deepToString(parameterNames.toArray()) + ")";
    }

    /**
     * Define a function
     *
     * @param ctx
     */
    @Override
    public void execute(BlockContext ctx) {
        // define function
        ctx.defineFunction(name, this);
    }

    /**
     * Execute a function call
     *
     * @param ctx Context for execution
     * @param values function parameters
     * @return
     */
    public Value call(BlockContext ctx, List<Value> values) {
        BlockContext funcCtx = new BlockContext(ctx);
        List<String> names = this.parameterNames;
        if (names.size() != values.size()) {
            throw new RockstarRuntimeException("Wrong number of arguments for function " + this.name + ": expected " + names.size() + ", got " + values.size());
        }

        for (int i = 0; i < values.size(); i++) {
            funcCtx.setLocalVariable(names.get(i), values.get(i));
        }

        try {
            // execute the function body
            super.execute(funcCtx);
        } catch (RockstarReturnException retExp) {
            return retExp.getReturnValue();
        }
        // return value is set by
        return Value.MYSTERIOUS;
    }

    @Override
    public String explain(BlockContext ctx) {
        return "FUNCDEF: " + name + "(" + Arrays.deepToString(parameterNames.toArray()) + ")";
    }

    @Override
    protected String list() {
        return "function " + name + ": " + Arrays.deepToString(parameterNames.toArray());
    }

    
}
