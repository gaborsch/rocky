package rockstar;

import rockstar.statement.Program;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import rockstar.parser.Parser;
import rockstar.runtime.BlockContext;
import rockstar.test.RockstarTest;

/**
 *
 * @author Gabor
 */
public class Rockstar {

    public static boolean DEBUG = false;

    private static final List<String> COMMANDS = (Arrays.asList(new String[]{"help", "run", "list", "repl", "test"}));

    public static void main(String[] args) {

//        args = new String[]{"run","C:\\work\\rocky\\rocky1\\rocky\\programs\\tests\\correct\\operators\\equalityComparison.rock"};
//        args = new String[]{"-s","programs/tests/correct/factorial.rock", "programs/tests/correct/operators/andTest.rock"};
//        args = new String[]{"list", "C:\\work\\rocky\\rocky1\\rocky\\programs\\tests\\correct\\operators\\equalityComparison.rock"};
//        args = new String[]{"test","C:\\work\\rocky\\rocky1\\rocky\\programs\\tests\\correct\\operators\\equalityComparison.rock"};
        args = new String[]{"test","--testdir","C:\\work\\rocky\\rocky1\\rocky\\programs\\tests"};
//        args = new String[]{"help", "run"};

        List<String> argl = new LinkedList<>(Arrays.asList(args));
        System.out.println("rockstar.Rockstar.main(), argc:" + argl.size());
        System.out.println("Args:");
        argl.forEach((arg) -> {
            System.out.println(arg);
        });

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
            if (options.containsKey("-h") || options.containsKey("--help")) {
                command = "help";
            } else if (!files.isEmpty()) {
                command = "run";
            } else {
                System.err.println("Unknown command.");
                return;
            }
        }
        switch (command) {
            case "help":
                String helpcmd = files.isEmpty() ? null : files.get(0);
                doHelp(helpcmd, options);
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
            case "test":
                doTest(files, options);
                break;
            default:
                System.err.println("Unknown command: " + command);
                break;
        }
    }

    private static void doHelp(String cmd, Map<String, String> options) {
        System.out.println("Rockstar Java by gaborsch, Version 0.99");
        System.out.println("---------------------------------------");
        System.out.println("Usage:");
        if (cmd == null || cmd.equals("run")) {
            System.out.println("rockstar <filename> ...");
            System.out.println("rockstar run [--options ...] <filename> ...");
            System.out.println("    Execute a program. Input is taken from standard input, output is printed to standard output.");
            if (cmd != null) {
                System.out.println("Options:");
                System.out.println("    -s, --same-context");
                System.out.println("        Use the same context for each consecutive program. (Default: create new context)");
            }
        }
        if (cmd == null || cmd.equals("list")) {
            System.out.println("rockstar list [--options ...] <filename> ...");
            System.out.println("    Parse and list a program. Useful for syntax checking.");
        }
        if (cmd == null || cmd.equals("repl")) {
            System.out.println("rockstar -");
            System.out.println("rockstar repl");
            System.out.println("    Start an interactive session (Read-Evaluate-Print Loop). Enter commands and execute them immediately.");
            System.out.println("    Special commands are available.");
        }
        if (cmd == null || cmd.equals("test")) {
            System.out.println("rockstar test [--options ...] <filename> ...");
            System.out.println("rockstar test --testdir <testdirectory>");
            System.out.println("    Execute unit tests. Files under 'parse-error' must produce parse error, files under 'runtime-error' must produce runtime error.");
            System.out.println("    All others must compile and run. Expected output is in *.rock.out files, input is taken from *.rock.in' files, if present.");
        }
        if (cmd == null || cmd.equals("help")) {
            System.out.println("rockstar [-h|--help]");
            System.out.println("rockstar help");
            System.out.println("    Print this help.");
            System.out.println("rockstar help <command>");
            System.out.println("    Print help about the command.");
        }
    }

    private static void doRun(List<String> files, Map<String, String> options) {
        if (files.isEmpty()) {
            doHelp("run", options);
            return;
        }
        boolean sameContext = options.containsKey("-s") || options.containsKey("--same-context");

        BlockContext ctx = null;
        for (String filename : files) {
            try {
                Program prg = new Parser(filename).parse();
                if(ctx == null || !sameContext) {
                    ctx = new BlockContext(System.in, System.out, System.err, options);
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

    private static void doRepl(List<String> files, Map<String, String> options) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void doTest(List<String> dirs, Map<String, String> options) {
        if (dirs.isEmpty()) {
            doHelp("test", options);
            return;
        }
        for (String dir : dirs) {
            new RockstarTest(options).executeDir(dir, null);
        }        
    }

}
