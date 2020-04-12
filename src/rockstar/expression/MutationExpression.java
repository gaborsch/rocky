/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.LinkedList;
import java.util.List;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

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
        this.baseExpr = vref;
        this.withExpr = null;
        this.intoExpr = vref;
    }

    /**
     * With usage: "Mutate `variable`" with `expression`"
     *
     * @param plusExpression
     */
    public MutationExpression(PlusExpression plusExpression) {
        // With usage
        List<Expression> plusParams = ((PlusExpression) plusExpression).getParameters();
        Expression plusParam0 = plusParams.get(0);
        if (plusParam0 instanceof VariableReference) {
            this.baseExpr = (VariableReference) plusParam0;
            this.withExpr = plusParams.get(1);
            this.intoExpr = (VariableReference) plusParam0;
        } else {
            throw new RockstarRuntimeException("Cannot use " + plusParam0 + " as target for 'into'");
        }
    }

    /**
     * Into usage: "Mutate `expression` into `variable`" 
     * 
     * Into-with usage: "Mutate `expression` into `variable` with `expression`"

     * With-Into usage: "Mutate `expression` with `expression`into `variable`"
     *
     * @param intoExpression
     */
    public MutationExpression(IntoExpression intoExpression) {
        List<Expression> intoParams = intoExpression.getParameters();
        Expression intoParam0 = intoParams.get(0);
        Expression intoParam1 = intoParams.get(1);
        if (intoParam1 instanceof PlusExpression) {
            // Into-With usage
            List<Expression> plusParams = ((PlusExpression) intoParam1).getParameters();
            Expression plusParam0 = plusParams.get(0);
            if (plusParam0 instanceof VariableReference) {
                this.baseExpr = intoParam0;
                this.intoExpr = (VariableReference) plusParam0;
                this.withExpr = plusParams.get(1);
            } else {
                throw new RockstarRuntimeException("Cannot use " + plusParam0 + " as target for 'into'");
            }
        } else if (intoParam0 instanceof PlusExpression) {
            // With-Into usage
            List<Expression> plusParams = ((PlusExpression) intoParam0).getParameters();
            if (intoParam1 instanceof VariableReference) {
                this.baseExpr = plusParams.get(0);
                this.withExpr = plusParams.get(1);
                this.intoExpr = (VariableReference) intoParam1;
            } else {
                throw new RockstarRuntimeException("Cannot use " + intoParam1 + " as target for 'into'");
            }
        } else if (intoParam1 instanceof VariableReference) {
            // Into usage
            this.baseExpr = intoParam0;
            this.withExpr = null;
            this.intoExpr = (VariableReference) intoParam1;
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
        List params = new LinkedList();
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

}
