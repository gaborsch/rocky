package rockstar;

import java.io.BufferedReader;
import rockstar.statement.Program;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import rockstar.parser.Parser;
import rockstar.runtime.BlockContext;
import rockstar.test.RockstarTest;

/**
 *
 * @author Gabor
 */
public class Rockstar {

    public static boolean DEBUG = false;

    public static void main(String[] args) {
        RockstarTest.main(args);
    }

    /**
     * @param args the command line arguments
     */
    public static void main1(String[] args) {
        String filename = "C:\\work\\rocky\\rocky1\\rocky\\programs\\tests\\correct\\operators\\equalityComparison.rock";
//        String filename = "programs/tests/correct/factorial.rock";
//        String filename = "programs/tests/correct/operators/andTest.rock";
//        String filename = "programs/tests/parse-errors/elseOutsideIf.rock";
//        String filename = "programs/tests/runtime-errors/andTest.rock";
        System.out.println("Filename:" + filename);
        Map<String, Object> env = new HashMap<>();
        Rockstar rockstar = new Rockstar(System.in, System.out, System.err, env);
        rockstar.run(filename);
    }

    private final BufferedReader input;
    private final PrintStream output;
    private final PrintStream error;
    private final Map<String, Object> env;

    private final BlockContext ctx;

    public Rockstar(InputStream inputstream, PrintStream output, PrintStream error, Map<String, Object> env) {
        this.input = new BufferedReader(new InputStreamReader(inputstream));
        this.output = output;
        this.error = error;
        this.env = env;
        ctx = new BlockContext(this.input, this.output, this.error, this.env);
    }

    public Program run(String filename) {
        Program prg = null;
        try {
            // TODO code application logic here
            prg = new Parser(filename).parse();
            
            // list the parsed program
//            System.out.println(prg.list(0));

            prg.execute(ctx);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Rockstar.class.getName()).log(Level.SEVERE, null, ex);
        }
        return prg;
    }

    public String getLogString() {
        return ctx.getLogString();
    }

}
