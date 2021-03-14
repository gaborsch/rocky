/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.Arrays;
import java.util.List;
import rockstar.expression.Expression;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.ReturnStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class GiveBackChecker extends Checker {
    
    private static final List<String> GIVE_BACK = Arrays.asList("give", "back");
    private static final List<String> SEND_BACK = Arrays.asList("send", "back");
        
    @Override
    public Statement check() {
        if (match(GIVE_BACK, 1) 
                || match("give", 1, "back")
                || match(SEND_BACK, 1) 
                || match("send", 1, "back")) {
            Expression expression = ExpressionFactory.getExpressionFor(getResult()[1], line, block);
            if (expression != null) {
                return new ReturnStatement(expression);
            }
        }
        return null;
    }
    
}
