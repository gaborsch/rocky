package rockstar;

import rockstar.statement.Program;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import rockstar.parser.Parser;
import rockstar.parser.StatementPrinter;

/**
 *
 * @author Gabor
 */
public class Rockstar {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String filename = "programs/fizzbuzz.rock";
//        String filename = "programs/fizzbuzz_minimalist.rock";
        try {
            // TODO code application logic here
            // System.out.println("Hello Rocky!");
            System.out.println("File found.");
            Program prg = new Parser(filename).parse();
            
            System.out.println("File parsed: ");
            PrintStream out = System.out;
            new StatementPrinter().print(prg, out);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Rockstar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
