/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.expression.Expression;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class IfStatement extends Block {

    private final Expression condition;
    private ElseStatement elseStatement = null;

    public IfStatement(Expression condition) {
        this.condition = condition;
    }

    public Expression getCondition() {
        return condition;
    }

    void setElseStatement(ElseStatement elseStmt) {
        this.elseStatement = elseStmt;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value v = condition.evaluate(ctx);
        if (v.getBool()) {
            super.execute(ctx);
        } else if (elseStatement != null) {
            elseStatement.executeElse(ctx);
        }
    }

    @Override
    public List<ASTAware> getASTChildren() {
        List<ASTAware> astValues = ASTValues.of(condition);
        astValues.addAll(super.getASTChildren());
        return astValues;
    }

}
