/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser;

/**
 *
 * @author Gabor
 */
public class ParserError {

    private final Line line;
    private final int pos;
    private final String msg;

    public ParserError(Line line, int pos, String msg) {
        this.line = line;
        this.pos = pos;
        this.msg = msg;
    }

    public Line getLine() {
        return line;
    }

    public int getPos() {
        return pos;
    }

    public String getMsg() {
        return msg;
    }
}
