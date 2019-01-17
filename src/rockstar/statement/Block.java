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
    
    private List<Statement> statements = new ArrayList<>();
    
    public List<Statement> getStatements() {
        return statements;
    }

    public void addStatement(Statement stmt) {
        statements.add(stmt);
    }
    
    public Statement lastStatement() {
        if(statements.size() > 0) {
            statements.get(statements.size()-1);
        }
        return null;
    }

    /**
     * Called when a block is closed
     */
    public void blockClosed() {
    }
}
