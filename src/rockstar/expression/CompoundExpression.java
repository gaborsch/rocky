/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import rockstar.runtime.Utils;

/**
 *
 * @author Gabor
 */
public abstract class CompoundExpression extends Expression {

    private final int paramCount;
    private final List<Expression> parameters;

    public CompoundExpression(Expression... params) {
        this.paramCount = params.length;
        parameters = params.length > 0 ? Arrays.asList(params) : new LinkedList<>();
    }

    public void addParameter(Expression parameter) {
        parameters.add(parameter);
    }

    public void addParameterReverse(Expression parameter) {
        parameters.add(0, parameter);
    }

    public void setupFinished() {
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    /* Precedences
    999: $ (expression end)
    800: and, or, nor
    700: is, isn't, >, <, >=, <=
    600: not
    500: +, -
    400: *, /
    300: ^ (power)
    200: function call
    100: unary minus
     */
    public abstract int getPrecedence();

    public abstract int getParameterCount();

    public abstract String getFormat();

    @Override
    public String toString() {
        return String.format(getFormat(), parameters.toArray());
    }

    @Override
    public String format() {
        List<String> formattedParams = new LinkedList<>();
        parameters.forEach((param) -> formattedParams.add(param.format()));
        return String.format(getFormat(), formattedParams.toArray());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CompoundExpression) {
            CompoundExpression o = (CompoundExpression) obj;
            if (!o.getClass().equals(this.getClass())) {
                return false;
            }
            return paramCount == o.paramCount && Utils.isListEquals(parameters, o.parameters);
        }
        return false;
    }

    
}
