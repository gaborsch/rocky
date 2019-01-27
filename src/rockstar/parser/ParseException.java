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
public class ParseException extends RuntimeException {
    
    private final Line line;

    public ParseException(String message, Line line) {
        super(message + " at line "+line.getLnum());
        this.line = line;
    }

    public ParseException(String message) {
        super(message);
        this.line = null;
    }

    public Line getLine() {
        return line;
    }
}
