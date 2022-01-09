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

import rockstar.runtime.ASTAware;
import rockstar.runtime.Utils;

/**
 *
 * @author Gabor
 */
public abstract class CompoundExpression extends Expression {
	
	public enum Precedence {
		END_OF_EXPRESSION(999), // $ (expression end)
		COMPOUND_ASSIGNMENT(900), // +=, -=, etc
		LOGICAL(800), // and, or, nor
		COMPARISON(700), // is, isn't, >, <, >=, <=
		NEGATION(600), // not
		INTO(550), // into (mutation modifier)
		ADDITION(500), // +, -
		MULTIPLICATION(400), // *, /
		POWER(300), // ^ (power)
		FUNCTION_CALL(200), // function call
		MUTATION(150), // mutation
		LIST_OPERATOR(100), // , (list operator)
		UNARY_MINUS(80), // - (unary minus)
		ROLL(75), // roll <array>
		BUILTIN_FUNCTION(60), // built-in functions
		QUALIFIER(50), // on, by, in, at, to, for, from, near
		INSTANCE_CHECK(40) // is like
		;

		private int value;

		Precedence(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
		
		public boolean isGreaterThan(Precedence other) {
			return this.value > other.value;
		}
	}
	
	 
	private Precedence precedence;

    private final int paramCount;
    private final List<Expression> parameters;

    public CompoundExpression(Precedence precedence, Expression... params) {
    	this.precedence = precedence;
        this.paramCount = params.length;
        parameters = params.length > 0 ? Arrays.asList(params) : new LinkedList<>();
    }

    public void addParameter(Expression parameter) {
        parameters.add(parameter);
    }

    public void addParameterReverse(Expression parameter) {
        parameters.add(0, parameter);
    }

    public CompoundExpression setupFinished() {
        return this;
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    public Precedence getPrecedence() {
    	return precedence;
    }
    
    public void setPrecedence(Precedence precedence) {
		this.precedence = precedence;
	}

    public abstract int getParameterCount();

    public abstract String getFormat();

    @Override
    public String toString() {
        return format();
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

    @Override
    public List<ASTAware> getASTChildren() {
        List<ASTAware> astChildren = new ArrayList<>();
        astChildren.addAll(parameters);
        return astChildren;
    }

}
