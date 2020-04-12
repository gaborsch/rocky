/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class IntoExpression extends CompoundExpression {

    @Override
    public int getPrecedence() {
        return 550;
    }

    @Override
    public int getParameterCount() {
        return 2;
    }

    @Override
    public String getFormat() {
        return "(%s into %s)";
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        throw new RockstarRuntimeException("Parse problem, should not evaluate 'into' expression");
    }
    
}
