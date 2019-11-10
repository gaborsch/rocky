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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;
import rockstar.parser.Line;
import rockstar.parser.ParseException;
import rockstar.parser.Parser;
import rockstar.runtime.Environment;
import rockstar.runtime.FileContext;
import rockstar.runtime.Utils;
import rockstar.statement.Program;

/**
 *
 * @author Gabor
 */
public class TestRun {

    private final Map<String, String> options;

    TestRun(Map<String, String> options) {
        this.options = options;
    }

    public TestResult execute(String filename, RockstarTest.Expected exp) {

        TestResult result = new TestResult(exp);

        Program prg = null;
        try {
            // input stream
            InputStream in;
            File inFile = new File(filename + ".in'");
            if (!inFile.exists()) {
                inFile = new File(filename + ".in");
            }
            if (inFile.exists()) {
                try {
                    in = new FileInputStream(inFile);
//                System.out.println("Input file " + inFile.getName() + " found");
                } catch (FileNotFoundException ex) {
                    in = new ByteArrayInputStream(new byte[0]);
                }
            } else {
                in = new ByteArrayInputStream(new byte[0]);
            }

            String expectedOutput;
            try {
                // find and load expected outpu from .out file
                File outValidationFile = new File(filename + ".out");
                FileInputStream ois = new FileInputStream(outValidationFile);
                BufferedReader ordr = new BufferedReader(new InputStreamReader(ois, Utils.UTF8));
                StringBuilder osb = new StringBuilder();
                String line;
                while ((line = ordr.readLine()) != null) {
                    osb.append(line).append('\n');
                }
                expectedOutput = osb.toString();
//                System.out.println("Output validation file " + outValidationFile.getName() + " found, " + expectedOutput.length() + " chars");
            } catch (FileNotFoundException ex) {
                expectedOutput = "";
            }

            // output stream
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(os, true, Utils.UTF8);
            ByteArrayOutputStream errs = new ByteArrayOutputStream();
            PrintStream err = new PrintStream(errs, true, Utils.UTF8);

            Environment env = new Environment(in, out, err, options);
            FileContext ctx = new FileContext(env);
            try {
                prg = new Parser(filename).parse();
                prg.execute(ctx);
            } catch (FileNotFoundException ex) {
                result.setException(ex);
            }
            result.setDebugInfo(prg == null ? "Not parsed" : prg.listProgram(true, true, false));

            String output = null;
            try {
                output = os.toString(Utils.UTF8);
            } catch (UnsupportedEncodingException ex) {
                throw ex;
            }

            compareOutput(filename, expectedOutput, output, result);

        } catch (ParseException e) {
            result.setMessage("Parse error:" + e.getMessage());
            Line line = e.getLine();
            if (line != null) {
                result.setDebugInfo(line.getOrigLine());
            }
        } catch (Throwable e) {
            result.setException(e);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    private void compareOutput(String filename, String expectedOutput, String output, TestResult result) {
        int lineNum = 1;
        Scanner exp = new Scanner(expectedOutput).useDelimiter("\\r?\\n");
        Scanner act = new Scanner(output).useDelimiter("\\r?\\n");

        while (exp.hasNext() && act.hasNext()) {
            String expLine = exp.next();
            String actLine = act.next();
            if (!expLine.equals(actLine)) {
                String msg = "OUTPUT MISMATCH at line " + lineNum + ": expected '" + expLine + "', got '" + actLine + "'";
                result.setMessage(msg);
                writeCurrentOutput(filename, output);
                return;
            }
            lineNum++;
        }
        if (exp.hasNext()) {
            result.setMessage("MORE OUTPUT EXPECTED at line " + lineNum);
            result.setDebugInfo(exp.next());
            writeCurrentOutput(filename, output);
        } else if (act.hasNext()) {
            result.setMessage("SURPLUS OUTPUT at line " + lineNum);
            result.setDebugInfo(act.next());
            writeCurrentOutput(filename, output);
        }

    }

    private void writeCurrentOutput(String rockFilename, String output) {
        boolean writeOutput = options.containsKey("-w") || options.containsKey("--write-output");
        if (writeOutput) {
            Writer w = null;
            String filename = rockFilename + ".current";
            try {
                FileOutputStream fos = new FileOutputStream(filename);
                w = new OutputStreamWriter(fos, Utils.UTF8);
                w.write(output);
            } catch (IOException ex) {
                System.err.println("Error writing file: " + filename);
            } finally {
                try {
                    if (w != null) {
                        w.close();
                    }
                } catch (IOException ex) {
                    System.err.println("Error writing file: " + filename);
                }
            }
        }
    }
}
