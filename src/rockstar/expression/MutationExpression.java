/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.LinkedList;
import java.util.List;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;
import rockstar.statement.ASTValues;

/**
 *
 * @author Gabor
 */
public class MutationExpression extends CompoundExpression {

    private final Expression baseExpr;
    private final Expression withExpr;
    private final VariableReference intoExpr;

    /**
     * Standalone usage: "mutate `variable`"
     *
     * @param vref
     */
    public MutationExpression(VariableReference vref) {
        super(vref);
        this.baseExpr = vref;
        this.withExpr = null;
        this.intoExpr = vref;
    }

    /**
     * With usage: "Mutate `variable`" with `expression`"
     *
     * @param withExpression
     */
    public MutationExpression(WithExpression withExpression) {
        super();
        // With usage
        List<Expression> withParams = ((WithExpression) withExpression).getParameters();
        Expression withParam0 = withParams.get(0);
        if (withParam0 instanceof VariableReference) {
            this.baseExpr = (VariableReference) withParam0;
            this.withExpr = withParams.get(1);
            this.intoExpr = (VariableReference) withParam0;
            addParameter(this.baseExpr);
            addParameter(this.withExpr);
        } else {
            throw new RockstarRuntimeException("Cannot use " + withParam0 + " as target for 'into'");
        }
    }

    /**
     * Into usage: "Mutate `expression` into `variable`"
     *
     * Into-with usage: "Mutate `expression` into `variable` with `expression`"
     *
     * With-Into usage: "Mutate `expression` with `expression`into `variable`"
     *
     * @param intoExpression
     */
    public MutationExpression(IntoExpression intoExpression) {
        super();
        List<Expression> intoParams = intoExpression.getParameters();
        Expression intoParam0 = intoParams.get(0);
        Expression intoParam1 = intoParams.get(1);
        if (intoParam1 instanceof WithExpression) {
            // Into-With usage
            List<Expression> withParams = ((WithExpression) intoParam1).getParameters();
            Expression withParam0 = withParams.get(0);
            if (withParam0 instanceof VariableReference) {
                this.baseExpr = intoParam0;
                this.intoExpr = (VariableReference) withParam0;
                this.withExpr = withParams.get(1);
                addParameter(this.baseExpr);
                addParameter(this.intoExpr);
                addParameter(this.withExpr);
            } else {
                throw new RockstarRuntimeException("Cannot use " + withParam0 + " as target for 'into'");
            }
        } else if (intoParam0 instanceof WithExpression) {
            // With-Into usage
            List<Expression> withParams = ((WithExpression) intoParam0).getParameters();
            if (intoParam1 instanceof VariableReference) {
                this.baseExpr = withParams.get(0);
                this.withExpr = withParams.get(1);
                this.intoExpr = (VariableReference) intoParam1;
                addParameter(this.baseExpr);
                addParameter(this.intoExpr);
                addParameter(this.withExpr);
            } else {
                throw new RockstarRuntimeException("Cannot use " + intoParam1 + " as target for 'into'");
            }
        } else if (intoParam1 instanceof VariableReference) {
            // Into usage
            this.baseExpr = intoParam0;
            this.withExpr = null;
            this.intoExpr = (VariableReference) intoParam1;
            addParameter(this.baseExpr);
            addParameter(this.intoExpr);
        } else {
            throw new RockstarRuntimeException("Cannot use " + intoParam1 + " as target for 'into'");
        }
    }

    public Expression getSourceExpr() {
        return baseExpr;
    }

    public Expression getParameterExpr() {
        return withExpr;
    }

    public VariableReference getTargetReference() {
        return intoExpr;
    }

    @Override
    public int getPrecedence() {
        return 150;
    }

    @Override
    public int getParameterCount() {
        throw new UnsupportedOperationException("Invalid call");
    }

    @Override
    public String getFormat() {
        if (withExpr == null) {
            if (intoExpr == null) {
                return "%s";
            }
            return "%s into %s";
        }
        if (intoExpr == null) {
            return "%s with %s";
        }
        return "%s into %s with %s";
    }

    @Override
    public List<Expression> getParameters() {
        List<Expression> params = new LinkedList<>();
        params.add(baseExpr);
        if (intoExpr != null) {
            params.add(intoExpr);
        }
        if (withExpr != null) {
            params.add(withExpr);
        }
        return params;
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        // by default we evaluate the base expression
        return baseExpr.evaluate(ctx);
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return ASTValues.of(baseExpr, withExpr, intoExpr);
    }

}
