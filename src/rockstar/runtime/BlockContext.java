/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
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
    
    private final ProgramContext rootCtx;
    private final FileContext fileCtx;
    private final RockObject thisObjectCtx;
    
    private final int level;
    
    protected final Map<String, Value> vars = new HashMap<>();
    protected final Map<String, FunctionBlock> funcs = new HashMap<>();
    
    private final Environment env;
    
    private final String ctxName;

    /**
     * Root context initialization
     *
     * @param env
     */
    protected BlockContext(Environment env) {
        this.env = env;
        this.parent = null;
        this.rootCtx = (ProgramContext) this;
        this.thisObjectCtx = null;
        this.level = 0;
        this.ctxName = "RockStar";
        this.fileCtx = null;
    }

    /**
     * Context initialization for functions
     *
     * @param parent
     * @param ctxName name of the context
     */
    public BlockContext(BlockContext parent, String ctxName) {
        // Normally we increase the level
        this(parent, ctxName, true);
    }

    /**
     * Context initialization for functions
     *
     * @param parent
     * @param ctxName name of the context
     * @param increaseLevel
     */
    public BlockContext(BlockContext parent, String ctxName, boolean increaseLevel) {
        this.env = parent.env;
        this.parent = parent;
        this.rootCtx = parent.rootCtx;
        this.fileCtx = (this instanceof FileContext) ? (FileContext) this : parent.fileCtx;
        this.thisObjectCtx = (this instanceof RockObject) ? (RockObject) this : parent.thisObjectCtx;
        this.level = parent.level + (increaseLevel ? 1 : 0);
        this.ctxName = ctxName;
    }
    
    public Environment getEnv() {
        return env;
    }
    
    public Map<String, Value> getVariables() {
        return vars;
    }
    
    public Map<String, FunctionBlock> getFunctions() {
        return funcs;
    }
    
    public String getName() {
        if (this.parent == null) {
            return ctxName;
        }
        if (this.thisObjectCtx != null) {
            return ctxName + "@" + thisObjectCtx.getName() + " L" + this.level;
        }
        return ctxName + " L" + this.level;
    }

    /**
     * The package path is set on files
     *
     * @return
     */
    public PackagePath getPackagePath() {
        return fileCtx.getPackagePath();
    }
    
    public BlockContext getParent() {
        return parent;
    }
    
    public ProgramContext getRootCtx() {
        return rootCtx;
    }
    
    public FileContext getFileCtx() {
        return fileCtx;
    }
    
    public Optional<RockObject> getThisObjectCtx() {
        return Optional.ofNullable(thisObjectCtx);
    }
    
    public int getLevel() {
        return level;
    }

    // last assigned variable name in this context
    private VariableReference lastVariableRef = null;
    
    public VariableReference getLastVariableRef() {
        BlockContext lastvarCtx = getContextFor(this, ctx -> lastVariableRef != null);
        if (lastvarCtx != null) {
            return lastvarCtx.lastVariableRef;
        } else {
            throw new RockstarRuntimeException("invalid reference to last variable");
        }
    }

    /**
     * Set a variable value in the proper context
     *
     * @param vref
     * @param value
     */
    public void setVariable(VariableReference vref, Value value) {
        // Find out the effective variable reference
        VariableReference effectiveVref = vref.getEffectiveVref(this);
        
        doSetVariable(effectiveVref, value);

        // last assigned variable name, if wasn't "it" reference
        if (vref == effectiveVref) {
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
        // determine if we are in object context
        // find the defining RockObject within thisObjectCtx, if present
        BlockContext objCtx = null;
        if (this.thisObjectCtx != null) {
            int thisId = thisObjectCtx.getObjId();
            objCtx = getContextFor(thisObjectCtx,
                    ctx -> (ctx instanceof RockObject)
                    && (((RockObject) ctx).getObjId() == thisId)
                    && (ctx.vars.containsKey(vref.getName()))
            );
        }
        
        String variableName = vref.getName();

        // we can set either local or global variables
        if (this.vars.containsKey(variableName)) {
            // overwrite local variable
            setLocalVariable(vref, value);
        } else if (objCtx != null) {
            // overwrite object member variable
            objCtx.setLocalVariable(vref, value);
        } else if (rootCtx.vars.containsKey(variableName)) {
            // overwrite global variable
            rootCtx.setLocalVariable(vref, value);
        } else {
            // initialize local variable
            setLocalVariable(vref, value);
        }
    }

    /**
     * Find a context in the hierarchy for the given condition
     *
     * @param startCtx
     * @param condition
     * @return
     */
    protected BlockContext getContextFor(BlockContext startCtx, Predicate<BlockContext> condition) {
        BlockContext ctx = startCtx;
        while (ctx != null && !condition.test(ctx)) {
            ctx = ctx.getParent();
        }
        return ctx;
    }

    /**
     * Set a variable in the local context, hiding global variables (e.g.
     * function parameters)
     *
     * @param vref
     * @param value
     */
    public void setLocalVariable(VariableReference vref, Value value) {
        vars.put(vref.getName(), value);
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
        String vname = vref.getEffectiveVref(this).getName();
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
        return getContextFor(this, ctx -> ctx.funcs.containsKey(name));
    }
    
    public QualifiedClassName findClass(String name) {
        // if we have an import, use that
        QualifiedClassName aliasQcn = fileCtx.getQualifiedClassNameByAlias(name);
        if (aliasQcn != null) {
            return aliasQcn;
        }
        // otherwise use the context package
        QualifiedClassName defaultQcn = new QualifiedClassName(getPackagePath(), name);
        return defaultQcn;
    }
    
    public void beforeStatement(Statement stmt) {
        if (env.getListener() != null) {
            env.getListener().beforeStatement(this, stmt);
        }
    }
    
    public void beforeExpression(Expression exp) {
        if (env.getListener() != null) {
            env.getListener().beforeExpression(this, exp);
        }
    }
    
    public Value afterExpression(Expression exp, Value v) {
        if (env.getListener() != null) {
            env.getListener().afterExpression(this, exp, v);
        }
        return v;
    }
    
}
