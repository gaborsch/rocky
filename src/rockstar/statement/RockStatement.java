/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import rockstar.expression.Expression;
import rockstar.expression.ListExpression;
import rockstar.expression.VariableReference;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class RockStatement extends Statement {

    private final VariableReference variable;
    private final Expression expression;

    public RockStatement(VariableReference variable, Expression expression) {
        this.expression = expression;
        this.variable = variable;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value arrayValue = variable.evaluate(ctx);
        if (arrayValue == null) {
            throw new RockstarRuntimeException("Rocking into a nonexistent variable: " + variable);
        } else if (arrayValue.isNumeric() || arrayValue.isString() || arrayValue.isObject() || arrayValue.isBoolean()) {
            arrayValue = Value.getValue(Arrays.asList(arrayValue));
        } else if (arrayValue.isNull()) {
            arrayValue = Value.getValue(Arrays.asList());
        }
            
        if (arrayValue.isArray()) {
            if (expression instanceof ListExpression) {
                List<Value> exprValues = new LinkedList<>();
                ListExpression listExpr = (ListExpression) expression;
                listExpr.getParameters().forEach(expr1 -> {
                    exprValues.add(expr1.evaluate(ctx));
                });
                ctx.setVariable(this.variable, arrayValue.plus(Value.getValue(exprValues)));
            } else {
                Value value = expression.evaluate(ctx);
                ctx.setVariable(this.variable, arrayValue.plus(value));
            }
        } else {
            throw new RockstarRuntimeException("Rocking into a non-array type: " + arrayValue.getType());
        }
    }

    @Override
    protected String explain() {
        return "rock " + expression.format() + " into " + variable.format();
    }
}
