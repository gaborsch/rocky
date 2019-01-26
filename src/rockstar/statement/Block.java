/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gabor
 */
public class Block extends Statement {

    private Block parent;
    
    private List<Statement> statements = new ArrayList<>();
    
    public List<Statement> getStatements() {
        return statements;
    }

    public void addStatement(Statement stmt) {
        if (! stmt.applyTo(this)) {
            throw new RuntimeException(stmt.getClass().getSimpleName() + " cannot be applied to the block");
        }
        statements.add(stmt);
    }
    
    public Statement lastStatement() {
        if(statements.size() > 0) {
            statements.get(statements.size()-1);
        }
        return null;
    }

    public void setParent(Block parent) {
        this.parent = parent;
    }

    public Block getParent() {
        return parent;
    }

    /**
     * Called when a block is closed
     * @return 
     */
    public boolean blockClosed() {
        return true;
    }
}
