/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.Rockstar;
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
        boolean isInfiniteLoopsAllowed = ctx.getEnv().hasOption("--infinite-loops");
        Value v = condition.evaluate(ctx);
        boolean lastCondition = v.asBoolean().getBool() ^ negateCondition;
        while (lastCondition && (isInfiniteLoopsAllowed || loopCount <= Rockstar.MAX_LOOP_ITERATIONS)) {
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

            // breakpoint before the next evaluation
            ctx.beforeStatement(this);
            v = condition.evaluate(ctx);
            lastCondition = canContinue && (v.asBoolean().getBool() ^ negateCondition);
        }
        if (!isInfiniteLoopsAllowed && loopCount > Rockstar.MAX_LOOP_ITERATIONS) {
            throw new RockstarRuntimeException("Loop exceeded " + Rockstar.MAX_LOOP_ITERATIONS + " iterations");
        }
    }

    @Override
    protected String explain() {
        if (negateCondition) {
            return "until " + condition.format();
        } else {
            return "while " + condition.format();
        }
    }

}
