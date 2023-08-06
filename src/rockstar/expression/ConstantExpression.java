/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.BlockContext;
import rockstar.runtime.RockNumber;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class ConstantExpression extends SimpleExpression {

    private final Value value;

	public static ConstantExpression CONST_MYSTERIOUS() {
		return new ConstantExpression(Value.MYSTERIOUS);
	};

	public static ConstantExpression CONST_NULL() {
		return new ConstantExpression(Value.NULL);
	};

	public static ConstantExpression CONST_EMPTY_STRING() {
		return new ConstantExpression(Value.getValue(""));
	};

	public static ConstantExpression CONST_EMPTY_ARRAY() {
		return new ConstantExpression(Value.EMPTY_ARRAY);
	};

	public static ConstantExpression CONST_TRUE() {
		return new ConstantExpression(Value.BOOLEAN_TRUE);
	};

	public static ConstantExpression CONST_FALSE() {
		return new ConstantExpression(Value.BOOLEAN_FALSE);
	};

    
    public ConstantExpression(String s) {
        this.value = Value.getValue(s);
    }

    public ConstantExpression(RockNumber n) {
        this.value = Value.getValue(n);
    }

    public ConstantExpression(long n) {
        this.value = Value.getValue(n);
    }

    public ConstantExpression(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        return ctx.afterExpression(this, value);
    }

    @Override
    public String format() {
        return this.value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ConstantExpression) {
            ConstantExpression o = (ConstantExpression) obj;
            return value.equals(o.value);
        }
        return false;
    }
    
    @Override
    public void accept(ExpressionVisitor visitor) {
    	visitor.visit(this);
    }

}
