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
public class AssignmentStatement extends Statement {

    private final VariableReference variable;
    private final Expression expression;
    private final ReferenceExpression ref;

    public AssignmentStatement(VariableReference variable, Expression expression) {
        this.expression = expression;
        this.variable = variable;
        this.ref = null;
    }

    public AssignmentStatement(ReferenceExpression ref, Expression expression) {
        if (! (ref.getBaseExpression() instanceof VariableReference)) {
            throw new ParseException("Assignment is not possible to a non-variable expression: " +ref.getBaseExpression(), getLine());
        }
        this.expression = expression;
        this.variable = null;
        this.ref = ref;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value value = expression.evaluate(ctx);
        if (this.variable != null) {
            ctx.setVariable(this.variable, value);
        } else {
            // the array reference
            VariableReference vref = (VariableReference) ref.getBaseExpression();
            // the index expression
            Expression indexExpr = ref.getIndexExpression();
            // evaluate the index
            Value indexValue = indexExpr.evaluate(ctx);
            // fetch the base variable
            Value baseValue = ctx.getVariableValue(vref);
            // assign the value to the specified index
            Value newBaseValue = baseValue.assign(ref.getRefType(), indexValue, value);
            // save the new base variable object if changed
            if (newBaseValue != baseValue) {
                ctx.setVariable(vref, newBaseValue);
            }
        }
    }

    @Override
    protected String explain() {
        return (ref != null ? ref : variable).format() + " := " + expression.format();
    }
}
