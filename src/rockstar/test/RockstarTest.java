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

/**
 *
 * @author Gabor
 */
public class RockstarTest {

    private final Map<String, String> options;

    public RockstarTest(Map<String, String> options) {
        this.options = options;
    }

    public enum Expected {
        CORRECT,
        PARSE_ERROR,
        RUNTIME_ERROR
    }

    private int testCount = 0;
    private int passed = 0;
    private int failed = 0;

    public void executeDir(String dirname, Expected exp) {
        File dir = new File(dirname);

        boolean allDirectories = options.containsKey("-a") || options.containsKey("--all-directories");

        boolean isFirstFileInDir = true;
        File[] files = dir.listFiles();
        List<File> dirs = new LinkedList<>();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".rock")) {
                    if (isFirstFileInDir) {
                        if (exp != null) {
                            System.out.println(exp + " tests in " + dirname);
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

    public void executeFile(String file, Expected exp) {
        executeFile(new File(file), exp == null ? Expected.CORRECT : exp);
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
            System.out.printf("   [ OK ] %-40s", file.getName());
        } else {
            failed++;
            if (exc == null) {
                System.out.printf("!  [FAIL] %-40s %s\n", file.getName(), message);
                System.out.println(debugInfo);
            } else {
                System.out.printf("!! [EXCP] %-40s %s %s\n", file.getName(), excName, message);
                System.out.println(debugInfo);
                // throw new RuntimeException(exc);
            }
        }
        System.out.println();
    }

}
