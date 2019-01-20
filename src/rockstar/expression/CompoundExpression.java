/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Gabor
 */
public abstract class CompoundExpression extends Expression {

    private final int paramCount;
    private final List<Expression> parameters;

    private CompoundExpression(int paramCount) {
        this.paramCount = paramCount;
        parameters = new ArrayList<>(paramCount);
    }

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
    500: +, -
    400: *, /
    300: ^ (power)
     */
    public abstract int getPrecedence();
    
    protected abstract int getParameterCount();
    
    protected abstract String getFormat();

    @Override
    public String toString() {
        return String.format(getFormat(), parameters.toArray());
    }

}
