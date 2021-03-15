/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar;

import java.io.FileNotFoundException;
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
    
    private Map<String,String> options;
    private Environment env;

    public RockstarApi() {
        options = new HashMap<>();
        BlockContextListener logger = new LoggerListener(options);
        env = new Environment(System.in, System.out, System.err, options);
        env.setListener(logger);
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }

    
    public void run(String filename, String fileContent) {
        Rockstar.setGlobalOptions(options);

        FileContext prgCtx = new FileContext(env);
        FileContext ctx;
        try {
            Program prg = new Parser(fileContent, filename).parse();
            ctx = new FileContext(prgCtx, filename);
            prg.execute(ctx);
        } catch (RuntimeException re) {
            System.err.println("Error: " + re.getMessage());
        }
    }
    
    public static void main(String[] args) {
        String program = "Say \"hello world!\"";
        new RockstarApi().run("RockstarApi", program);        
    }
    
}
