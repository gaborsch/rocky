/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.test;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import rockstar.runtime.Utils;

/**
 *
 * @author Gabor
 */
public class RockstarTest {

    private final Map<String, String> options;

    private boolean allDirectories;
    private boolean isQuiet;
    private boolean isVerbose;
    private boolean isVeryVerbose;

    public RockstarTest(Map<String, String> options) {
        this.options = options;
    }

    public enum Expected {
        CORRECT,
        PARSE_ERROR,
        RUNTIME_ERROR
    }

    public void execute(String path) {
        File f = new File(path);
        if(f.exists()) {
            if (f.isDirectory()) {
                executeDir(path);
            } else {
                executeFile(f, Expected.CORRECT);
            }
        }
    }


    private int testCount = 0;
    private int passed = 0;
    private int failed = 0;

    private void executeDir(String dirname) {

        allDirectories = options.containsKey("-a") || options.containsKey("--all-directories");
        isQuiet = options.containsKey("-q") || options.containsKey("--quiet");
        isVeryVerbose = options.containsKey("-vv") || options.containsKey("--very-verbose");
        isVerbose = isVeryVerbose || options.containsKey("-v") || options.containsKey("--verbose");

        executeDir(dirname, null);

        String SEPARATOR = Utils.repeat("=", 60);
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("Test results for " + dirname + ":");
        System.out.println(SEPARATOR);
        System.out.format("All tests:    %d\n", testCount);
        System.out.format("Failed tests: %d\n", failed);
        System.out.format("Passed tests: %d\n", passed);
        System.out.format("Pass ratio:   %3.2f%%\n", (testCount > 0) ? 100f * passed / testCount : 0.0d);
        System.out.println(SEPARATOR);
        System.out.println();
    }

    private void executeDir(String dirname, Expected exp) {
        File dir = new File(dirname);

        boolean isFirstFileInDir = true;
        File[] files = dir.listFiles();
        List<File> dirs = new LinkedList<>();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".rock")) {
                    if (isFirstFileInDir) {
                        if (exp != null) {
                            if (isVerbose) {
                                System.out.println(exp + " tests in " + dirname);
                            }
                        }
                        isFirstFileInDir = false;
                    }
                    executeFile(file, exp == null ? Expected.CORRECT : exp);
                } else if (file.isDirectory()) {
                    if (allDirectories || !file.getName().matches("^[._].*")) {
                        // skip directories starting with "." or "_"
                        dirs.add(file);
                    }
                }
            }
            for (File file : dirs) {
                switch (file.getName()) {
                    case "correct":
                    case "fixtures":
                        executeDir(file.getPath(), Expected.CORRECT);
                        break;
                    case "parse-errors":
                    case "failures":
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

    private void executeFile(File file, Expected exp) {

//        System.out.println("--- Processing file " + file.getName() + " for " + exp + " test");
        testCount++;
        TestResult result = new TestRun(options).execute(file.getAbsolutePath(), exp);
        String message = result.getMessage();
        Throwable exc = result.getException();
        String debugInfo = result.getDebugInfo();
        String excName = exc == null ? "" : exc.getClass().getSimpleName();
        if (result.isPassed()) {
            passed++;
            if (isVerbose) {
                System.out.printf("   [ OK ] %-40s\n", file.getName());
            }
        } else {
            failed++;
            if (exc == null) {
                if (!isQuiet) {
                    if (!isVerbose) {
                        System.out.println(exp + " test for " + file.getPath());
                    }
                    System.out.printf("!  [FAIL] %-40s %s\n", file.getName(), message);
                    if (isVeryVerbose) {
                        System.out.println(debugInfo);
                    }
                }
            } else {
                if (!isQuiet) {
                    if (!isVerbose) {
                        System.out.println(exp + " test for " + file.getPath());
                    }
                    System.out.printf("!! [EXCP] %-40s %s %s\n", file.getName(), excName, message);
                    if (isVeryVerbose) {
                        System.out.println(debugInfo);
                    }
                }
                // throw new RuntimeException(exc);
            }
        }
    }

}
