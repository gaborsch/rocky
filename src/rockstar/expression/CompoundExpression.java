/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.ArrayList;
import java.util.Arrays;
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
        parameters = Arrays.asList(params);
    }

    public void addParameter(Expression parameter) {
        parameters.add(parameter);
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    protected abstract String getFormat();

    @Override
    public String toString() {
        return String.format(getFormat(), parameters.toArray());
    }

}
