/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.OutputStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class SayChecker extends Checker {
    
    @Override
    public Statement check() {
        if (match("Say", 1) || match("Shout", 1) || match("Whisper", 1) || match("Scream", 1)) {
            Expression expr = ExpressionFactory.getExpressionFor(getResult()[1], line);
            if (expr != null) {
                return new OutputStatement(expr);
            }
        }
        return null;
    }
    
}
