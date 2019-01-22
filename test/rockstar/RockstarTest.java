/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 *
 * @author Gabor
 */
public class RockstarTest {

    enum Expected {
        CORRECT,
        PARSE_ERROR,
        RUNTIME_ERROR
    }

    public static void main(String[] args) {
        String correctDir = "C:\\work\\rocky\\tests\\rockstar\\tests\\correct";
        String parseErrorDir = "C:\\work\\rocky\\tests\\rockstar\\tests\\parse-errors";
        String runtimeErrorDir = "C:\\work\\rocky\\tests\\rockstar\\tests\\runtime-errors";

        RockstarTest tester = new RockstarTest();
        tester.listAndExecuteDir(correctDir, Expected.CORRECT);
//        tester.listAndExecuteDir(parseErrorDir, Expected.PARSE_ERROR);
//        tester.listAndExecuteDir(runtimeErrorDir, Expected.RUNTIME_ERROR);

    }
    
    private int testCount = 0;
    private int passed = 0;
    private int failed = 0;
    

    private void listAndExecuteDir(String dirname, Expected exp) {
        File dir = new File(dirname);
        System.out.println("Listing directory "+dirname + " for "+exp+" tests");
        File[] files = dir.listFiles((File file, String name) -> name.endsWith(".rock"));
        if (files != null) {
            for (File file : files) {
                System.out.println("processing file " + file.getName()+ " for "+exp+" test");
                testCount++;
              if(runTest(file.getAbsolutePath(), exp)) {
                  passed++;
              } else {
                  failed++;
              }
            }
        } else {
            System.out.println("NO FILES IN "+dirname);
        }
        System.out.println(exp + ": " + testCount+ " tests, "+passed + " passed, "+failed + " failed");
    }
    
    public boolean runTest(String filename, Expected exp) {
        
//        return true;
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

        String result = os.toString(Charset.defaultCharset());

        System.out.println("Output for file " + filename + ": " + result.length());
        System.out.println(result);
        return true;
    }

}
