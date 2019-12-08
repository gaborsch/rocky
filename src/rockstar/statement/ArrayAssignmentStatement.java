/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.LinkedList;
import java.util.List;
import rockstar.expression.Expression;
import rockstar.expression.ReferenceExpression;
import rockstar.expression.VariableReference;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockNumber;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class ArrayAssignmentStatement extends Statement {

    private final VariableReference variable;
    private final List<Expression> expressionList = new LinkedList<>();

    public ArrayAssignmentStatement(VariableReference variable) {
        this.variable = variable;
    }

    public void addExpression(Expression expr) {
        expressionList.add(expr);
    }

    @Override
    public void execute(BlockContext ctx) {
        // empty array is null
        Value arrayValue = Value.NULL;
        // adding to this index will append to the list
        Value idxValue = Value.getValue(RockNumber.getValue(expressionList.size()));
        for (Expression expr : expressionList) {
            if (expr instanceof ReferenceExpression) {
                // assoc array initialization
                ReferenceExpression refExp = (ReferenceExpression) expr;
                Value baseValue = refExp.getBaseExpression().evaluate(ctx);
                Value indexValue = refExp.getIndexExpression().evaluate(ctx);
                arrayValue = arrayValue.assign(indexValue, baseValue);
            } else {
                Value memberValue = expr.evaluate(ctx);
                arrayValue = arrayValue.assign(idxValue, memberValue);
            }
        }
        ctx.setVariable(this.variable, arrayValue);
    }

    @Override
    protected String explain() {
        StringBuilder sb = new StringBuilder(variable.format());
        sb.append(" := ");
        boolean isFirst = true;
        for (Expression expr : expressionList) {
            if (!isFirst) {
                sb.append(", ");
            }
            sb.append(expr);
            isFirst = false;
        }
        return sb.toString();
    }
}
