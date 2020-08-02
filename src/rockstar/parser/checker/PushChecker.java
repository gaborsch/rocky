/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.PushStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class PushChecker extends Checker {

    @Override
    public Statement check() {
        if (match("Push", 1, "into", 2) || match("Rock", 1, "into", 2) ) {
            Expression valueExpr = ExpressionFactory.getExpressionFor(getResult()[1], line, block);
            Expression varExpr = ExpressionFactory.getExpressionFor(getResult()[2], line, block);
            if (varExpr != null && valueExpr != null) {
                if (varExpr instanceof VariableReference) {
                    return new PushStatement((VariableReference)varExpr, valueExpr);
                }
            }
        }
        return null;
    }

}
