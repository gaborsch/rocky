/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.debugger;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import rockstar.Rockstar;
import rockstar.parser.Parser;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Utils;
import rockstar.statement.Program;

/**
 *
 * @author Gabor
 */
public class RockstarDebugger {

    Map<String, String> options;

    public RockstarDebugger(Map<String, String> options) {
        this.options = options;
    }

    public void debug(List<String> files) {
        BlockContext ctx = new BlockContext(System.in, System.out, System.err, options);

        DebugListener listener = new DebugListener(options);
        ctx.setListener(listener);

        System.out.println(Rockstar.CLI_HEADER);
        System.out.println(Utils.repeat("-", Rockstar.CLI_HEADER.length()));
        System.out.println("Type 'exit' to quit, 'show' to get more info.");

        // pre-run any programs defined as parameter
        files.forEach((filename) -> {
            try {
                Program prg = new Parser(filename).parse();
                prg.execute(ctx);
            } catch (FileNotFoundException ex) {
                System.err.println("File not found: " + filename);
            }
        });

    }

    public static void printDebuggerHelp(String helpCmd) {
        System.out.println("Debugger commands:");
        System.out.println("    5 or newline    Step Into");
        System.out.println("    6               Step Over (stop at line breakpoints)");
        System.out.println("    7               Step Return (stop at line breakpoints)");
        System.out.println("    8               Step Run (stop at line breakpoints)");
        System.out.println("    b [linenum]     Add line breakpoint, default: current line");
        System.out.println("    br [linenum]    Remove line breakpoint, default: current line");
        System.out.println("    s <variable>    Show variable (no expressions are possible)");
        System.out.println("    w <variable>    Watch variable (no expressions). Watches evaluated before every statement.");
        System.out.println("    wr <variable>   Remove watch. '#1' refers to the first watch");
    }

}
