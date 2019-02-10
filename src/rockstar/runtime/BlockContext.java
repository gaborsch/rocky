/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import rockstar.Rockstar;
import rockstar.parser.Line;
import rockstar.statement.FunctionBlock;
import rockstar.statement.Statement;

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
    private final Map<String, Object> env;

    private final StringWriter log;

    public BlockContext(BufferedReader input, PrintStream output, PrintStream error, Map<String, Object> env) {
        this.parent = null;
        this.root = this;
        this.input = input;
        this.output = output;
        this.error = error;
        this.env = env;
        this.log = new StringWriter();
    }

    /**
     * Context initialization
     *
     * @param parent
     */
    public BlockContext(BlockContext parent) {
        this.parent = parent;
        this.root = parent.root;
        this.input = parent.input;
        this.output = parent.output;
        this.error = parent.error;
        this.env = parent.env;
        this.log = parent.log;
    }

    public boolean isGlobalContext() {
        return this == parent;
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

    public Map<String, Object> getEnv() {
        return env;
    }

    public String getLogString() {
        return log.toString();
    }
    
    /**
     * Set a variable value in the proper context
     * @param name
     * @param value 
     */
    public void setVariable(String name, Value value) {
        if (this.vars.containsKey(name)) {
            // if it is already defined locally, set it locallz
            setLocalVariable(name, value);
        } else if (root.vars.containsKey(name)) {
            // if it is already defined globally, set in the root context
            root.setLocalVariable(name, value);
        }
        // define locally or globally, whichecer context we are in
        setLocalVariable(name, value);
    }

    /**
     * Set a variable in the local context, hiding global variables (e.g. function parameters)
     * @param name
     * @param value 
     */
    public void setLocalVariable(String name, Value value) {
        vars.put(name, value);
    }

    /**
     * Get a variable value, either from local or from global context
     * @param name
     * @return 
     */
    public Value getVariableValue(String name) {
        // a variable is either local or global
        Value v = this.vars.get(name);
        if(v == null) { 
            v = root.vars.get(name);
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

//    private BlockContext findVariableContext(String name) {
////        return root;
//        if (vars.containsKey(name)) {
//            return this;
//        }
//        return (parent == this || parent == null) ? null : parent.findVariableContext(name);
//    }

    public FunctionBlock retrieveFunction(String name) {
        return root.funcs.get(name);
    }

    public void defineFunction(String name, FunctionBlock function) {
        root.funcs.put(name, function);
    }

}
