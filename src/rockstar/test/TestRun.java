/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import rockstar.Rockstar;
import rockstar.parser.ParseException;

/**
 *
 * @author Gabor
 */
public class TestRun {

    public TestResult execute(String filename, RockstarTest.Expected exp) {
        
        TestResult result = new TestResult(exp);
        
        try {
            // input stream
            InputStream in = System.in;
            File inFile = new File(filename + "in'");
            try {
                in = new FileInputStream(inFile);
            } catch (FileNotFoundException ex) {
                // if the file does not exists, we will use the stdin
            }

            // output stream
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(os);

            Rockstar rockstar = new Rockstar(in, out, out /* err -> out */, new HashMap<>());
            rockstar.run(filename);

//            String result = os.toString(Charset.defaultCharset());

//            System.out.println("Output for file " + filename + ": " + result.length());
//            System.out.println(result);

        } catch (ParseException e) {
            result.setMessage("Parse error:"+ e.getMessage());
        } catch (Exception e) {
            result.setException(e);
            result.setMessage(e.getMessage());
        }

        return result;
    }

}
