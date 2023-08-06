/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;

import rockstar.expression.MutationExpression;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class JoinStatement extends Statement {

    final MutationExpression expr;

    public JoinStatement(MutationExpression expr) {
        this.expr = expr;
    }

    @Override
    public void execute(BlockContext ctx) {
        // evaluate array
        Value value = expr.getSourceExpr().evaluate(ctx);
        List<Value> arrayValue = value.asListArray();
        // evaluate separator, use default if not present
        String sep = (expr.getParameterExpr() == null) ? "" : expr.getParameterExpr().evaluate(ctx).getString();
        // JOIN the array into a string
        StringBuilder sb = new StringBuilder();
        arrayValue.stream()
                .filter(v -> (!v.isNull() && !v.isMysterious()))
                .forEachOrdered(v -> {
                    if (sb.length() > 0) {
                        sb.append(sep);
                    }
                    sb.append(v.getString());
                });
        // get string vale
        Value stringValue = Value.getValue(sb.toString());

        // assign the value to the variable
        AssignmentStatement.assign(expr.getTargetReference(), stringValue, ctx);

    }
    
    @Override
    public void accept(StatementVisitor visitor) {
    	visitor.visit(this);
    }
}
