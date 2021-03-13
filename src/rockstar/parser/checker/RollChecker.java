/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.RollStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class RollChecker extends Checker {

    @Override
    public Statement check() {
        if (match("Roll", 1, "into", 2) || match("Pull", 1, "from", 2)) {
            VariableReference valueExpr = ExpressionFactory.tryVariableReferenceFor(getResult()[1], line, block);
            VariableReference varExpr = ExpressionFactory.tryVariableReferenceFor(getResult()[2], line, block);
            if (varExpr != null && valueExpr != null) {
                return new RollStatement(varExpr, valueExpr);
            }
        }
        return null;
    }

}
