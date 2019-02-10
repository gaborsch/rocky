/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.expression.Expression;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarBreakException;
import rockstar.runtime.RockstarContinueException;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class WhileStatement extends Block {

    public static final int MAX_LOOP_ITERATIONS = 200;

    private final Expression condition;
    private boolean negateCondition = false;

    public WhileStatement(Expression condition) {
        this.condition = condition;
    }

    public WhileStatement(Expression condition, boolean negateCondition) {
        this.condition = condition;
        this.negateCondition = negateCondition;
    }

    public Expression getCondition() {
        return condition;
    }

    @Override
    public void execute(BlockContext ctx) {
        int loopCount = 0;
        Value v = condition.evaluate(ctx);
        boolean lastCondition = v.asBoolean().getBool() ^ negateCondition;
        while (lastCondition && loopCount <= MAX_LOOP_ITERATIONS) {
            boolean canContinue = true;
            try {
                super.execute(ctx);
            } catch (RockstarContinueException rce) {
                // continue exits the block, but not the loop
            } catch (RockstarBreakException rbe) {
                // break exits the loop, too
                canContinue = false;
            }
            // other exceptions like ReturnException are falling thru

            loopCount++;

            v = condition.evaluate(ctx);
            lastCondition = canContinue && (v.asBoolean().getBool() ^ negateCondition);
        }
        if (loopCount > MAX_LOOP_ITERATIONS) {
            throw new RockstarRuntimeException("Loop exceeded " + MAX_LOOP_ITERATIONS + " iterations");
        }
    }

    @Override
    protected String list() {
        if (negateCondition) {
            return "until(" + condition.format() + ")";
        } else {
            return "while(" + condition.format() + ")";
        }
    }

}
