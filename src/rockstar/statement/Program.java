/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.parser.Line;
import rockstar.runtime.Utils;

/**
 *
 * @author Gabor
 */
public class Program extends Block {

    private final String name;

    public String getName() {
        return name;
    }

    public Program(String name) {
        this.name = name;
    }

    @Override
    protected String explain() {
        return "PROGRAM " + name;
    }

    public static class Listing {

        private final boolean lineNums;
        private final boolean normal;
        private final boolean explained;
        private final StringBuilder sb = new StringBuilder();
        private int lastLnum = 0;

        public Listing(boolean lineNums, boolean normal, boolean explained) {
            this.lineNums = lineNums;
            this.normal = normal;
            this.explained = explained;
        }

        @Override
        public String toString() {
            return sb.toString();
        }

        private void listStatement(int indent, Statement stmt) {
            Line line = stmt.getLine();
            int lnum = (line != null) ? line.getLnum() : 0;
            while (lastLnum < lnum - 1) {
                lastLnum++; 
                if (lineNums) {
                    sb.append(String.format("(%d)", lastLnum));
                } 
                sb.append("\n");
            }
            lastLnum = lnum;
            if (normal) {
                if (lineNums) {
                    sb.append(line == null ? "" : String.format("(%d)", lnum)).append(" ");
                }
                sb.append(Utils.repeat("  ", indent));
                sb.append(line == null ? "" : line.getOrigLine().trim()).append("\n");
            }
            if (explained) {
                if (lineNums) {
                    sb.append(line == null ? "" : String.format("(%d)", lnum)).append(" ");
                }
                sb.append(Utils.repeat("  ", indent));
                sb.append(stmt.explain()).append("\n");
            }
        }
    }

    public String listProgram(boolean lineNums, boolean normal, boolean explained) {
        Listing listing = new Listing(lineNums, normal, explained);
        listProgram(0, this, listing);
        return listing.toString();
    }

    public void listProgram(int indent, Statement stmt, Listing listing) {
        // preorder traversal through the block statements
        listing.listStatement(indent, stmt);
        if (stmt instanceof Block) {
            List<Statement> stmts = ((Block) stmt).getStatements();
            stmts.forEach((stmt2) -> {
                int indentIncrement = (stmt2 instanceof ElseStatement) ? 0 : 1;
                listProgram(indent + indentIncrement, stmt2, listing);
            });
        }
    }

}
