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
import java.util.Map;

/**
 *
 * @author Gabor
 */
public class Environment {

    private final BufferedReader input;
    private final PrintStream output;
    private final PrintStream error;
    private final Map<String, String> env;

    private BlockContextListener listener = null;

    public Environment(InputStream input, PrintStream output, PrintStream error, Map<String, String> env) {
        this.output = output;
        this.error = error;
        this.env = env;
        InputStreamReader rdr = null;
        try {
            rdr = new InputStreamReader(input, Utils.UTF8);
        } catch (UnsupportedEncodingException ex) {
        }
        this.input = (rdr == null) ? null : new BufferedReader(rdr);
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

    public String getParameter(String key) {
        return env.get(key);
    }

    public void setListener(BlockContextListener listener) {
        this.listener = listener;
    }

    public BlockContextListener getListener() {
        return listener;
    }
    
    

}
