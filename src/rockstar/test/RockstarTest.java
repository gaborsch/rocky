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
        String dir = "C:\\work\\rocky\\rocky1\\rocky\\programs\\tests";
//        String dir = "C:\\work\\rocky\\tests\\rockstar\\tests";
        new RockstarTest().executeDir(dir, null);
    }

    private int testCount = 0;
    private int passed = 0;
    private int failed = 0;

    private void executeDir(String dirname, Expected exp) {
        File dir = new File(dirname);
//        System.out.println("Listing directory " + dirname + (exp == null ? "" : ", testing "+ exp) );
//        if (exp != null) { System.out.println(exp + " tests"); }
        if (exp != null) {
            System.out.println(exp + " tests in " + dirname);
        }

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".rock")) {
                    executeFile(file, exp);
                } else if (file.isDirectory()) {
                    switch (file.getName()) {
                        case "correct":
                            executeDir(file.getPath(), Expected.CORRECT);
                            break;
                        case "parse-errors":
                            executeDir(file.getPath(), Expected.PARSE_ERROR);
                            break;
                        case "runtime-errors":
                            executeDir(file.getPath(), Expected.RUNTIME_ERROR);
                            break;
                        default:
                            executeDir(file.getPath(), exp);
                            break;
                    }
                }

            }
        }
    }

    private void executeFile(File file, Expected exp) {

//        System.out.println("--- Processing file " + file.getName() + " for " + exp + " test");
        testCount++;
        TestResult result = new TestRun().execute(file.getAbsolutePath(), exp);
        String message = result.getMessage();
        Throwable exc = result.getException();
        String excName = exc == null ? "" : exc.getClass().getSimpleName();
        if (result.isPassed()) {
            passed++;
            System.out.printf("   [ OK ] %-30s", file.getName());
        } else {
            failed++;
            if (exc == null) {
                System.out.printf("!  [FAIL] %-40s %s", file.getName(), message);
            } else {
                System.out.printf("!! [EXCP] %-40s %s %s", file.getName(), excName, message);
            }
        }
        System.out.println();
    }

}
