/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
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

        Rockstar rockstar = null;
        try {
            // input stream
            InputStream in;
            File inFile = new File(filename + ".in'");
            try {
                in = new FileInputStream(inFile);
//                System.out.println("Input file " + inFile.getName() + " found");
            } catch (FileNotFoundException ex) {
                in = new ByteArrayInputStream(new byte[0]);
            }

            String expectedOutput;
            try {
                // find and load expected outpu from .out file
                File outValidationFile = new File(filename + ".out");
                BufferedReader rdr = new BufferedReader(new FileReader(outValidationFile));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rdr.readLine()) != null) {
                    sb.append(line);
                }
                expectedOutput = sb.toString();
//                System.out.println("Output validation file " + outValidationFile.getName() + " found, " + expectedOutput.length() + " chars");
            } catch (FileNotFoundException ex) {
                expectedOutput = "";
            }

            // output stream
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(os);
            ByteArrayOutputStream errs = new ByteArrayOutputStream();
            PrintStream err = new PrintStream(errs);

            rockstar = new Rockstar(in, out, err, new HashMap<>());
            rockstar.run(filename);

            String output = os.toString(Charset.defaultCharset());

//            if (output != null && output.length() > 0) {
//                System.out.println("Output for " + filename + ": " + output.length() + " chars");
//                System.out.println(output);
//            } else if (expectedOutput.length() > 0) {
//                result.setMessage("No output received");
//            }

        } catch (ParseException e) {
            result.setMessage("Parse error:" + e.getMessage());
        } catch (Throwable e) {
            result.setException(e);
            result.setMessage(e.getMessage());
//            if (rockstar != null) {
//                System.err.println(rockstar.getLogString());
//            }
        }

        // print log
//        if (rockstar != null) {
//            System.out.println(rockstar.getLogString());
//        }

        return result;
    }

}
