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
public class Token {

    int lnum;
    int pos;
    int len;
    String value;

    public Token(int lnum, int pos, String value) {
        this.lnum = lnum;
        this.pos = pos;
        this.len = value.length();
        this.value = value;
    }

    public Token(int lnum, int pos, int len, String value) {
        this.lnum = lnum;
        this.pos = pos;
        this.len = len;
        this.value = value;
    }

    public int getLnum() {
        return lnum;
    }

    public int getPos() {
        return pos;
    }

    public String getValue() {
        return value;
    }

    public int getLen() {
        return len;
    }

    @Override
    public String toString() {
        return "\"" + value + "\" [L" + lnum + ", P" + pos + "+" + len + "]";
    }

}
