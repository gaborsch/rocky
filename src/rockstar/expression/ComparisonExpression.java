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

    }

    private ComparisonType type;

    public ComparisonExpression(ComparisonType type) {
        super();
        this.type = type;
    }

    @Override
    public String getFormat() {
        return "(%s " + type.getSign() + " %s)";
    }

    @Override
    public int getPrecedence() {
        return 700;
    }

    @Override
    public int getParameterCount() {
        return 2;
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        Expression expr1 = this.getParameters().get(0);
        Expression expr2 = this.getParameters().get(1);
        Value v1 = expr1.evaluate(ctx);
        Value v2 = expr2.evaluate(ctx);
        switch (type) {
            case EQUALS:
                return v1.isEquals(v2);
            case NOT_EQUALS:
                return v1.isNotEquals(v2);
            case LESS_THAN:
                return v1.isLessThan(v2);
            case LESS_OR_EQUALS:
                return v1.isLessOrEquals(v2);
            case GREATER_THAN:
                return v1.isGreaterThan(v2);
            case GREATER_OR_EQUALS:
                return v1.isGreaterOrEquals(v2);
        }
        return null;
    }

}
