/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.List;
import rockstar.parser.Line;
import rockstar.parser.ParserError;
import rockstar.runtime.ASTAware;
import rockstar.runtime.Utils;

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

    @Override
    protected String explain() {
        return "PROGRAM " + name;
    }

    public class Listing {

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
            } else {
                ParserError error = Program.this.getErrorOnLine(lnum);
                if (error != null) {
                    if (lineNums) {
                        sb.append(line == null ? "" : Utils.repeat(" ", String.format("(%d)", lnum).length())).append(" ");
                    }
                    sb.append(Utils.repeat("  ", indent + error.getPos()));
                    sb.append("^--- Error: ");
                    sb.append(error.getMsg());
                }
            }
        }
    }

    public String listProgram(boolean lineNums, boolean normal, boolean explained) {
        if (normal) {
            Listing listing = new Listing(lineNums, normal, explained);
            listProgram(0, this, listing);
            return listing.toString();
        } else {
            return list(this);
        }
    }

    public void listProgram(int indent, Statement stmt, Listing listing) {
        // preorder traversal through the block statements
        listing.listStatement(indent, stmt);
        if (stmt instanceof Block) {
            List<Statement> stmts = ((Block) stmt).getStatements();
            stmts.forEach((stmt2) -> {
                int indentIncrement = (stmt2 instanceof ContinuingBlockStatementI) ? 0 : 1;
                listProgram(indent + indentIncrement, stmt2, listing);
            });
        }
    }

    private static class ASTListingLine {

        private final String text;
        private final Integer lineNumber;
        private final int indent;

        public ASTListingLine(String text, Integer lineNumber, int indent) {
            this.text = text;
            this.lineNumber = lineNumber;
            this.indent = indent;
        }

        public String getText() {
            return text;
        }

        public int getIndent() {
            return indent;
        }

        public Integer getLineNumber() {
            return lineNumber;
        }

    }

    public static class ASTListing {

        private final StringBuilder sb = new StringBuilder();

        private final ArrayList<ASTListingLine> lines = new ArrayList<>();

        private void addIndented(String s, Integer lineNumber, int indent) {
            lines.add(new ASTListingLine(s, lineNumber, indent));
        }

        @Override
        public String toString() {
            for (int i = 0; i < lines.size(); i++) {
                formatLine(i, lines.get(i));
            }

            return sb.toString();
        }

        private void formatLine(int astCount, ASTListingLine l) {
            if (l.getLineNumber() != null) {
                sb.append(String.format("[ %4d ] ", l.getLineNumber()));
            } else {
                sb.append("         ");
            }
            int indent = l.getIndent();
            for (int i = 0; i < indent - 1; i++) {
                if (checkBar(astCount, i)) {
                    sb.append("|   ");
                } else {
                    sb.append("    ");
                }
            }
            if (indent > 0) {
                sb.append("\\-- ");
            }
            sb.append(l.getText());
            sb.append(System.lineSeparator());
        }

        private boolean checkBar(int astIndex, int currentIndent) {
            for (int i = astIndex + 1; i < lines.size(); i++) {
                int indent = lines.get(i).getIndent() - 1;
                if (indent <= currentIndent) {
                    return indent == currentIndent;
                }
            }
            return false;
        }

    }

    private static String list(ASTAware node) {
        ASTListing listing = new ASTListing();
        list(node, listing, 0);
        return listing.toString();
    }

    private static void list(ASTAware node, ASTListing listing, int indent) {
        listing.addIndented(node.getASTNodeText(), node.getASTLineNum(), indent);
        List<ASTAware> children = node.getASTChildren();
        if (children != null) {
            for (ASTAware child : children) {
                list(child, listing, indent + 1);
            }
        }
    }

    @Override
    public String getASTNodeText() {
        return super.getASTNodeText() + " " + (name != null ? name : null);
    }

}
