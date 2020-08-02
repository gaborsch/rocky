/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import rockstar.runtime.FileContext;
import rockstar.runtime.Utils;
import rockstar.statement.AliasStatement;
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
    
    public static Program parseProgram(String programText) {
        InputStream is;
        try {
            is = new ByteArrayInputStream(programText.getBytes("UTF-8"));
            return new Parser(is, "").parse();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

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
        Line l = Line.STARTER_LINE;
        Stack<Block> blocks = new Stack();
        blocks.push(prg);
        try {
            while ((line = rdr.readLine()) != null) {
                l = new Line(line, filename, rdr.getLnum());
                Block currentBlock = blocks.peek();
                Statement stmt = StatementFactory.getStatementFor(l, currentBlock);
                if (stmt instanceof AliasStatement) {
                    AliasStatement aliasStmt = (AliasStatement) stmt;
                    currentBlock.defineAlias(aliasStmt.getAlias(), aliasStmt.getKeyword());
                } 
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
                        currentBlock.addStatement(stmt);
                    } else {
                        parseError("Statement out of block", l);
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
            parseError(ex.getClass().getSimpleName() + ": " + ex.getMessage(), l);
        }

        return prg;
    }

    private void parseError(String msg, Line l) {
        // System.err.println("parse error: " + msg);
        throw new ParseException(msg, l);
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
