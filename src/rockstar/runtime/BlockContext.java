/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import rockstar.statement.FunctionBlock;

/**
 *
 * @author Gabor
 */
public class BlockContext {

    private final BlockContext parent;
    private final BlockContext root;
    private final Map<String, Value> vars = new HashMap<>();
    private final Map<String, FunctionBlock> funcs = new HashMap<>();

    private final BufferedReader input;
    private final PrintStream output;
    private final PrintStream error;
    private final Map<String, String> env;
    private String name;

    public BlockContext(InputStream inputstream, PrintStream output, PrintStream error, Map<String, String> env) {
        this.parent = null;
        this.root = this;
        InputStreamReader rdr = null;
        try {
            rdr = new InputStreamReader(inputstream, Utils.UTF8);
        } catch (UnsupportedEncodingException ex) {
        }
        this.input = (rdr == null) ? null : new BufferedReader(rdr);
        this.output = output;
        this.error = error;
        this.env = env;
        this.name = "<RockStar>";
    }

    /**
     * Context initialization
     *
     * @param parent
     * @param name name of the context
     */
    public BlockContext(BlockContext parent, String name) {
        this.parent = parent;
        this.root = parent.root;
        this.input = parent.input;
        this.output = parent.output;
        this.error = parent.error;
        this.env = parent.env;
        this.name = name;
    }

    public BufferedReader getInput() {
        return input;
    }

    public PrintStream getOutput() {
        return output;
    }

    public PrintStream getError() {
        return error;
    }

    public String getEnv(String key) {
        return env.get(key);
    }

    public Map<String, Value> getVariables() {
        return vars;
    }

    public Map<String, FunctionBlock> getFunctions() {
        return funcs;
    }

    /**
     * Set a variable value in the proper context
     *
     * @param name
     * @param value
     */
    public void setVariable(String name, Value value) {
        // find the context where the variable was defined (if possible)
        BlockContext ctx = this;
        // find the first block with the same name
        Map<String, BlockContext> firstBlock = new HashMap<>();
        firstBlock.put(ctx.name, ctx);
        while(ctx != null && !ctx.vars.containsKey(name)) {
            ctx = ctx.parent;
            if (ctx != null && !firstBlock.containsKey(ctx.name)) {
                firstBlock.put(ctx.name, ctx);
            }
        }
        // if we found the variable in a block, save it in the first instance
        if (ctx != null) {
            ctx = firstBlock.get(ctx.name);
            ctx.setLocalVariable(name, value);
        } else {
            // otherwise set it locally, wherever we are now
            setLocalVariable(name, value);
        }
        // we can set either local or global variables
//        boolean hasGlobal = root.vars.containsKey(name);
//        if(this.vars.containsKey(name)) {
//            // overwrite local variable
//            setLocalVariable(name, value);
//        } else if (hasGlobal) {
//            // overwrite global variable
//            root.setLocalVariable(name, value);
//        } else {
//            // initialize local variable
//            setLocalVariable(name, value);
//        }
    }

    /**
     * Set a variable in the local context, hiding global variables (e.g.
     * function parameters)
     *
     * @param name
     * @param value
     */
    public void setLocalVariable(String name, Value value) {
        vars.put(name, value);
    }

    /**
     * Get a variable value, either from local or from global context
     *
     * @param name
     * @return
     */
    public Value getVariableValue(String name) {
        // find the context where the variable was defined 
        Value v = null;
        BlockContext ctx = this;
        while(v == null && ctx != null) {
            v = ctx.vars.get(name);
            ctx = ctx.parent;
        }

        if (v == null) {
            // is it a function reference?
            FunctionBlock f = retrieveFunction(name);
            if (f != null) {
                return Value.BOOLEAN_TRUE;
            }
        }
        return v == null ? Value.MYSTERIOUS : v;
    }

    public FunctionBlock retrieveFunction(String name) {
        FunctionBlock f = null;
        BlockContext ctx = this;
        while(f == null && ctx != null) {
            f = ctx.funcs.get(name);
            ctx = ctx.parent;
        }
        return f;
    }

    public void defineFunction(String name, FunctionBlock function) {
        funcs.put(name, function);
    }

}
