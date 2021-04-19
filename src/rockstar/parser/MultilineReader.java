/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser;

import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author Gabor
 */
public class MultilineReader {

    BufferedReader rdr;
    int lnum;
    int prevLineCount = 0;

    public MultilineReader(BufferedReader bufferedReader) {
        rdr = bufferedReader;
        lnum = 1;
    }

    public int getLnum() {
        return lnum;
    }

    public String readLine() throws IOException {
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
