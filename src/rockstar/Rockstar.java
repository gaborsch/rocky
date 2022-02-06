package rockstar;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import rockstar.debugger.RockstarDebugger;
import rockstar.parser.Parser;
import rockstar.repl.RockstarRepl;
import rockstar.runtime.Environment;
import rockstar.runtime.FileContext;
import rockstar.runtime.LoggerListener;
import rockstar.runtime.NativeObject;
import rockstar.runtime.RockNumber;
import rockstar.runtime.Utils;
import rockstar.statement.Program;
import rockstar.test.RockstarTest;
import rockstar.tool.Packer;

/**
 *
 * @author Gabor
 */
public class Rockstar {
    
    public static final String VERSION = "2.1.0";

    // implementation constants
    public static final int MAX_LOOP_ITERATIONS = 10000;

    // CLI commands
    private static final String CLI_WRAPPER = "rockstar";
    public static final String CLI_HEADER = "Rockstar Java by gaborsch, Version "+VERSION+" (with OOP and native Java)";

    private static final List<String> COMMANDS = (Arrays.asList(new String[]{"help", "run", "list", "repl", "test", "debug"}));

    public static void main(String[] args) {

//        if (args.length == 0) {
//    		args = new String[]{"list", "-x", "a.rock"};
//            args = new String[]{"list", "-x", "programs/tests/fixtures/Rocky_ext/native/arraylist.rock"};
//        args = new String[]{"run", "programs/tests/fixtures/Rocky_ext/native/arraylist.rock"};
//        args = new String[]{"test", "programs/tests"};
//        args = new String[]{"explain", "-s", "programs/tests/correct/nested_function_scopes.rock"};
//        args = new String[]{"explain", "programs/tests/correct/factorial.rock", "programs/tests/correct/operators/andTest.rock"};
//        args = new String[]{"explain", "C:\\work\\rocky\\rocky1\\rocky\\programs\\tests\\correct\\operators\\equalityComparison.rock"};
//        args = new String[]{"list", "-s", "programs/tests/correct/nested_function_scopes.rock"};
//        args = new String[]{"list", "programs/tests/correct/factorial.rock", "programs/tests/correct/operators/andTest.rock"};
//        args = new String[]{"list", "C:\\work\\rocky\\rocky1\\rocky\\programs\\tests\\correct\\operators\\equalityComparison.rock"};
//        args = new String[]{"test","-v", "programs/tests"};
//        args = new String[]{"test","C:\\work\\rocky\\rocky1\\rocky\\programs\\tests\\correct\\operators\\equalityComparison.rock"};
//        args = new String[]{"test", "--testdir", "C:\\work\\rocky\\rocky1\\rocky\\programs\\tests"};
//        args = new String[]{"test", "-w", "--testdir", "C:\\work\\rocky\\rocky1\\rocky\\programs\\tests\\_own_"};
//        args = new String[]{"help", "run"};
//        args = new String[]{"-", "-x"};
//          args = new String[]{"pack", "abc.rock", "programs/tests/correct/nested_function_scopes.rock"};
//        }

        List<String> argl = new LinkedList<>(Arrays.asList(args));

        new Rockstar().main(argl);
    }

    public void main(List<String> argl) {
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
                // -o or --option, also -o=value
                int eqIdx = a.indexOf('=');
                String key = (eqIdx >= 0) ? a.substring(0, eqIdx): a;
                String value = (eqIdx >= 0) ? a.substring(eqIdx+1): a;
                options.put(key, value);
            } else {
                // normal path name
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

        if (command.equals("help")) {
            doHelp(files.isEmpty() ? null : files.get(0), options);
        } else {            
            try {
                switch (command) {
                    case "help":
                        break;
                    case "run":
                        run(files, options);
                        break;
                    case "list":
                        list(files, options);
                        break;
                    case "repl":
                        repl(files, options);
                        break;
                    case "debug":
                        debug(files, options);
                        break;
                    case "test":
                        test(files, options);
                        break;
                    case "pack":
                        pack(files, options);
                        break;
                    default:
                        System.err.println("Unknown command: " + command);
                        doHelp(files.isEmpty() ? null : files.get(0), options);
                        break;
                }
            } catch (IllegalArgumentException iae) {
                doHelp(command, options);
            }
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
                System.out.println("    All files use the same context.");
                System.out.println("Options:");
                System.out.println("    --dec64");
                System.out.println("        Uses Dec64 arithmetic instead of the default IEEE754 (Double precision)");
                System.out.println("    --infinite-loops");
                System.out.println("        Loops can run infinitely. Default: maximum " + MAX_LOOP_ITERATIONS + " cycles per loop (for safety reasons)");
                System.out.println("    --exprlog");
                System.out.println("        Log the execution path and expression evaluations to the stdout");
                System.out.println("    --runlog");
                System.out.println("        Log the execution path to the stdout");
                System.out.println("    -X --rocky");
                System.out.println("        Use Rocky extensions (default: automatic mode)");
            }
        }
        if (cmd == null || cmd.equals("debug")) {
            System.out.println(CLI_WRAPPER + " debug [--options ...] <filename> ...");
            System.out.println("    Debug a program interactively. Stop at breakpoints, lines, display and watch variables.");
            if (cmd != null) {
                System.out.println("Options:");
                System.out.println("    --dec64");
                System.out.println("        Uses Dec64 arithmetic instead of the default IEEE754 (Double precision)");
                System.out.println("    --infinite-loops");
                System.out.println("        Loops can run infinitely. Default: maximum " + MAX_LOOP_ITERATIONS + " cycles per loop (for safety reasons)");
                System.out.println("    -X --rocky");
                System.out.println("        Use Rocky extensions (default: automatic mode)");
                RockstarDebugger.printDebuggerHelp("");
            }
        }
        if (cmd == null || cmd.equals("list")) {
            System.out.println(CLI_WRAPPER + " list [--options ...] <filename> ...");
            System.out.println("    Parse and list a program. Useful for syntax checking.");
            if (cmd != null) {
                System.out.println("Options:");
                System.out.println("    -l --line-number");
                System.out.println("        Print line numbers.");
                System.out.println("    -x --explain");
                System.out.println("        Display the AST (Abstract Syntax Tree)");
                System.out.println("    -X --rocky");
                System.out.println("        Use Rocky extensions (default: automatic mode)");
            }
        }
        if (cmd == null || cmd.equals("repl")) {
            System.out.println(CLI_WRAPPER + " - [<filename> ...]");
            System.out.println(CLI_WRAPPER + " repl [<filename> ...]");
            System.out.println("    Start an interactive session (Read-Evaluate-Print Loop). Enter commands and execute them immediately.");
            System.out.println("    The specified programs are pre-run (e.g. defining functions, etc). Special commands are available.");
            if (cmd != null) {
                System.out.println("Options:");
//                System.out.println("    -x --explain");
//                System.out.println("        Explain all statements and expressions parsed from input.");
                System.out.println("    --infinite-loops");
                System.out.println("        Loops can run infinitely. Default: maximum " + MAX_LOOP_ITERATIONS + " cycles per loop (for safety reasons)");
                System.out.println("    -X --rocky");
                System.out.println("        Use Rocky extensions (default: automatic mode)");
            }
        }
        if (cmd == null || cmd.equals("test")) {
            System.out.println(CLI_WRAPPER + " test [--options ...] <file-or-dirname> ...");
            System.out.println("    Execute unit tests. Special rules apply, check `" + CLI_WRAPPER + " help test` for details");
            if (cmd != null) {
                System.out.println("    Directories with name starting with '.' or '_' are skipped, unless -a option is given.");
                System.out.println("    Files under 'correct' or 'fixtures' directory must compile and run properly. This is the default.");
                System.out.println("    Files under 'parse-error' or 'failures' directory (and subdirectories) must produce parse error");
                System.out.println("    Files under 'runtime-error' directory (and subdirectories) must produce runtime error.");
                System.out.println("    All correct tests must compile and run. Expected output is in *.rock.out files, input is taken from *.rock.in' files, if present.");
                System.out.println();
                System.out.println("Options:");
                System.out.println("    -a, --all-directories");
                System.out.println("        Also include directories with name starting with '.' or '_'.");
                System.out.println("    -q, --quiet");
                System.out.println("        Quiet mode, print statistics only. Default: print failed tests and the statistics.");
                System.out.println("    -v, --verbose");
                System.out.println("        Verbose mode, print all executed test, including passed tests.");
                System.out.println("    -vv, --very-verbose");
                System.out.println("        Very verbose mode, print more information for failed tests.");
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

    public void run(List<String> files, Map<String, String> options) {
    	List<String> rockFiles = files.stream().filter(fn -> fn.endsWith(".rock")).collect(Collectors.toList());
        if (rockFiles.isEmpty()) {
            throw new IllegalArgumentException("Missing files");
        }

        LoggerListener logger = new LoggerListener(options);

        Environment env = Environment.create(System.in, System.out, System.err, options);
        env.setListener(logger);

        FileContext prgCtx = new FileContext(env);
        FileContext ctx;
        for (String filename : rockFiles) {
            try {
                Program prg = new Parser(filename, env).parse();
                ctx = new FileContext(prgCtx, filename);
                prg.execute(ctx);
            } catch (FileNotFoundException ex) {
                System.err.println("File not found: " + filename);
            } catch (RuntimeException re) {
                System.err.println("Error: " + re.getMessage());
            }
        }
    }

    public void list(List<String> files, Map<String, String> options) {
		List<String> rockFiles = files.stream().filter(fn -> fn.endsWith(".rock")).collect(Collectors.toList());   	
        if (rockFiles.isEmpty()) {
            throw new IllegalArgumentException("Missing files");
        }

        Environment env = Environment.create(System.in, System.out, System.err, options);
        boolean explain = env.hasOption("-x", "--explain");
        boolean lineNums = env.hasOption("-l", "--line-number");

        rockFiles.forEach((filename) -> {
            try {
                Program prg = new Parser(filename, env).parse();
                System.out.println(prg.listProgram(lineNums, !explain, explain));
            } catch (FileNotFoundException ex) {
                System.err.println("File not found: " + filename);
            } catch (RuntimeException re) {
                System.err.println("Error: " + re.getMessage());
            }
        });
    }

    public void test(List<String> files, Map<String, String> options) {
        if (files.isEmpty()) {
            throw new IllegalArgumentException("Missing files");
        }
        files.forEach((path) -> {
            new RockstarTest(options).execute(path);
        });
    }

    private void pack(List<String> files, Map<String, String> options) {
		List<String> rockFiles = files.stream().filter(fn -> fn.endsWith(".rock")).collect(Collectors.toList());   	
        if (rockFiles.isEmpty()) {
            throw new IllegalArgumentException("Missing files");
        }
        String mainFile = rockFiles.remove(0);
        new Packer().pack(mainFile, rockFiles);		
	}

    public void repl(List<String> files, Map<String, String> options) {
        new RockstarRepl(options).repl(files);
    }

    public void debug(List<String> files, Map<String, String> options) {
    	List<String> rockFiles = files.stream().filter(fn -> fn.endsWith(".rock")).collect(Collectors.toList());
    	new RockstarDebugger(options).debug(rockFiles);

    }

    public static void setGlobalOptions(Map<String, String> options) {
        boolean dec64 = options.containsKey("--dec64");
        RockNumber.setDec64(dec64);
        boolean disableNativeBinding = options.containsKey("--disable-native-java");
        NativeObject.setNativeDisabled(disableNativeBinding);

    
    }    
}
