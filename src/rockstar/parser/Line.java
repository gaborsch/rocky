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

    private String line;
    private final String origLine;
    private final String fileName;
    private final int lnum;

    private final List<String> tokens = new ArrayList<>();

    public Line(String line, String fileName, int lnum) {
        this.line = line;
        this.origLine = line;
        this.fileName = fileName;
        this.lnum = lnum;
        tokenize();
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

    public List<String> getTokens() {
        return tokens;
    }

    private void tokenize() {
        tokenizeV2();
    }

    private void tokenizeV2() {

        // trim trailing extra chars
        line = line.replaceAll("[ ,;:]+$", "");

        int len = line.length();
        int pos = 0;
        String nextToken = null;

        while (pos < len) {
            switch (line.charAt(pos)) {
                case ' ':
                    pos++;
                    break;
                case '"':
                    int nextQM = line.indexOf('"', pos + 1);
                    if (nextQM < 0) {
                        nextQM = len;
                    }
                    tokens.add(line.substring(pos, nextQM + 1));
                    pos = nextQM + 1;
                    break;
                case '(':
                    int nextCB = line.indexOf(')', pos + 1);
                    if (nextCB < 0) {
                        nextCB = len;
                    }
                    pos = nextCB + 1;
                    break;
                default:
                    int limit = pos;
                    StringBuilder tokenBuilder = new StringBuilder();
                    boolean endOfToken = false;
                    while (!endOfToken) {
                        char cl = (limit < len) ? line.charAt(limit) : '\0';
                        if ("0123456789.".indexOf(cl) >= 0 //                                || (cl == '-' && limit < len - 1 && "0123456789.".indexOf(line.charAt(limit + 1)) >= 0)
                                ) {
                            // regular decimal number (integral or fraction)
                            while (limit < len
                                    && (Character.isDigit(cl) || cl == '.' || cl == 'e' || cl == 'E')) {
                                limit++;
                                if (limit < len) {
                                    cl = line.charAt(limit);
                                }
                            }
                            nextToken = line.substring(pos, limit);
                            pos = limit;
                            endOfToken = true;
                        } else if ("+*/-".indexOf(cl) >= 0) {
                            // single operators are single char
                            limit++;
                            nextToken = line.substring(pos, limit);
                            pos = limit;
                            endOfToken = true;
                        } else {
                            // identifier
                            while (limit < len && Character.isLetterOrDigit(cl)) {
                                limit++;
                                if (limit < len) {
                                    cl = line.charAt(limit);
                                }

                            }
                        }
                        tokenBuilder.append(line.substring(pos, limit));
                        if (limit >= len) {
                            endOfToken = true;
                        } else if (!endOfToken) {
                            char c = line.charAt(limit);
                            String right = line.substring(limit);
                            if (c == '\'') {
                                if (right.matches("'s\\b.*")) {
                                    // "'s" becomes " is "
                                    endOfToken = true;
                                    nextToken = "is";
                                    limit += 2;
                                } else if (right.startsWith("'n'")) {
                                    // "'n'" becomes " and "
                                    endOfToken = true;
                                    nextToken = "and";
                                    limit += 3;
                                } else {
                                    // skip single quote within word
                                    limit++;
                                    pos = limit;
                                }
                            } else if (right.startsWith(", and")) {
                                // double and: skip the first
                                nextToken = ",";
                                endOfToken = true;
                                limit += 5;
                            } else if (c == '&' || c == ',') {
                                // "&" and "," becomes " and "
                                endOfToken = true;
                                nextToken = ",";
                                limit++;
                            } else {
                                // skip character, end token if word separator
                                if (c != '+' && c != '-' && c != '/' && c != '*') {
                                    limit++;
                                } 
                                pos = limit;
                                endOfToken = true;
                            }
                        }
                    }
                    if (tokenBuilder.length() > 0) {
                        tokens.add(tokenBuilder.toString());
                    }
                    pos = limit;
                    break;
            }
            if (nextToken != null) {
                // add second token, if detected
                tokens.add(nextToken);
                nextToken = null;
            }
        }
    }

    /*
    private void tokenizeV1() {

        // trim trailing extra chars
        line = line.replaceAll("[,;:]+$", "");

        // "'n'", ",", "&" is generally replaced by " and "
//        line = line.replace(", and ", " and ").replace(",", " and ").replace("&", " and ").replace("'n'", " and ");
        int len = line.length();
        int pos = 0;

        while (pos < len) {
            switch (line.charAt(pos)) {
                case ' ':
                    pos++;
                    break;
                case '"':
                    int nextQM = line.indexOf('"', pos + 1);
                    if (nextQM < 0) {
                        nextQM = len;
                    }
                    tokens.add(line.substring(pos, nextQM + 1));
                    pos = nextQM + 1;
                    break;
                case '(':
                    int nextCB = line.indexOf(')', pos + 1);
                    if (nextCB < 0) {
                        nextCB = len;
                    }
                    pos = nextCB + 1;
                    break;
                case ',':
                    pos++;
                    if (line.substring(pos).startsWith(" and ")) {
                        pos += 4;
                    }
                    tokens.add("and");
                    break;
                case '&':
                    pos++;
                    tokens.add("and");
                    break;
                default:
                    if (line.substring(pos).startsWith("'n'")) {
                        tokens.add("and");
                        pos += 3;
                    } else {
                        int nextSpc = pos;
                        while (nextSpc < line.length() && Character.isLetter(line.charAt(nextSpc))) {
                            nextSpc++;
                        }
                        String token = purifyToken(line.substring(pos, nextSpc));
                        int tokenSpc = token.indexOf(' ');
                        if (tokenSpc >= 0) {
                            tokens.add(token.substring(0, tokenSpc));
                            tokens.add(token.substring(tokenSpc + 1));
                        } else if (token.length() > 0) {
                            tokens.add(token);
                        }
                        pos = nextSpc + 1;
                    }
                    break;
            }
        }
    }
     */
 /*
    private void tokenizeOrig() {

        // trim trailing extra chars
        line = line.replaceAll("[,;:]+$", "");

        // "'n'", ",", "&" is generally replaced by " and "
        line = line.replace(", and ", " and ").replace(",", " and ").replace("&", " and ").replace("'n'", " and ");

        int len = line.length();
        int pos = 0;

        while (pos < len) {
            switch (line.charAt(pos)) {
                case ' ':
                    pos++;
                    break;
                case '"':
                    int nextQM = line.indexOf('"', pos + 1);
                    if (nextQM < 0) {
                        nextQM = len;
                    }
                    tokens.add(line.substring(pos, nextQM + 1));
                    pos = nextQM + 1;
                    break;
                case '(':
                    int nextCB = line.indexOf(')', pos + 1);
                    if (nextCB < 0) {
                        nextCB = len;
                    }
                    pos = nextCB + 1;
                    break;
                default:
                    int nextSpc = line.indexOf(' ', pos + 1);
                    if (nextSpc < 0) {
                        nextSpc = len;
                    }
                    String token = purifyToken(line.substring(pos, nextSpc));
                    int tokenSpc = token.indexOf(' ');
                    if (tokenSpc >= 0) {
                        tokens.add(token.substring(0, tokenSpc));
                        tokens.add(token.substring(tokenSpc + 1));
                    } else if (token.length() > 0) {
                        tokens.add(token);
                    }
                    pos = nextSpc + 1;
                    break;
            }
        }
    }

    private String purifyToken(String token) {
        String t = token; // .replaceAll("[^a-zA-Z0-9.']", "");
        if (t.endsWith("'s")) {
            t = t.substring(0, t.length() - 2) + " is";
        }
        t = t.replace("'", "");
        return t;
    }
     */
    public String getOrigLineAfter(String token) {
        int pos = origLine.indexOf(token);
        pos += token.length();
        while (pos < origLine.length() && origLine.charAt(pos) == ' ') {
            pos++;
        }
        if (pos < origLine.length()) {
            return origLine.substring(pos);
        }
        return "";
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
        if (!Objects.equals(this.fileName, other.fileName)) {
            return false;
        }
        return true;
    }

    
    

}
