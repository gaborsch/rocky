/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

/**
 *
 * @author Gabor
 */
public class NoOpStatement extends Statement {

    public NoOpStatement() {
        var l = getLine();
        throw new RuntimeException("Statement parsing in "+l.getFileName()+" at line " + l.getLnum() + ":\n" + l.getOrigLine());
    }

}
