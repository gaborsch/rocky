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
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rockstar.runtime.BlockStack;
import rockstar.runtime.Environment;
import rockstar.runtime.Utils;
import rockstar.statement.AliasStatement;
import rockstar.statement.Block;
import rockstar.statement.BlockEnd;
import rockstar.statement.ContinuingBlockStatementI;
import rockstar.statement.Program;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class Parser {

    private String filename;
    private MultilineReader rdr;
    private Environment env;

    public static Program parseProgram(String programText, Environment env) {
        InputStream is;
        try {
            is = new ByteArrayInputStream(programText.getBytes("UTF-8"));
            return new Parser(is, "", env).parse();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Parser(String filename, Environment env) throws FileNotFoundException {
        this(new FileInputStream(new File(filename)), filename, env);
    }

    public Parser(InputStream is, String filename, Environment env) {
        this.env = env;
        try {
            this.filename = filename;
            rdr = new MultilineReader(new BufferedReader(new InputStreamReader(is, Utils.UTF8)));
        } catch (UnsupportedEncodingException ex) {
            System.err.println(Utils.UTF8 + " charset is not supported");
        }
    }

    public Parser(String content, String filename) {
        this.filename = filename;
        rdr = new MultilineReader(new BufferedReader(new StringReader(content)));
    }

    public Program parse() {
        Program prg = new Program(filename);

        String line;
        Line l = Line.STARTER_LINE;
        BlockStack blocks = new BlockStack(env);
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
                    stmt = blocks.removeBlock();
                } else {
                    // meaningful statements
                    if (stmt instanceof ContinuingBlockStatementI) {
                        // if it sticks to the previous block, close that block, and append it
                        Block finishedBlock = blocks.pop();
                        boolean appendToParent = ((ContinuingBlockStatementI) stmt).appendTo(finishedBlock);
                        // should it be appended to the previous block?
                        if (appendToParent) {
                            currentBlock = blocks.peek();
                        }
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
}
