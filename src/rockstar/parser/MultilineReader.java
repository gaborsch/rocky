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
    char commentStyle;
    boolean isInQuote = false;
    boolean isInQuoteAfterBackslash = false;
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
        isInQuoteAfterBackslash = false;
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
        while (!tokens.isEmpty() 
        		&& tokens.get(tokens.size()-1).getValue().equals(",")) {
        	tokens.remove(tokens.size()-1);
        }
        return new Line(origLine.toString(), filename, startLnum, tokens);
    }

    private String readBuffer() throws IOException {
        addToken(pos);
        l = rdr.readLine();
        if (l != null) {
            origLine.append(l);
            lnum++;
        }
        return l;
    }

    private void processChar(char c) {
        if (isInComment) {
            // in bracket comment
            if ((commentStyle == '(' && c == ')')
            	|| (commentStyle == '[' && c == ']')
            	|| (commentStyle == '{' && c == '}')) {
                isInComment = false;
            }
            return;
        } 
        if (isInQuote) {
            if (c == '"' && !isInQuoteAfterBackslash) {
                addToken(1);
                isInQuote = false;
            } else {
                isInQuoteAfterBackslash = (c == '\\');
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
                    case '[':
                    case '{':
                        isInComment = true;
                        commentStyle = c;
                        addToken(0);
                        break;
                    case '"':
                        isInQuote = true;
                        isInQuoteAfterBackslash = false;
                        startToken();
                        break;
                    case ' ':
                    case ';':
                    case ':':
                    case '!':
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
            if (" .;:!\t\r\n".indexOf(c) >= 0) {
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
            boolean skipThisToken = false;
            String token = l.substring(tokenStartPos, pos + offset);
            int len = token.length();
            if (isInNumber) {
                // a single dot is not a decimal number
                skipThisToken = token.equals(".");
            } else if (!isInQuote) {
                int nIdx = token.indexOf("'n'");
                while (nIdx >= 0) {
                    if (nIdx > 0) {
                        tokens.add(new Token(lnum, tokenStartPos, nIdx, token.substring(0, nIdx)));
                    }
                    tokens.add(new Token(lnum, tokenStartPos+nIdx, 3, ","));
                    tokenStartPos += nIdx + 3;
                    token = token.substring(nIdx+3);
                    len = token.length();
                    nIdx = token.indexOf("'n'");
                }
                if(len >= 2 && token.substring(len-2).equalsIgnoreCase("'s")) {
                    tokens.add(new Token(lnum, tokenStartPos, len-2, token.substring(0, len-2)));
                    token = "is";
                    tokenStartPos = pos-2;
                    len = token.length();
                }
                if(len >= 3 && token.substring(len-3).equalsIgnoreCase("'re")) {
                    tokens.add(new Token(lnum, tokenStartPos, len-3, token.substring(0, len-3)));
                    token = "are";
                    tokenStartPos = pos-3;
                    len = token.length();
                }

                // apos does not count in a non-string token
                token = token.replaceAll("[']*", "");

                // in case of ", and", the comma is skipped  
                if (token.equalsIgnoreCase("and") 
                        && !tokens.isEmpty()
                        && ",".equals(tokens.get(tokens.size()-1).getValue())) {
                    skipThisToken = true;
                }
            }
            if (! skipThisToken) {
                tokens.add(new Token(lnum, tokenStartPos, len, token));
            }
            tokenStartPos = -1;
        }
    }
}
