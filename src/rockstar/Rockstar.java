package rockstar;

import rockstar.elements.Program;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.System;
import java.util.logging.Level;
import java.util.logging.Logger;
import rockstar.parser.Parser;

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
        try {
            // TODO code application logic here
            // System.out.println("Hello Rocky!");
            System.out.println("file open");
            Program prg = new Parser(filename).parse();
            System.out.println("file parsed: " + prg);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Rockstar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
