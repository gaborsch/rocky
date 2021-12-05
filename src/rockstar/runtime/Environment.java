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
    private final Map<String, String> options;

    private BlockContextListener listener = null;

    private static Environment instance = null;

    private Environment(InputStream input, PrintStream output, PrintStream error, Map<String, String> options) {
        this.output = output;
        this.error = error;
        this.options = options;
        InputStreamReader rdr = null;
        try {
            rdr = (input == null)
                    ? null
                    : new InputStreamReader(input, Utils.UTF8);
        } catch (UnsupportedEncodingException ex) {
        }
        this.input = (rdr == null) ? null : new BufferedReader(rdr);
    }

    public static synchronized Environment get() {
        return instance;
    }

    public static synchronized Environment create(InputStream input, PrintStream output, PrintStream error, Map<String, String> options) {
        instance = new Environment(input, output, error, options);
        return instance;
    }

    public static synchronized Environment forOptions(Map<String, String> options) {
        return new Environment(null, null, null, options);
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

    public Map<String, String> getOptions() {
        return options;
    }

    public String getOptionValue(String key) {
        return options.get(key);
    }

    public boolean hasOption(String shortOpt, String longOpt) {
        return options.containsKey(shortOpt) || options.containsKey(longOpt);
    }

    public boolean hasOption(String opt) {
        return options.containsKey(opt);
    }

    public boolean isStrictMode() {
        return hasOption("-S", "--strict");
    }

    public void setListener(BlockContextListener listener) {
        this.listener = listener;
    }

    public BlockContextListener getListener() {
        return listener;
    }

}
