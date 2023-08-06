/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;

/**
 *
 * @author Gabor
 */
public class AliasStatement extends Statement {

    final List<String> alias;
    final List<String> keyword;

    public AliasStatement(List<String> alias, List<String> keyword) {
        this.alias = alias;
        this.keyword = keyword;
    }

    public List<String> getAlias() {
        return alias;
    }

    public List<String> getKeyword() {
        return keyword;
    }

    @Override
    public void execute(BlockContext ctx) {
        // alias definition executed immediately by the Parser
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return ASTValues.of(String.join(" ", alias), String.join(" ", keyword));
    }
    
    @Override
    public void accept(StatementVisitor visitor) {
    	visitor.visit(this);
    }

}
