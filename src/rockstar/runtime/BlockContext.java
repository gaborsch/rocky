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
import rockstar.expression.Expression;
import rockstar.statement.FunctionBlock;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class BlockContext {

    private final BlockContext parent;
    private final BlockContext root;
    private int level = 0;
    private final Map<String, Value> vars = new HashMap<>();
    private final Map<String, FunctionBlock> funcs = new HashMap<>();

    private final BufferedReader input;
    private final PrintStream output;
    private final PrintStream error;
    private final Map<String, String> env;
    private final String name;
    
    private BlockContextListener listener = null;

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
        this.name = "RockStar";
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
        this.level = parent.level + 1;
        this.input = parent.input;
        this.output = parent.output;
        this.error = parent.error;
        this.env = parent.env;
        this.listener = parent.listener;
        this.name = name + "#" + this.level;
    }

    public void setListener(BlockContextListener listener) {
        this.listener = listener;
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

    public String getName() {
        return name;
    }
    
    public BlockContext getParent() {
        return parent;
    }

    public int getLevel() {
        return level;
    }
    
    // last assigned variable name in this context
    private String lastVariableName = null;

    public String getLastVariableName() {
        return lastVariableName;
    }
    
    /**
     * Set a variable value in the proper context
     *
     * @param name
     * @param value
     */
    public void setVariable(String name, Value value) {
        // we can set either local or global variables
        boolean hasGlobal = root.vars.containsKey(name);
        if(this.vars.containsKey(name)) {
            // overwrite local variable
            setLocalVariable(name, value);
        } else if (hasGlobal) {
            // overwrite global variable
            root.setLocalVariable(name, value);
        } else {
            // initialize local variable
            setLocalVariable(name, value);
        }
        
        // last assigned variable name
        this.lastVariableName = name;
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
    
    public void beforeStatement(Statement stmt) {
        if (listener != null) {
            listener.beforeStatement(this, stmt);
        }
    }

    public void beforeExpression(Expression exp) {
        if (listener != null) {
            listener.beforeExpression(this, exp);
        }
    }

    public Value afterExpression(Expression exp, Value v) {
        if (listener != null) {
            listener.afterExpression(this, exp, v);
        }
        return v;
    }

}
