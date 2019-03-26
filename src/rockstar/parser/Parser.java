/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.util.Stack;
import rockstar.runtime.Utils;
import rockstar.statement.Block;
import rockstar.statement.BlockEnd;
import rockstar.statement.Program;
import rockstar.statement.Statement;
import rockstar.statement.ContinuingBlockStatementI;

/**
 *
 * @author Gabor
 */
public class Parser {

    private String filename;
    private MultilineReader rdr;

    public Parser(String filename) throws FileNotFoundException {
        this(new FileInputStream(new File(filename)), filename);
    }

    public Parser(InputStream is, String filename) {
        try {
            this.filename = filename;
            rdr = new MultilineReader(new BufferedReader(new InputStreamReader(is, Utils.UTF8)));
        } catch (UnsupportedEncodingException ex) {
            System.err.println(Utils.UTF8 + " charset is not supported");
        }
    }

    public Program parse() {
        Program prg = new Program(filename);

        String line;
        Stack<Block> blocks = new Stack();
        blocks.push(prg);
        try {
            while ((line = rdr.readLine()) != null) {
                Statement stmt = StatementFactory.getStatementFor(new Line(line, filename, rdr.getLnum()));
                if (stmt instanceof BlockEnd) {
                    // simple block closing: no need to add it anywhere
                    if (blocks.size() > 1) {
                        Block finishedBlock = blocks.pop();
                    }
                } else {
                    // meaningful statements
                    if (stmt instanceof ContinuingBlockStatementI) {
                        // if it sticks to the previous block, close that block, and append it
                        Block finishedBlock = blocks.pop();
                        ((ContinuingBlockStatementI) stmt).appendTo(finishedBlock);
                    }

                    // append statement to current block
                    if (!blocks.isEmpty()) {
                        blocks.peek().addStatement(stmt);
                    } else {
                        parseError("Statement out of block");
                    }

                    // open a new block if it's a block statement
                    if (stmt instanceof Block) {
                        Block block = (Block) stmt;
                        block.setParent(blocks.peek());
                        blocks.push(block);
                    }

                }
            }
        } catch (IOException ex) {
            parseError(ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }

        return prg;
    }

    private void parseError(String msg) {
        // System.err.println("parse error: " + msg);
        throw new ParseException(msg);
    }

    public static class MultilineReader {

        BufferedReader rdr;
        int lnum;
        int prevLineCount = 0;

        private MultilineReader(BufferedReader bufferedReader) {
            rdr = bufferedReader;
            lnum = 1;
        }

        public int getLnum() {
            return lnum;
        }

        private String readLine() throws IOException {
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

}
