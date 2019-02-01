/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.IncrementStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class BuildUpChecker extends Checker {
    
    @Override
    public Statement check() {
        if (match("Build", 1, "up", 2)) {
            VariableReference varRef = ExpressionFactory.tryVariableReferenceFor(getResult()[1], line);
            int count = 1;
            boolean isAndPossible = true;
            for (String s : getResult()[2]) {
                if ("up".equals(s)) {
                    count++;
                    isAndPossible = true;
                } else if (isAndPossible && s.equals("and")) {
                    isAndPossible = false;
                } else {
                    return null;
                }
            }
            if (varRef != null) {
                return new IncrementStatement(varRef, count);
            }
        }
        return null;
    }
    
}
