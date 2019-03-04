/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.expression.Expression;
import rockstar.expression.ReferenceExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.ParseException;
import rockstar.runtime.BlockContext;
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
        if (this.variable != null) {
            Value varValue = this.variable.evaluate(ctx);
            if (varValue.isListArray()) {
                ctx.setVariable(this.variable, varValue.plus(value));
            } 
        } 
    }

    @Override
    protected String explain() {
        return "push " + expression.format() + " into " + variable.format();
    }
}
