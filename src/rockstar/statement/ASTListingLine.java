/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

/**
 *
 * @author Gabor
 */
public class ASTListingLine {

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
