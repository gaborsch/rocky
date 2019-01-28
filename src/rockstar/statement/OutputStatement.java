/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.expression.Expression;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class OutputStatement extends Statement {
    
    private Expression expression;

    public OutputStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return super.toString() + 
                "\n    OUTPUT " + expression ; 
    }

    @Override
    public void execute(BlockContext ctx) {
        super.execute(ctx); //To change body of generated methods, choose Tools | Templates.
        
        Value v = expression.evaluate(ctx);
        ctx.getOutput().println(v.asString());
    }
    
    
    
    
    
}
