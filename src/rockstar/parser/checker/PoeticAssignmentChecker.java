/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.List;
import rockstar.expression.ConstantExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.AssignmentStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class PoeticAssignmentChecker extends Checker {
    
    @Override
    public Statement check() {
        if (match(1, "is", 2) || match(1, "was", 2) || match(1, "are", 2) || match(1, "were", 2)) {
            VariableReference varRef = ExpressionFactory.tryVariableReferenceFor(getResult()[1], line);
            if (varRef != null) {
                List<String> list2 = getResult()[2];
                // poetic expressions
                ConstantExpression literalValue = ExpressionFactory.tryLiteralFor(list2.subList(0, 1), line);
                if (literalValue != null) {
                    if (list2.size() == 1) {
                        return new AssignmentStatement(varRef, literalValue);
                    }
                } else {
                    // poetic literals
                    ConstantExpression constValue = ExpressionFactory.getPoeticLiteralFor(list2, line);
                    if (constValue != null) {
                        return new AssignmentStatement(varRef, constValue);
                    }
                }
            }
        }
        return null;
    }
    
}
