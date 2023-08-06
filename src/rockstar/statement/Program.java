/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.List;

import rockstar.parser.ParserError;

/**
 *
 * @author Gabor
 */
public class Program extends Block {

    private final String name;
    private List<ParserError> errors = null;

    public String getName() {
        return name;
    }

    public Program(String name) {
        this.name = name;
    }

    public List<ParserError> getErrors() {
        return errors;
    }

    public ParserError getErrorOnLine(int lnum) {
        return errors == null
                ? null
                : errors
                        .stream()
                        .filter(e -> e.getLine().getLnum() == lnum)
                        .findFirst()
                        .orElse(null);
    }

    public void addError(ParserError e) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(e);
    }

    public boolean hasNoError() {
        return errors == null;
    }

//    private class Listing {
//
//        private final boolean lineNums;
//        private final StringBuilder sb = new StringBuilder();
//        private int lastLnum = 0;
//
//        public Listing(boolean lineNums, boolean normal, boolean explained) {
//            this.lineNums = lineNums;
//        }
//
//        @Override
//        public String toString() {
//            return sb.toString();
//        }

//        private void listStatement(int indent, Statement stmt) {
//            Line line = stmt.getLine();
//            int lnum = (line != null) ? line.getLnum() : 0;
//            while (lastLnum < lnum - 1) {
//                lastLnum++;
//                if (lineNums) {
//                    sb.append(String.format("(%d)", lastLnum));
//                }
//                sb.append("\n");
//            }
//            lastLnum = lnum;
//
//            if (lineNums) {
//                sb.append(line == null ? "" : String.format("(%d)", lnum)).append(" ");
//            }
//            sb.append(Utils.repeat("  ", indent));
//            sb.append(line == null ? "" : line.getOrigLine().trim()).append("\n");
//
//            ParserError error = Program.this.getErrorOnLine(lnum);
//            if (error != null) {
//                if (lineNums) {
//                    sb.append(line == null ? "" : Utils.repeat(" ", String.format("(%d)", lnum).length())).append(" ");
//                }
//                sb.append(Utils.repeat("  ", indent));
//                sb.append(Utils.repeat(" ", error.getPos()));
//                sb.append("^--- Error: ");
//                sb.append(error.getMsg());
//            }
//        }
//    }

//    public String listProgram(boolean lineNums, boolean normal, boolean explained) {
//        if (normal) {
//            Listing listing = new Listing(lineNums, normal, explained);
//            listProgram(0, this, listing);
//            return listing.toString();
//        } else {
//            return new ASTListing(this).list();
//        }
//    }
//
//    public void listProgram(int indent, Statement stmt, Listing listing) {
//        // preorder traversal through the block statements
//        listing.listStatement(indent, stmt);
//        if (stmt instanceof Block) {
//            List<Statement> stmts = ((Block) stmt).getStatements();
//            stmts.forEach((stmt2) -> {
//                int indentIncrement = (stmt2 instanceof ContinuingBlockStatementI) ? 0 : 1;
//                listProgram(indent + indentIncrement, stmt2, listing);
//            });
//        }
//    }

    @Override
    public String getASTNodeText() {
        return super.getASTNodeText() + " " + (name != null ? name : null);
    }
    
    @Override
    public void accept(StatementVisitor visitor) {
    	visitor.visit(this);
    }

}
