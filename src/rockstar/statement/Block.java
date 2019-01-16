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
    private int level = 0;
    
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

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        statements.forEach((statement) -> {
//            sb.append("   |".repeat(level)).append(statement.toString());
//        });
//        return sb.toString();
        return super.toString();
    }
    
    
    
}
