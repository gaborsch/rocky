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
public class ComparisonExpression extends CompoundExpression {

    public enum ComparisonType {
        EQUALS("=="),
        NOT_EQUALS("!="),
        GREATER_THAN(">"),
        LESS_THAN("<"),
        GREATER_OR_EQUALS(">="),
        LESS_OR_EQUALS("<=");

        private final String sign;

        ComparisonType(String sign) {
            this.sign = sign;
        }

        public String getSign() {
            return sign;
        }

        public ComparisonType negated() {
            switch (this) {
                case EQUALS:
                    return NOT_EQUALS;
                case NOT_EQUALS:
                    return EQUALS;
                case GREATER_THAN:
                    return LESS_OR_EQUALS;
                case LESS_OR_EQUALS:
                    return GREATER_THAN;
                case LESS_THAN:
                    return GREATER_OR_EQUALS;
                case GREATER_OR_EQUALS:
                    return LESS_THAN;
            }
            return null;
        }
    }

    private ComparisonType type;

    public ComparisonExpression(ComparisonType type) {
        super(Precedence.COMPARISON);
        this.type = type;
    }

    @Override
    public String getFormat() {
        return "(%s " + type.getSign() + " %s)";
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
        Value v2 = expr2.evaluate(ctx);
        switch (type) {
            case EQUALS:
                return ctx.afterExpression(this, v1.isEquals(v2));
            case NOT_EQUALS:
                return ctx.afterExpression(this, v1.isNotEquals(v2));
            case LESS_THAN:
                return ctx.afterExpression(this, v1.isLessThan(v2));
            case LESS_OR_EQUALS:
                return ctx.afterExpression(this, v1.isLessOrEquals(v2));
            case GREATER_THAN:
                return ctx.afterExpression(this, v1.isGreaterThan(v2));
            case GREATER_OR_EQUALS:
                return ctx.afterExpression(this, v1.isGreaterOrEquals(v2));
        }
        return ctx.afterExpression(this, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ComparisonExpression) {
            ComparisonExpression o = (ComparisonExpression) obj;
            return type == o.type && super.equals(obj);
        }
        return false;
    }

    @Override
    public String getASTNodeText() {
        return type.toString();
    }

}
