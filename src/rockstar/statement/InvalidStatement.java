/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.parser.ParseException;

/**
 *
 * @author Gabor
 */
public class InvalidStatement extends Statement {

    public InvalidStatement() {
        var l = getLine();
        throw new ParseException("Statement parsing in "+l.getFileName()+" at line " + l.getLnum() + ":\n" + l.getOrigLine(), l);
    }

}
