/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.expression.VariableReference;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockNumber;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class CastStatement extends Statement {

    private final VariableReference variable;

    public CastStatement(VariableReference variable) {
        this.variable = variable;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value v = ctx.getVariableValue(variable);
        if (v.isNumeric()) {
            // create a string with the given char code
            RockNumber num = v.getNumeric();
            int code = num.asInt();
            String s = new String(new char[] {(char)code });
            ctx.setVariable(variable, Value.getValue(s));
            return;
        } 
        else if (v.isString()) {
            // round it according to the direction
            RockNumber num = v.getNumeric();
            ctx.setVariable(variable, Value.getValue(num));
            return;
        } 
        throw new RockstarRuntimeException("casted " + v.getType());
    }
    
    @Override
    protected String explain() {
        return variable.format() + " = cast " + variable.format();
    }
}
