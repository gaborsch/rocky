/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class LastVariableReference extends VariableReference {

    protected LastVariableReference(String name) {
        super(name);
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        VariableReference effectiveVRef = ctx.getLastVariableRef();
        // evaluate the actual variable reference
        return evaluate(ctx, effectiveVRef);
    }
    
    
    @Override
    public String format() {
        return "<it>";
    }

    @Override
    public VariableReference getEffectiveVref(BlockContext ctx) {
        return ctx.getLastVariableRef();
    }

    
}
