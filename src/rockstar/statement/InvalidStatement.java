/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.parser.Line;
import rockstar.parser.ParseException;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;

/**
 *
 * @author Gabor
 */
public class InvalidStatement extends Statement {

    public InvalidStatement() {
        Line l = getLine();
        throw new ParseException("Statement parsing in " + l.getFileName() + " at line " + l.getLnum() + ":\n" + l.getOrigLine(), l);
    }

    @Override
    public void execute(BlockContext ctx) {
        throw new UnsupportedOperationException("InvalidStatement not supported.");
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return ASTValues.of(getLine().getOrigLine());
    }
    
    @Override
    public void accept(StatementVisitor visitor) {
    	visitor.visit(this);
    }

}
