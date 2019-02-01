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

    private VariableReference variable;
    private Expression expression;

    public AssignmentStatement(VariableReference variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return super.toString()
                + "\n    " + variable + " := " + expression;
    }

    @Override
    public void execute(BlockContext ctx) {
        String name = this.variable.getName();
        Value value = expression.evaluate(ctx);
        ctx.setVariable(name, value);
        ctx.logStatement(this, null);
    }

    @Override
    public String explain(BlockContext ctx) {
        return variable.getName() + " = " + ctx.getVariableValue(variable.getName());
    }
}
