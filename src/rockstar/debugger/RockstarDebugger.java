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
import rockstar.parser.ParseException;
import rockstar.parser.Parser;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
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
        System.out.println("Type 'exit' to quit, '?' to get more help on debugger commands. Press enter after each command.");

        try {
        // pre-run any programs defined as parameter
        files.forEach((filename) -> {
            try {
                Program prg = new Parser(filename).parse();
                listener.setProgram(prg);
                System.out.println("Debugging " + filename);       
                prg.execute(ctx);
            } catch (FileNotFoundException ex) {
                System.err.println("File not found: " + filename);
            } catch (ParseException ex) {
                System.out.println("!!! Could not parse " + filename);       
            }
        });
        } catch (RockstarRuntimeException rrex) {
            System.out.println("Rockstar Debugger finished by " + rrex.getMessage());
        }

    }

    public static void printDebuggerHelp(String helpCmd) {
        System.out.println("Debugger commands:");
        System.out.println("    5 or newline    Step Into");
        System.out.println("    6               Step Over (stop at line breakpoints)");
        System.out.println("    7               Step Return (stop at line breakpoints)");
        System.out.println("    8               Step Run (stop at line breakpoints)");
        System.out.println("    1 or x          Step Into Expression (print every step of the expression evaluation)");
        System.out.println("    X               Turns the Step Into mode sticky (X again will turn it off)");
        System.out.println("    b [linenum]     Add line breakpoint, default: current line");
        System.out.println("    br [linenum]    Remove line breakpoint, default: current line");
        System.out.println("    bl              List breakpoints");
        System.out.println("    s [<variable>]  Show variable (no expressions are possible). Default: show all variables");
        System.out.println("                    Showing an Object by name lists its properties");
        System.out.println("    w <variable>    Watch variable (no expressions). Watches evaluated before every statement.");
        System.out.println("    wr <variable>   Remove watch. '#1' refers to the first watch");
        System.out.println("    .               Prints the current line again (no step)");
        System.out.println("    list            Lists the current program");
        System.out.println("    exit            Exits the debugger");
    }

}
