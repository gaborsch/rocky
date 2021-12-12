/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import rockstar.expression.MutationExpression;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class SplitStatement extends Statement {

    private final MutationExpression expr;

    public SplitStatement(MutationExpression expr) {
        this.expr = expr;
    }

    @Override
    public void execute(BlockContext ctx) {
        // evaluate string
        Value value = expr.getSourceExpr().evaluate(ctx);
        String strValue = value.getString();
        // evaluate separator, use default if not present
        String sep = (expr.getParameterExpr() == null) ? "" : expr.getParameterExpr().evaluate(ctx).getString();
        // SPLIT the string into array
        List<String> parts = split(strValue, sep);
        // convert Strings into List<Value>
        List<Value> list = parts.stream().map(s -> Value.getValue(s)).collect(Collectors.toList());
        // create array Value
        Value arrayValue = Value.getValue(list);
        // assign the value to the variable
        AssignmentStatement.assign(expr.getTargetReference(), arrayValue, ctx);

    }

    /**
     * Split a string with a given separator
     *
     * @param orig
     * @param sep
     * @return
     */
    private List<String> split(String orig, String sep) {
        List<String> parts = new LinkedList<>();
        boolean emptySep = sep.isEmpty();
        int sepLen = sep.length();
        int start = 0;
        int len = orig.length();

        while (start < len) {
            int end = (emptySep ? start + 1 : orig.indexOf(sep, start));
            if (end < 0) {
                end = len;
            }
            String part = orig.substring(start, end);
            parts.add(part);
            start = end + sepLen;
        }
        return parts;
    }

    @Override
    protected String explain() {
        return expr.getTargetReference().format() + " = split " + expr.format();
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return ASTValues.of(expr);
    }
}
