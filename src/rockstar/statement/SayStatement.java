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
public class SayStatement extends Statement {

    final Expression expression;

    public SayStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value v = expression.evaluate(ctx);
        ctx.getEnv().getOutput().println(v.asScalar().getString());
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return ASTValues.of(expression);
    }
    
    @Override
    public void accept(StatementVisitor visitor) {
    	visitor.visit(this);
    }
}
