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

/**
 *
 * @author Gabor
 */
public class ExpressionStatement extends Statement {

    final Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void execute(BlockContext ctx) {
        //discard value
        expression.evaluate(ctx);
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
