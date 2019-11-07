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
import java.util.function.Predicate;
import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.statement.ClassBlock;
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
    private final Map<String, ClassBlock> classes = new HashMap<>();

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
        this.ctxName = ctxName;
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

    public Map<String, ClassBlock> getClasses() {
        return classes;
    }

    public String getName() {
        if (this.parent == null) {
            return ctxName;
        }
        RockObject obj = getObjectContext();
        if (obj != null) {
            return ctxName + "@" + obj.getName();
        }
        return ctxName + " L" + this.level;
    }

    public BlockContext getParent() {
        return parent;
    }

    public BlockContext getRoot() {
        return root;
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
        doSetVariable(vref, value);
        // last assigned variable name
        if (!vref.isLastVariable()) {
            this.lastVariableRef = vref;
        }
    }

    /**
     * Set a variable value in the proper context
     *
     * @param vref
     * @param value
     */
    private void doSetVariable(VariableReference vref, Value value) {

        final String variableName = vref.getName(this);
        BlockContext objCtx = getObjectContext(
                ctx -> ctx.vars.containsKey(vref.getName(this))
        );

        // we can set either local or global variables
        if (this.vars.containsKey(variableName)) {
            // overwrite local variable
            setLocalVariable(vref, value);
        } else if (objCtx != null) {
            // overwrite object member variable
            objCtx.setLocalVariable(vref, value);
        } else if (root.vars.containsKey(variableName)) {
            // overwrite global variable
            root.setLocalVariable(vref, value);
        } else {
            // initialize local variable
            setLocalVariable(vref, value);
        }
    }

    public RockObject getObjectContext() {
        // return the first object context
        return getObjectContext(ctx -> true);
    }

    private RockObject getObjectContext(Predicate<BlockContext> condition) {
        BlockContext objCtx = this;
        // find the nearest object context, if exists
        RockObject enclosingObject = null;
        while (objCtx != null) {
            if (objCtx instanceof RockObject) {
                final RockObject rockObjCtx = (RockObject) objCtx;
                if (enclosingObject == null) {
                    // find the enclosing RockObject
                    enclosingObject = rockObjCtx;
                } else if (enclosingObject.getObjId() != rockObjCtx.getObjId()) {
                    // if the current object is different than the first, we quit (parent fields cannot be set)
                    objCtx = null;
                    break;
                }
                if (condition.test(objCtx)) {
                    // if we found, use this context
                    return (RockObject) objCtx;
                }
            }
            objCtx = objCtx.getParent();
        }
        return null;
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
            ctx = ctx.getParent();
        }

        return v;
    }

    public void defineFunction(String name, FunctionBlock function) {
        funcs.put(name, function);
    }

    public FunctionBlock retrieveLocalFunction(String name) {
        return funcs.get(name);
    }

    public BlockContext getContextForFunction(String name) {
        BlockContext ctx = this;
        while (ctx != null && !ctx.funcs.containsKey(name)) {
            ctx = ctx.getParent();
        }
        return ctx;
    }

    public void defineClass(String name, ClassBlock classBlock) {
        this.classes.put(classBlock.getName(), classBlock);
    }

    public ClassBlock retrieveClass(String name) {
        ClassBlock c = null;
        BlockContext ctx = this;
        while (c == null && ctx != null) {
            c = ctx.classes.get(name);
            ctx = ctx.getParent();
        }
        return c;
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
