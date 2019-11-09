/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

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
    
    protected final Map<String, Value> vars = new HashMap<>();
    protected final Map<String, FunctionBlock> funcs = new HashMap<>();
    protected final Map<QualifiedClassName, ClassBlock> classes = new HashMap<>();
    protected final Map<String, QualifiedClassName> imports = new HashMap<>();
    
    private final Environment env;

    private PackagePath packagePath = null;
    
    private final String ctxName;

    /**
     * Root context initialization
     * @param env 
     */
    public BlockContext(Environment env) {
        this.parent = null;
        this.root = this;
        this.env = env;
        
        this.ctxName = "RockStar";
    }

    /**
     * Context initialization for functions
     *
     * @param parent
     * @param ctxName name of the context
     */
    public BlockContext(BlockContext parent, String ctxName) {
        this.parent = parent;
        this.root = parent.root;
        this.level = parent.level + 1;
        this.env = parent.env;
        this.ctxName = ctxName;
    }

    /**
     * Context initialization for sub-objects
     *
     * @param parentObj
     * @param ctxName name of the context
     */
    protected BlockContext(RockObject parentObj, String ctxName) {
        BlockContext parentCtx = parentObj;
        this.parent = parentCtx;
        this.root = parentCtx.root;
        // sub-objects do not increase level!
        this.level = parentCtx.level;
        this.env = parentCtx.env;
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

    public Map<QualifiedClassName, ClassBlock> getClasses() {
        return classes;
    }

    public String getName() {
        if (this.parent == null) {
            return ctxName;
        }
        RockObject obj = getObjectContext();
        if (obj != null) {
            return ctxName + "@" + obj.getName() + " L" + this.level;
        }
        return ctxName + " L" + this.level;
    }

    public PackagePath getPackagePath() {
        return (packagePath == null) ? PackagePath.DEFAULT : packagePath;
    }

    public void setPackagePath(PackagePath packagePath) {
        this.packagePath = packagePath;
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
                    return null;
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
    
    public void defineImport(String alias, QualifiedClassName qcn) {
        imports.put(alias, qcn);
    }

    public void defineClass(QualifiedClassName qcn, ClassBlock classBlock) {
        this.classes.put(qcn, classBlock);
    }

    public QualifiedClassName findClass(String name) {
        // if we have an import, use that
        QualifiedClassName aliasQcn = imports.get(name);
        if (retrieveClass(aliasQcn) != null) {
            return aliasQcn;
        }
        // otherwise use the context package
        QualifiedClassName defaultQcn = new QualifiedClassName(getPackagePath(), name);
        if (retrieveClass(defaultQcn) != null) {
            return defaultQcn;
        }
        return null;
    }

    public ClassBlock retrieveClass(QualifiedClassName qcn) {
        if (qcn == null) {
            return null;
        }
        ClassBlock c = null;
        BlockContext ctx = this;
        while (c == null && ctx != null) {
            c = ctx.classes.get(qcn);
            ctx = ctx.getParent();
        }
        return c;
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
