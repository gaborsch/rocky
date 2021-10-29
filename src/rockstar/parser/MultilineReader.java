/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gabor
 */
public class MultilineReader {

    BufferedReader rdr;
    String filename;
    
    int lnum;
    String l;
    StringBuilder origLine;
    
    List<Token> tokens;
    int pos;
    int tokenStartPos = -1;

    boolean isInComment = false;
    boolean isInQuote = false;
    boolean isInNumber = false;
    boolean wasApos = false;
    boolean isCommentToEOL = false;

    public MultilineReader(BufferedReader bufferedReader, String filename) {
        this.rdr = bufferedReader;
        this.filename = filename;
        this.lnum = 0;
    }

    public Line readLine() throws IOException {
        tokens = new ArrayList<>();
        isInComment = false;
        isInQuote = false;
        isInNumber = false;
        wasApos = false;
        isCommentToEOL = false;
        origLine = new StringBuilder();
        if (readBuffer() == null) {
            return null;
        }
        int startLnum = lnum;
        while (l != null) {
            for (pos = 0; pos < l.length(); pos++) {
                processChar(l.charAt(pos));
            }
            if (isInComment || isCommentToEOL) {
                readBuffer();
                isCommentToEOL = false;
            } else {
                addToken(0);
                break;
            }
        }
        return new Line(origLine.toString(), filename, startLnum, tokens);
    }

    private String readBuffer() throws IOException {
        addToken(pos);
        l = rdr.readLine();
        if (l != null) {
            origLine.append(l);
            lnum++;
            l = l.replaceAll("[ ,;:]+$", "");
        }
        return l;
    }

    private void processChar(char c) {
        if (isInComment) {
            // in bracket comment
            if (c == ')') {
                isInComment = false;
            }
            return;
        } 
        if (isInQuote) {
            if (c == '"') {
                isInQuote = false;
                addToken(1);
            }
            return;
        } 
        if (isInNumber) {
            if (Character.isDigit(c) || c == '.' || c == 'e' || c == 'E') {
                return;
            }
            // number fully detected, now starts something else, falling through
            addToken(0);
            isInNumber = false;
        } 
        if (tokenStartPos < 0) {
            if (!isCommentToEOL) {
                // normal code text
                switch (c) {
                    case '#':
                        isCommentToEOL = true;
                        addToken(0);
                        break;
                    case '(':
                        isInComment = true;
                        addToken(0);
                        break;
                    case '"':
                        isInQuote = true;
                        startToken();
                        break;
                    case ' ':
                    case '\t':
                    case '\n':
                    case '\r':
                        break;
                    default:
                        if(",+-*/&".indexOf(c) >= 0) {
                            startToken();
                            addToken(1);
                        } else {
                            isInNumber = Character.isDigit(c) || c == '.';
                            startToken();
                        }
                }
            }
        } else {
            if (" \t\r\n".indexOf(c) >= 0) {
                // terminal char
                addToken(0);
            } else if(",+-*/&".indexOf(c) >= 0) {
                startToken();
                addToken(1);
            }
        } 

    }

    private void startToken() {
        if (tokenStartPos >= 0) {
            addToken(0);
        }
        tokenStartPos = pos;
    }

    private void addToken(int offset) {
        if (tokenStartPos >= 0) {                        
            String token = l.substring(tokenStartPos, pos + offset);
            int len = token.length();
            if (len >=2) {
                if (token.substring(len-2).equalsIgnoreCase("'s")) {
                    tokens.add(new Token(lnum, tokenStartPos, len-2, token.substring(0, len-2)));
                    token = "is";
                    tokenStartPos = pos-2;
                    len = token.length();
                } else if (len >=3) {
                    if (token.substring(len-3).equalsIgnoreCase("'re")) {
                        tokens.add(new Token(lnum, tokenStartPos, len-3, token.substring(0, len-3)));
                        token = "are";
                        tokenStartPos = pos-3;
                        len = token.length();
                    }
                    int nIdx = token.indexOf("'n'");
                    while (nIdx >= 0) {
                        tokens.add(new Token(lnum, tokenStartPos, nIdx, token.substring(0, nIdx)));
                        tokens.add(new Token(lnum, tokenStartPos+nIdx, 3, "and"));
                        tokenStartPos += nIdx + 3;
                        token = token.substring(nIdx+3);
                        len = token.length();
                        nIdx = token.indexOf("'n'");
                    }
                }
            }
            if (! token.startsWith("\"")) {
                token = token.replaceAll("[']*", "");
            }
            if (token.equalsIgnoreCase("and") 
                    && !tokens.isEmpty()
                    && ",".equals(tokens.get(tokens.size()-1).getValue())) {
                tokens.remove(tokens.size()-1);                
            }
            tokens.add(new Token(lnum, tokenStartPos, len, token));
            tokenStartPos = -1;
        }
    }
    

    
    int prevLineCount = 0;

    public String readLineOrig() throws IOException {
        lnum += prevLineCount;
        prevLineCount = 1;
        String l = rdr.readLine();
        if (l == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean isEmptyLine = true;

        boolean isInQuote = false;
        boolean isInComment = false;
        boolean isCommentToEOL;
        char c;

        while (l != null) {
            isCommentToEOL = false;
            for (int i = 0; i < l.length(); i++) {
                c = l.charAt(i);
                if (isInComment) {
                    // in bracket comment
                    if (c == ')') {
                        isInComment = false;
                        c = ' ';
                    }
                } else if (isInQuote) {
                    if (c == '"') {
                        isInQuote = false;
                    }
                } else if (!isCommentToEOL) {
                    // normal code text
                    switch (c) {
                        case '#':
                            isCommentToEOL = true;
                            break;
                        case '(':
                            isInComment = true;
                            break;
                        case '"':
                            isInQuote = true;
                            break;
                    }
                }
                if (!isInComment && !isCommentToEOL) {
                    sb.append(c);
                    if (c != ' ') {
                        isEmptyLine = false;
                    }
                }
            }
            if (isInComment || (isCommentToEOL && isEmptyLine)) {
                prevLineCount++;
                l = rdr.readLine();
            } else {
                l = null;
            }
        }
        return sb.toString();
    }

    public String readLine2() throws IOException {
        lnum += prevLineCount;
        prevLineCount = 1;
        String l = rdr.readLine();
        if (l == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(l);
        int lastOpen = l.lastIndexOf('(');
        int lastClose = l.lastIndexOf(')');
        boolean isComment = false;
        while (lastOpen > lastClose || (isComment && lastClose == -1)) {
            prevLineCount++;
            l = rdr.readLine();
            if (l == null) {
                break;
            }
            sb.append(l).append(' ');
            lastOpen = l.lastIndexOf('(');
            lastClose = l.lastIndexOf(')');
            isComment = true;
        }
        return sb.toString();
    }

}
