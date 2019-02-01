/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.ConstantExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.AssignmentStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class PoeticStringAssignmentChecker extends Checker {

    @Override
    public Statement check() {
        if (match(1, "says", 2)) {
            VariableReference varRef = ExpressionFactory.tryVariableReferenceFor(getResult()[1], line);
            if (varRef != null) {
                // grab original string from line
                String poeticLiteralString = line.getOrigLine().substring(line.getOrigLine().indexOf("says ") + 5);
                ConstantExpression value = new ConstantExpression(poeticLiteralString);
                return new AssignmentStatement(varRef, value);
            }
        }
        return null;
    }
    
}
