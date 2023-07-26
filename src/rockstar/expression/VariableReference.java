/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.parser.Keyword;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Environment;
import rockstar.runtime.Value;
import rockstar.statement.FunctionBlock;

/**
 *
 * @author Gabor
 */
public class VariableReference extends SimpleExpression {

    public static boolean isSelfReference(String ref) {
        return Keyword.SELF.matches(ref);
    }

    public static boolean isParentReference(String ref) {
        return Keyword.PARENT.matches(ref);
    }

    private static boolean isLastVariableReference(String ref) {
        return Keyword.IT.matches(ref);
    }

    public static VariableReference getInstance(String name) {
        if (isSelfReference(name) || isParentReference(name)) {
            return new SelfVariableReference(name);
        }
        if (isLastVariableReference(name)) {
            return new LastVariableReference(name);
        }
        return new VariableReference(name);
    }

    private final String name;
//    private boolean isFunctionName = false;

    protected VariableReference(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        // the effective variable reference is <this>
        return evaluate(ctx, this);
    }

    protected Value evaluate(BlockContext ctx, VariableReference vref) {
        Value value = ctx.getVariableValue(vref);

        if (value == null) {
            // is it a function reference?
            BlockContext funcCtx = ctx.getContextForFunction(vref.name);
            if (funcCtx != null) {
            	if(Environment.get().isStrictMode()) {
            		value = Value.BOOLEAN_TRUE;
            	} else {
            		FunctionBlock function = funcCtx.retrieveLocalFunction(vref.name);
            		value = Value.getValue(function);
            	}
            }
        }

        if (value == null) {
            value = Value.MYSTERIOUS;
            ctx.setVariable(vref, value);
        }

        return ctx.afterExpression(vref, value);
    }


    @Override
    public String format() {
        return "<" + name + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof VariableReference) {
            VariableReference o = (VariableReference) obj;
            return name.equals(o.name);
        }
        return false;
    }

    public VariableReference getEffectiveVref(BlockContext ctx) {
        return this;
    }

}
