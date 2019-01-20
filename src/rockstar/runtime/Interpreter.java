/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import rockstar.statement.Block;
import rockstar.statement.Program;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class Interpreter {

    private final InputStream input;
    private final PrintStream output;
    private final PrintStream error;
    private final Map<String, Object> env;

    private BlockContext ctx;

    public Interpreter(InputStream input, PrintStream output, PrintStream error, Map<String, Object> env) {
        this.input = input;
        this.output = output;
        this.error = error;
        this.env = env;
    }

    public void execute(Program prg) {
        executeBlock(prg);
    }
    
    public void executeBlock(Block block) {
        ctx = new BlockContext(ctx);
        List<Statement> stmtList = block.getStatements();
        
        ctx = ctx.getParent();
    }

}
