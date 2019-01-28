/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.NumericValue;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class ConstantExpression extends SimpleExpression {

    public static ConstantExpression CONST_MYSTERIOUS = new ConstantExpression(Value.MYSTERIOUS);
    public static ConstantExpression CONST_NULL = new ConstantExpression(Value.NULL);
    public static ConstantExpression CONST_TRUE = new ConstantExpression(Value.BOOLEAN_TRUE);
    public static ConstantExpression CONST_FALSE = new ConstantExpression(Value.BOOLEAN_FALSE);

    private final Value value;

    public ConstantExpression(String s) {
        this.value = Value.getValue(s);
    }

    public ConstantExpression(NumericValue n) {
        this.value = Value.getValue(n);
    }

    public ConstantExpression(int n) {
        this.value = Value.getValue(NumericValue.getValueFor(n));
    }

    private ConstantExpression(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

}
