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

    public enum Expected {
        CORRECT,
        PARSE_ERROR,
        RUNTIME_ERROR
    }

    public static void main(String[] args) {
        String correctDir = "C:\\work\\rocky\\tests\\rockstar\\tests\\correct";
        String correctOpDir = "C:\\work\\rocky\\tests\\rockstar\\tests\\correct\\operators";
        String parseErrorDir = "C:\\work\\rocky\\tests\\rockstar\\tests\\parse-errors";
        String runtimeErrorDir = "C:\\work\\rocky\\tests\\rockstar\\tests\\runtime-errors";

        RockstarTest tester = new RockstarTest();
        tester.listAndExecuteDir(correctDir, Expected.CORRECT);
        tester.listAndExecuteDir(correctOpDir, Expected.CORRECT);
        tester.listAndExecuteDir(parseErrorDir, Expected.PARSE_ERROR);
        tester.listAndExecuteDir(runtimeErrorDir, Expected.RUNTIME_ERROR);

        System.out.println("Result> : " + tester.testCount + " tests, " + tester.passed + " passed, " + tester.failed + " failed");

    }

    private int testCount = 0;
    private int passed = 0;
    private int failed = 0;

    private void listAndExecuteDir(String dirname, Expected exp) {
        File dir = new File(dirname);
        System.out.println(/*"Listing directory " + dirname + " for " +*/ exp + " tests");
        File[] files = dir.listFiles((File file, String name) -> name.endsWith(".rock"));
        if (files != null) {
            for (File file : files) {
//                System.out.println("processing file " + file.getName() + " for " + exp + " test");
                testCount++;
                TestResult result = new TestRun().execute(file.getAbsolutePath(), exp);
                if (result.isPassed) {
                    passed++;
                    System.out.printf("[OK  ] %-30s", file.getName());
                } else {
                    failed++;
                    if (result.exception == null) {
                        System.out.printf("[FAIL] %-40s %s", file.getName(), result.message);
                    } else {
                        System.out.printf("[EXCP] %-40s %s %s", file.getName(), result.exception.getClass().getSimpleName(), result.message);
                    }

                }
                System.out.println();
            }
        } else {
            System.out.println("NO FILES IN " + dirname);
        }
    }

}
