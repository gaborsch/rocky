/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.AssignmentStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class AssignmentChecker extends Checker {
    
    @Override
    public Statement check() {
        if (match("Put", 1, "into", 2) || match("Let", 2, "be", 1) || match(2, "thinks", 1)) {
            VariableReference varRef = ExpressionFactory.tryVariableReferenceFor(getResult()[2], line);
            Expression expr = ExpressionFactory.getExpressionFor(getResult()[1], line);
            if (varRef != null && expr != null) {
                return new AssignmentStatement(varRef, expr);
            }
        }
        return null;
    }
    
}
