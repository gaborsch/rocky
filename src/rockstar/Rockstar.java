package rockstar;

import rockstar.statement.Program;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import rockstar.debugger.RockstarDebugger;
import rockstar.parser.Parser;
import rockstar.repl.RockstarRepl;
import rockstar.runtime.BlockContext;
import rockstar.runtime.LoggerListener;
import rockstar.runtime.RockNumber;
import rockstar.runtime.Utils;
import rockstar.test.RockstarTest;

/**
 *
 * @author Gabor
 */
public class Rockstar {

    // implementation constants
    public static final int MAX_LOOP_ITERATIONS = 1000;

    // CLI commands
    private static final String CLI_WRAPPER = "rockstar";
    public static final String CLI_HEADER = "Rockstar Java by gaborsch, Version 0.99";
    
    private static final List<String> COMMANDS = (Arrays.asList(new String[]{"help", "run", "list", "repl", "test", "debug"}));
    
    public static void main(String[] args) {

//        args = new String[]{"run","C:\\work\\rocky\\rocky1\\rocky\\programs\\tests\\correct\\operators\\equalityComparison.rock"};
//        args = new String[]{"test", "-w", "programs/tests/correct/fibonacci.rock"};
//        args = new String[]{"list", "-s", "programs/tests/correct/nested_function_scopes.rock"};
//        args = new String[]{"list", "programs/tests/correct/factorial.rock", "programs/tests/correct/operators/andTest.rock"};
//        args = new String[]{"list", "C:\\work\\rocky\\rocky1\\rocky\\programs\\tests\\correct\\operators\\equalityComparison.rock"};
//        args = new String[]{"test","C:\\work\\rocky\\rocky1\\rocky\\programs\\tests\\correct\\operators\\equalityComparison.rock"};
//        args = new String[]{"test", "--testdir", "C:\\work\\rocky\\rocky1\\rocky\\programs\\tests"};
//        args = new String[]{"test", "-w", "--testdir", "C:\\work\\rocky\\rocky1\\rocky\\programs\\tests\\_own_"};
//        args = new String[]{"help", "run"};
//        args = new String[]{"-", "-x"};
//        args = new String[]{"run", "programs/tests/correct/umlauts.rock"};
        List<String> argl = new LinkedList<>(Arrays.asList(args));
        
        List<String> files = new LinkedList<>();
        Map<String, String> options = new HashMap<>();
        
        String command = null;
        if (!argl.isEmpty()) {
            String a1 = argl.get(0);
            if (COMMANDS.contains(a1)) {
                command = argl.remove(0);
            }
        }
        
        while (!argl.isEmpty()) {
            String a = argl.remove(0);
            if (a.equals("-")) {
                // "-" is a special parameter
                command = "repl";
            } else if (a.startsWith("-")) {
                // -o or --option
                options.put(a, a);
            } else {
                // normal parh name
                files.add(a);
            }
        }
        
        if (command == null) {
            // no explicit command defined
            if (options.containsKey("-h") || options.containsKey("--help")) {
                command = "help";
            } else if (!files.isEmpty()) {
                command = "run";
            } else {
                command = "help";
            }
        }
        
        setGlobalOptions(options);
        
        switch (command) {
            case "help":
                doHelp(files.isEmpty() ? null : files.get(0), options);
                break;
            case "run":
                doRun(files, options);
                break;
            case "list":
                doList(files, options);
                break;
            case "repl":
                doRepl(files, options);
                break;
            case "debug":
                doDebug(files, options);
                break;
            case "test":
                doTest(files, options);
                break;
            default:
                System.err.println("Unknown command: " + command);
                doHelp(files.isEmpty() ? null : files.get(0), options);
                break;
        }
    }
    
    private static void doHelp(String cmd, Map<String, String> options) {
        System.out.println(CLI_HEADER);
        System.out.println(Utils.repeat("-", CLI_HEADER.length()));
        System.out.println("Usage:");
        if (cmd == null || cmd.equals("run")) {
            System.out.println(CLI_WRAPPER + " <filename> ...");
            System.out.println(CLI_WRAPPER + " run [--options ...] <filename> ...");
            System.out.println("    Execute a program. Input is taken from standard input, output is printed to standard output.");
            if (cmd != null) {
                System.out.println("Options:");
                System.out.println("    -s, --same-context");
                System.out.println("        Use the same context for each consecutive program. (Default: create new context)");
                System.out.println("    --infinite-loops");
                System.out.println("        Loops can run infinitely. Default: maximum " + MAX_LOOP_ITERATIONS + " cycles per loop (for safety reasons)");
                System.out.println("    --runlog");
                System.out.println("        Log the execution path to the stdout");
                System.out.println("    --exprlog");
                System.out.println("        Log the execution path and expression evaluations to the stdout");
                System.out.println("    --dec64");
                System.out.println("        Uses Dec64 arithmetic instead of the default IEEE754 (Double precision)");
            }
        }
        if (cmd == null || cmd.equals("debug")) {
            System.out.println(CLI_WRAPPER + " debug [--options ...] <filename> ...");
            System.out.println("    Debug a program interactively. Stop at breakpoints, lines, display and watch variables.");
            if (cmd != null) {
                System.out.println("Options:");
                System.out.println("    --infinite-loops");
                System.out.println("        Loops can run infinitely. Default: maximum " + MAX_LOOP_ITERATIONS + " cycles per loop (for safety reasons)");
                System.out.println("    --dec64");
                System.out.println("        Uses Dec64 arithmetic instead of the default IEEE754 (Double precision)");
                RockstarDebugger.printDebuggerHelp("");
            }
        }
        if (cmd == null || cmd.equals("list")) {
            System.out.println(CLI_WRAPPER + " list [--options ...] <filename> ...");
            System.out.println("    Parse and list a program. Useful for syntax checking.");
        }
        if (cmd == null || cmd.equals("repl")) {
            System.out.println(CLI_WRAPPER + " - [<filename> ...]");
            System.out.println(CLI_WRAPPER + " repl [<filename> ...]");
            System.out.println("    Start an interactive session (Read-Evaluate-Print Loop). Enter commands and execute them immediately.");
            System.out.println("    The specified programs are pre-run (e.g. defining functions, etc). Special commands are available.");
            if (cmd != null) {
                System.out.println("Options:");
                System.out.println("    -x --explain");
                System.out.println("        Explain all statements and expressions parsed from input.");
                System.out.println("    --infinite-loops");
                System.out.println("        Loops can run infinitely. Default: maximum " + MAX_LOOP_ITERATIONS + " cycles per loop (for safety reasons)");
            }
        }
        if (cmd == null || cmd.equals("test")) {
            System.out.println(CLI_WRAPPER + " test [--options ...] <filename> ...");
            System.out.println(CLI_WRAPPER + " test --testdir <directoryname>");
            System.out.println("    Execute unit tests. Special rules apply, check `" + CLI_WRAPPER + " help test` for details");
            if (cmd != null) {
                System.out.println("    Directories with name starting with '.' or '_' are skipped.");
                System.out.println("    Files under 'parse-error' directory (and subdirectories) must produce parse error");
                System.out.println("    Files under 'runtime-error' directory (and subdirectories) must produce runtime error.");
                System.out.println("    Files under 'correct' directory must compile and run properly. This is the default.");
                System.out.println("    All correct tests must compile and run. Expected output is in *.rock.out files, input is taken from *.rock.in' files, if present.");
                System.out.println();
                System.out.println("Options:");
                System.out.println("    -a, --all-directories");
                System.out.println("        Also include directories with name starting with '.' or '_'.");
                System.out.println("    -w, --write-output");
                System.out.println("        Write actual output into *.rock.current file, if the output does not match the expected.");
                System.out.println("    --infinite-loops");
                System.out.println("        Loops can run infinitely. Default: maximum " + MAX_LOOP_ITERATIONS + " cycles per loop (for safety reasons)");
            }
        }
        if (cmd == null || cmd.equals("help")) {
            System.out.println(CLI_WRAPPER + " [-h|--help]");
            System.out.println(CLI_WRAPPER + " help");
            System.out.println("    Print this help.");
            System.out.println(CLI_WRAPPER + " help <command>");
            System.out.println("    Print more detailed help about the given command.");
        }
    }
    
    private static void doRun(List<String> files, Map<String, String> options) {
        if (files.isEmpty()) {
            doHelp("run", options);
            return;
        }
        boolean sameContext = options.containsKey("-s") || options.containsKey("--same-context");
        
        LoggerListener logger = new LoggerListener(options);
        
        BlockContext ctx = null;
        for (String filename : files) {
            try {
                Program prg = new Parser(filename).parse();
                if (ctx == null || !sameContext) {
                    ctx = new BlockContext(System.in, System.out, System.err, options);
                    ctx.setListener(logger);
                }
                prg.execute(ctx);
            } catch (FileNotFoundException ex) {
                System.err.println("File not found: " + filename);
            }
        }
    }
    
    private static void doList(List<String> files, Map<String, String> options) {
        if (files.isEmpty()) {
            doHelp("list", options);
            return;
        }
        files.forEach((filename) -> {
            try {
                Program prg = new Parser(filename).parse();
                System.out.println(prg.listProgram());
            } catch (FileNotFoundException ex) {
                System.err.println("File not found: " + filename);
            }
        });
    }
    
    private static void doTest(List<String> files, Map<String, String> options) {
        if (files.isEmpty()) {
            doHelp("test", options);
            return;
        }
        boolean isDirectory = options.containsKey("--testdir");
        if (isDirectory) {
            // 
            files.forEach((dir) -> {
                new RockstarTest(options).executeDir(dir, null);
            });
        } else {
            // files
            files.forEach((file) -> {
                new RockstarTest(options).executeFile(file, null);
            });
            
        }
    }
    
    private static void doRepl(List<String> files, Map<String, String> options) {
        new RockstarRepl(options).repl(files);
    }
    
        
    private static void doDebug(List<String> files, Map<String, String> options) {
        new RockstarDebugger(options).debug(files);

    }
    private static void setGlobalOptions(Map<String, String> options) {
        boolean dec64 = options.containsKey("--dec64");
        RockNumber.setDec64(dec64);
    }
    
}
