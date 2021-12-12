/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.List;
import rockstar.parser.ParserError;
import rockstar.runtime.ASTAware;
import rockstar.runtime.Utils;

/**
 *
 * @author Gabor
 */
public class ASTListing {

    private final Program prg;
    private final StringBuilder sb = new StringBuilder();
    private final ArrayList<ASTListingLine> lines = new ArrayList<>();

    public ASTListing(Program prg) {
        this.prg = prg;
    }

    public String list() {
        visitNode(prg, 0);
        return this.toString();
    }

    private void visitNode(ASTAware node, int indent) {
        lines.add(new ASTListingLine(node.getASTNodeText(), node.getASTLineNum(), indent));
        List<ASTAware> children = node.getASTChildren();
        if (children != null) {
            children.forEach((ASTAware child) -> {
                visitNode(child, indent + 1);
            });
        }
    }

    @Override
    public String toString() {
        for (int i = 0; i < lines.size(); i++) {
            formatLine(i, lines.get(i));
        }
        return sb.toString();
    }

    private void formatLine(int astCount, ASTListingLine l) {
        ParserError error = null;
        Integer lnum = l.getLineNumber();
        if (lnum != null) {
            error = prg.getErrorOnLine(lnum);
            sb.append(String.format("L%04d  ", lnum));
        } else {
            sb.append("       ");
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
        if (error != null) {
            sb.append(String.format(": %sv--- %s at pos %d", Utils.repeat(" ", error.getPos()), error.getMsg(), error.getPos() + 1));
        }
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
