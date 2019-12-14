/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.expression.Expression;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class JoinStatement extends Statement {

    private final Expression valueExpr;
    private final Expression separatorExpr;
    private final Expression targetReference;

    public JoinStatement(Expression valueExpr, Expression separatorExpr, Expression targetReference) {
        this.valueExpr = valueExpr;
        this.separatorExpr = separatorExpr;
        this.targetReference = targetReference;

    }

    @Override
    public void execute(BlockContext ctx) {
        // evaluate array
        Value value = valueExpr.evaluate(ctx);
        List<Value> arrayValue = value.asListArray();
        // evaluate separator, use default if not present
        String sep = (separatorExpr == null) ? "" : separatorExpr.evaluate(ctx).getString();
        // JOIN the array into a string
        StringBuilder sb = new StringBuilder();
        arrayValue.stream().forEach(
                v -> sb.append(v.getString()).append(sep));
        // get string vale
        String s = sb.substring(0, sb.length() - sep.length());
        Value stringValue = Value.getValue(s);

        // assign the value to the variable
        Expression target = targetReference != null ? targetReference : valueExpr;
        AssignmentStatement.assign(target, stringValue, ctx);

    }

    @Override
    protected String explain() {
        return "split" + valueExpr.format()
                + (targetReference == null ? " " : "into" + targetReference.format())
                + (separatorExpr == null ? " " : "with" + separatorExpr.format());
    }
}
