/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.expression.Expression;
import rockstar.runtime.RockstarReturnException;
import rockstar.runtime.BlockContext;
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
    protected String list() {
        return "return " + expression.format();
    }

}
