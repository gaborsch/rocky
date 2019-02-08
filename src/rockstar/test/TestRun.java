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
import java.util.Scanner;
import rockstar.Rockstar;
import rockstar.parser.ParseException;
import rockstar.statement.Program;

/**
 *
 * @author Gabor
 */
public class TestRun {

    public TestResult execute(String filename, RockstarTest.Expected exp) {

        TestResult result = new TestResult(exp);

        Program prg = null;
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
                    sb.append(line).append('\n');
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

            Rockstar rockstar = new Rockstar(in, out, err, new HashMap<>());
            prg = rockstar.run(filename);
            result.setDebugInfo(prg == null ? "Not parsed" : prg.listProgram());

            String output = os.toString(Charset.defaultCharset());

            compareOutput(expectedOutput, output, result);

        } catch (ParseException e) {
            result.setMessage("Parse error:" + e.getMessage());
            result.setDebugInfo(e.getLine().getOrigLine());
        } catch (Throwable e) {
            result.setException(e);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    private void compareOutput(String expectedOutput, String output, TestResult result) {
        int lineNum = 1;
        Scanner exp = new Scanner(expectedOutput).useDelimiter("\\r?\\n");
        Scanner act = new Scanner(output).useDelimiter("\\r?\\n");

        while (exp.hasNext() && act.hasNext()) {
            String expLine = exp.next();
            String actLine = act.next();
            if (!expLine.equals(actLine)) {
                String msg = "OUTPUT MISMATCH at line " + lineNum + ": expected '" + expLine + "', got '" + actLine + "'";
                result.setMessage(msg);
                return;
            }
            lineNum++;
        }
        if (exp.hasNext()) {
            result.setMessage("PREMATURE END OF OUTPUT at line " + lineNum);
        } else if (act.hasNext()) {
            result.setMessage("SURPLUS OUTPUT at line " + lineNum);
        }

    }

}
