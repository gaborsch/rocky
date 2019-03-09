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
import rockstar.expression.VariableReference;
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
    private final String ctxName;

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
        this.ctxName = "RockStar";
    }

    /**
     * Context initialization
     *
     * @param parent
     * @param ctxName name of the context
     */
    public BlockContext(BlockContext parent, String ctxName) {
        this.parent = parent;
        this.root = parent.root;
        this.level = parent.level + 1;
        this.input = parent.input;
        this.output = parent.output;
        this.error = parent.error;
        this.env = parent.env;
        this.listener = parent.listener;
        this.ctxName = ctxName + "#" + this.level;
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
        return ctxName;
    }

    public BlockContext getParent() {
        return parent;
    }

    public int getLevel() {
        return level;
    }

    // last assigned variable name in this context
    private VariableReference lastVariableRef = null;

    public VariableReference getLastVariableRef() {
        return lastVariableRef;
    }

    /**
     * Set a variable value in the proper context
     *
     * @param vref
     * @param value
     */
    public void setVariable(VariableReference vref, Value value) {
        // we can set either local or global variables
        boolean hasGlobal = root.vars.containsKey(vref.getName(this));
        if (this.vars.containsKey(vref.getName(this))) {
            // overwrite local variable
            setLocalVariable(vref, value);
        } else if (hasGlobal) {
            // overwrite global variable
            root.setLocalVariable(vref, value);
        } else {
            // initialize local variable
            setLocalVariable(vref, value);
        }

        // last assigned variable name
        if (!vref.isLastVariable()) {
            this.lastVariableRef = vref;
        }
    }

    /**
     * Set a variable in the local context, hiding global variables (e.g.
     * function parameters)
     *
     * @param vref
     * @param value
     */
    public void setLocalVariable(VariableReference vref, Value value) {
        vars.put(vref.getName(this), value);

    }

    /**
     * Get a variable value, either from local or from global context
     *
     * @param vref
     * @return
     */
        public Value getVariableValue(VariableReference vref) {
        // find the context where the variable was defined 
        Value v = null;
        String vname = vref.getName(this);
        BlockContext ctx = this;
        while (v == null && ctx != null) {
            v = ctx.vars.get(vname);
            ctx = ctx.parent;
        }

        if (v == null) {
            // is it a function reference?
            FunctionBlock f = retrieveFunction(vname);
            if (f != null) {
                return Value.BOOLEAN_TRUE;
            }
        }

        return v == null ? Value.MYSTERIOUS : v;
    }

    public FunctionBlock retrieveFunction(String name) {
        FunctionBlock f = null;
        BlockContext ctx = this;
        while (f == null && ctx != null) {
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
