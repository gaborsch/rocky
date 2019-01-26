package rockstar;

import rockstar.statement.Program;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import rockstar.parser.Parser;
import rockstar.parser.StatementPrinter;
import rockstar.runtime.Interpreter;
import rockstar.test.RockstarTest;

/**
 *
 * @author Gabor
 */
public class Rockstar {

    public static void main(String[] args) {
        RockstarTest.main(args);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main1(String[] args) {
        String filename = "programs/t.rock";
//        String filename = "programs/tests/correct/factorial.rock";
//        String filename = "programs/tests/correct/operators/andTest.rock";
//        String filename = "programs/tests/parse-errors/elseOutsideIf.rock";
//        String filename = "programs/tests/runtime-errors/andTest.rock";
        System.out.println("Filename:" + filename);
        Map<String, Object> env = new HashMap<>();
        Rockstar rockstar = new Rockstar(System.in, System.out, System.err, env);
        rockstar.run(filename);
    }


    private final InputStream input;
    private final PrintStream output;
    private final PrintStream error;
    private final Map<String, Object> env;

    private final Interpreter interpreter;

    public Rockstar(InputStream input, PrintStream output, PrintStream error, Map<String, Object> env) {
        this.input = input;
        this.output = output;
        this.error = error;
        this.env = env;
        interpreter = new Interpreter(input, output, error, env);
    }

    public void run(String filename) {
        try {
            // TODO code application logic here
            Program prg = new Parser(filename).parse();
            if (true) {
                error.println("File parsed: ");
                new StatementPrinter().print(prg, error);
            }
            interpreter.execute(prg);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Rockstar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
