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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import rockstar.parser.ParseException;
import rockstar.parser.Parser;
import rockstar.runtime.BlockContext;
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

            BlockContext ctx = new BlockContext(in, out, err, options);
            try {
                prg = new Parser(filename).parse();
                prg.execute(ctx);
            } catch (FileNotFoundException ex) {
                result.setException(ex);
            }
            result.setDebugInfo(prg == null ? "Not parsed" : prg.listProgram());

            String output = os.toString(Charset.defaultCharset());

            compareOutput(filename, expectedOutput, output, result);

        } catch (ParseException e) {
            result.setMessage("Parse error:" + e.getMessage());
            result.setDebugInfo(e.getLine().getOrigLine());
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
            FileWriter w = null;
            String filename = rockFilename + ".current";
            try {
                w = new FileWriter(filename);
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
