/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.BlockContext;
import rockstar.runtime.RockObject;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class SelfVariableReference extends VariableReference {
    
    protected SelfVariableReference(String name) {
        super(name);
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        RockObject obj = ctx.getThisObjectCtx()
                .orElseThrow(() -> new RockstarRuntimeException("Self reference in a non-class context"));
        return ctx.afterExpression(this, Value.getValue(obj));
    }

    @Override
    public String format() {
        return "<self>";
    }

}
