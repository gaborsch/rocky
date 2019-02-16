/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import rockstar.expression.Expression;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public interface  BlockContextListener {

    public void beforeStatement(BlockContext ctx, Statement stmt);

    public void beforeExpression(BlockContext ctx, Expression exp);

    public void afterExpression(BlockContext ctx, Expression exp, Value v);
    
}
