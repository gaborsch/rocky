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

    public static ConstantExpression CONST_MYSTERIOUS = new ConstantExpression(Value.MYSTERIOUS);
    public static ConstantExpression CONST_NULL = new ConstantExpression(Value.NULL);
    public static ConstantExpression CONST_EMPTY_STRING = new ConstantExpression(Value.getValue(""));
    public static ConstantExpression CONST_EMPTY_ARRAY = new ConstantExpression(Value.EMPTY_ARRAY);
    public static ConstantExpression CONST_TRUE = new ConstantExpression(Value.BOOLEAN_TRUE);
    public static ConstantExpression CONST_FALSE = new ConstantExpression(Value.BOOLEAN_FALSE);

    private final Value value;

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
    
    

}
