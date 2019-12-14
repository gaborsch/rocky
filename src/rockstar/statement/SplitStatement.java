/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import rockstar.expression.Expression;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class SplitStatement extends Statement {

    private final Expression valueExpr;
    private final Expression separatorExpr;
    private final Expression targetReference;

    public SplitStatement(Expression valueExpr, Expression separatorExpr, Expression targetReference) {
        this.valueExpr = valueExpr;
        this.separatorExpr = separatorExpr;
        this.targetReference = targetReference;

    }

    @Override
    public void execute(BlockContext ctx) {
        // evaluate string
        Value value = valueExpr.evaluate(ctx);
        String strValue = value.getString();
        // evaluate separator, use default if not present
        String sep = (separatorExpr == null) ? "" : separatorExpr.evaluate(ctx).getString();
        // SPLIT the string into array
        String[] array = strValue.split(sep);
        // convert array into List<Value>
        List<Value> list = new ArrayList<>(array.length);
        Arrays.stream(array).forEach(
                s -> list.add(Value.getValue(s)));
        // create array Value
        Value arrayValue = Value.getValue(list);
        // assign the value to the variable
        Expression target = targetReference != null ? targetReference : valueExpr;
        AssignmentStatement.assign(target, arrayValue, ctx);
        
    }

    @Override
    protected String explain() {
        return "split" + valueExpr.format()
                + (targetReference == null ? " " : "into" + targetReference.format())
                + (separatorExpr == null ? " " : "with" + separatorExpr.format());
    }
}
