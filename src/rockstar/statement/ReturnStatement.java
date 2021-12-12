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
import rockstar.runtime.RockstarReturnException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class ReturnStatement extends Statement {

    private final Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    boolean applyTo(Block block) {
        Block b = block;
        while (b != null) {
            if (b instanceof FunctionBlock) {
                return true;
            }
            b = b.getParent();
        }
        return false;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value value = expression.evaluate(ctx);
        throw new RockstarReturnException(value);
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return ASTValues.of(expression);
    }

}
