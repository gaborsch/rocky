/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class AssignmentStatement extends Statement {

    private final VariableReference variable;
    private final Expression expression;

    public AssignmentStatement(VariableReference variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value value = expression.evaluate(ctx);
        ctx.setVariable(this.variable, value);
    }

    @Override
    protected String explain() {
        return variable.format() + " := " + expression.format();
    }
}
