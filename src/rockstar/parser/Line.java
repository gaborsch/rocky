/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Gabor
 */
public class Line {

    private final String line;
    private final String origLine;
    private final String fileName;
    private final int lnum;
    private final List<Token> tokens;

    public static Line STARTER_LINE;

    static {
        STARTER_LINE = new Line("", "-", 0, new ArrayList<>());
    }

    public Line(String line, String fileName, int lnum, List<Token> tokens) {
        this.line = line;
        this.origLine = line;
        this.fileName = fileName;
        this.lnum = lnum;
        this.tokens = tokens;
    }

    public String getLine() {
        return line;
    }

    public String getOrigLine() {
        return origLine;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLnum() {
        return lnum;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    @Override
    public String toString() {
        return this.origLine;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.origLine);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Line other = (Line) obj;
        if (this.lnum != other.lnum) {
            return false;
        }
        return Objects.equals(this.fileName, other.fileName);
    }

}
