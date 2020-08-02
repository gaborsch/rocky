/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.ArrayList;
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
    
    private static final List<String> GIVE_BACK = new ArrayList<String>();
    
    static {
        GIVE_BACK.add("give");
        GIVE_BACK.add("back");
    }

    @Override
    public Statement check() {
        if (match(GIVE_BACK, 1)) {
            Expression expression = ExpressionFactory.getExpressionFor(getResult()[1], line);
            if (expression != null) {
                return new ReturnStatement(expression);
            }
        }
        return null;
    }
    
}
