/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.Arrays;
import java.util.List;
import rockstar.expression.Expression;
import rockstar.expression.ListExpression;
import rockstar.expression.VariableReference;
import rockstar.runtime.ASTAware;
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
        this.variable = variable;
        this.expression = expression;
    }

    public RockStatement(VariableReference variable) {
        this.variable = variable;
        this.expression = null;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value arrayValue = ctx.getVariableValue(variable);
        if (arrayValue == null) {
            arrayValue = Value.getValue(Arrays.asList());
        } else if (arrayValue.isNumeric() || arrayValue.isString() || arrayValue.isObject() || arrayValue.isBoolean() || arrayValue.isNull() || arrayValue.isMysterious()) {
            arrayValue = Value.getValue(Arrays.asList(arrayValue));
        } else if (!arrayValue.isArray()) {
            throw new RockstarRuntimeException("Rocking a non-allowed type: " + arrayValue.getType());
        }

        if (expression == null) {
            // in-place array conversion
            ctx.setVariable(this.variable, arrayValue);
        } else if (expression instanceof ListExpression) {
            // multiple values: add them one by one
            ListExpression listExpr = (ListExpression) expression;
            for (Expression expr1 : listExpr.getParameters()) {
                arrayValue = arrayValue.push(expr1.evaluate(ctx));
            }
            ctx.setVariable(this.variable, arrayValue);
        } else {
            // single value
            Value value = expression.evaluate(ctx);
            ctx.setVariable(this.variable, arrayValue.push(value));
        }
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return ASTValues.of(variable, expression);
    }

}
