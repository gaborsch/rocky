/*
 * Prints the program using visitor pattern
 */
package rockstar.parser;

import java.io.PrintStream;
import java.util.List;
import rockstar.statement.Block;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class StatementPrinter {

    public void print(Statement s, PrintStream out) {
        print(s, out, 0);
    }
    
    private void print(Statement s, PrintStream out, int level) {
        Line line = s.getLine();
        if (line != null) {
            out.format("[ %2d ] ", line.getLnum());
        } else {
            out.print("       ");
        }
        out.print("    ".repeat(level));
        // prune extra line information, if present
        out.println(s.getClass().getSimpleName() + ": " + s.toString().replaceAll("\\n.*$", ""));
        if (s instanceof Block) {
            List<Statement> statements = ((Block) s).getStatements();
            statements.forEach((Statement sub) -> {
                print(sub, out, level+1);
            });
        }
    }

}
