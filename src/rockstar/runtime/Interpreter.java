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

    private BlockContext ctx;

    public Interpreter(InputStream input, PrintStream output, PrintStream error, Map<String, Object> env) {
        ctx = new BlockContext(input, output, error, env);
    }

    public void execute(Program prg) {
        executeBlock(prg);
//        output.println("Running " + prg.getName().substring(prg.getName().lastIndexOf('\\')+1));
    }

    public void executeBlock(Block block) {
        ctx = new BlockContext(ctx);
        List<Statement> stmtList = block.getStatements();

        for (Statement statement : stmtList) {
            statement.execute(ctx);
        }

        ctx = ctx.getParent();
    }

}
