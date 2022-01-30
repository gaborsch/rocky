/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import rockstar.parser.Parser;
import rockstar.runtime.BlockContextListener;
import rockstar.runtime.Environment;
import rockstar.runtime.FileContext;
import rockstar.runtime.LoggerListener;
import rockstar.statement.Program;

/**
 *
 * @author Gabor
 */
public class RockstarApi {

    private BlockContextListener logger;
    private Environment env;

    public RockstarApi() {
        Map<String, String> options = new HashMap<>();
        env = Environment.create(System.in, System.out, System.err, options);
        logger = new LoggerListener(env.getOptions());
        env.setListener(logger);
    }

    public Map<String, String> getOptions() {
        return env.getOptions();
    }

    public void setOptions(Map<String, String> options) {
        env.getOptions().putAll(options);
        logger = new LoggerListener(env.getOptions());
        env.setListener(logger);
    }

    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        this.env = env;
        logger = new LoggerListener(env.getOptions());
        env.setListener(logger);
    }

    public void run(String filename, String fileContent) {
        Rockstar.setGlobalOptions(env.getOptions());

        FileContext prgCtx = new FileContext(env);
        FileContext ctx;
        try {
            Program prg = new Parser(fileContent, filename, env).parse();
            ctx = new FileContext(prgCtx, filename);
            prg.execute(ctx);
        } catch (RuntimeException re) {
            System.err.println("Error: " + re.getMessage());
        }
    }

    public Program parse(String filename, InputStream fileContent) {
        Rockstar.setGlobalOptions(env.getOptions());
        return new Parser(fileContent, filename, env).parse();
    }

}
