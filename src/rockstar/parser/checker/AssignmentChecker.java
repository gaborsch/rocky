/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.expression.QualifierExpression;
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
        if (match("Let", 2, "be", 1) 
                || match("Put", 1, "into", 2)  
                || match("Put", 1, "in", 2)
                || match(2, "thinks", 1)) {
            Expression varExpr = ExpressionFactory.getExpressionFor(getResult()[2], line, block);
            Expression valueExpr = ExpressionFactory.getExpressionFor(getResult()[1], line, varExpr, block);
            if (varExpr != null && valueExpr != null) {
                if (varExpr instanceof VariableReference || varExpr instanceof QualifierExpression) {
                    return new AssignmentStatement(varExpr, valueExpr);
                } 
            }
        }
        return null;
    }

}
