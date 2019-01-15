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

    public void addStatement(Statement stmt) {
        statements.add(stmt);
    }
    
    public Statement lastStatement() {
        if(statements.size() > 0) {
            statements.get(statements.size()-1);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Statement statement : statements) {
            sb.append(statement.toString());
        }
        return sb.toString();
    }
    
    
    
}
