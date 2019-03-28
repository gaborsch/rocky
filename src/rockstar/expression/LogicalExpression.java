/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class LogicalExpression extends CompoundExpression {

    public enum LogicalType {
        AND,
        OR,
        NOR
    }

    private final LogicalType type;

    public LogicalExpression(LogicalType type) {
        super();
        this.type = type;
    }

    public LogicalType getType() {
        return type;
    }

    @Override
    public String getFormat() {
        return "(%s " + type + " %s)";
    }

    @Override
    public int getPrecedence() {
        return 800;
    }

    @Override
    public int getParameterCount() {
        return 2;
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        Expression expr1 = this.getParameters().get(0);
        Expression expr2 = this.getParameters().get(1);
        Value v1 = expr1.evaluate(ctx);
        // short circuit: do not evaluate expr2 if not needed
        switch (type) {
            case AND:
                if (v1.asBoolean().equals(Value.BOOLEAN_TRUE)) {
                    return ctx.afterExpression(this, v1.and(expr2.evaluate(ctx)));
                }
                return ctx.afterExpression(this, v1);
            case OR:
                if (v1.asBoolean().equals(Value.BOOLEAN_FALSE)) {
                    return ctx.afterExpression(this, v1.or(expr2.evaluate(ctx)));
                }
                return ctx.afterExpression(this, v1);
            case NOR:
                if (v1.asBoolean().equals(Value.BOOLEAN_FALSE)) {
                    return ctx.afterExpression(this, v1.nor(expr2.evaluate(ctx)));
                }
                return ctx.afterExpression(this, Value.BOOLEAN_FALSE);
        }
        return ctx.afterExpression(this, null);
    }
}
