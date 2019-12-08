/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class PushStatement extends Statement {

    private final VariableReference variable;
    private final Expression expression;

    public PushStatement(VariableReference variable, Expression expression) {
        this.expression = expression;
        this.variable = variable;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value value = expression.evaluate(ctx);
        Value varValue = variable.evaluate(ctx);
        if (varValue == null) {
            throw new RockstarRuntimeException("Pushing into a nonexistent variable: " + variable);
        } else if (varValue.isArray()) {
            ctx.setVariable(this.variable, varValue.plus(value));
        } else {
            throw new RockstarRuntimeException("Pushing into a non-array type: " + varValue.getType());
        }
    }

    @Override
    protected String explain() {
        return "push " + expression.format() + " into " + variable.format();
    }
}
