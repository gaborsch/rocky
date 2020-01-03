/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.Arrays;
import java.util.List;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class VariableReference extends SimpleExpression {

    public static boolean isSelfReference(String ref) {
        return "self".equals(ref)
                || "myself".equals(ref)
                || "yourself".equals(ref)
                || "himself".equals(ref)
                || "herself".equals(ref)
                || "itself".equals(ref)
                || "ourselves".equals(ref)
                || "yourselves".equals(ref)
                || "themselves".equals(ref);
    }

    public static boolean isParentReference(String ref) {
        return "parent".equals(ref)
                || "father".equals(ref)
                || "mother".equals(ref)
                || "papa".equals(ref)
                || "mama".equals(ref);
    }

    private static final List<String> LAST_NAMED_VARIABLE_REFERENCE_KEYWORDS = Arrays.asList(new String[]{
        "it", "he", "she", "him", "her", "they", "them", "ze", "hir", "zie", "zir", "xe", "xem", "ve", "ver",
        "It", "He", "She", "Him", "Her", "They", "Them", "Ze", "Hir", "Zie", "Zir", "Xe", "Xem", "Ve", "Ver"});

    public static boolean isLastVariableReference(String ref) {
        return (LAST_NAMED_VARIABLE_REFERENCE_KEYWORDS.contains(ref));
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
                value = Value.BOOLEAN_TRUE;
            }
        }

        if (value == null) {
            value = Value.MYSTERIOUS;
            ctx.setVariable(vref, value);
        }

        return ctx.afterExpression(vref, value);
    }

    private boolean isSelfReference() {
        return false;
    }

    @Override
    public String format() {
        return name;
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
