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
        if (match("Roll", 2, "into", 1) 
                || match("Pop", 2, "into", 1) 
                || match("Pull", 1, "from", 2)) {
            VariableReference targetRefExpr = ExpressionFactory.tryVariableReferenceFor(getResult()[1], line, block);
            VariableReference arrayExpr = ExpressionFactory.tryVariableReferenceFor(getResult()[2], line, block);
            if (arrayExpr != null && targetRefExpr != null) {
                return new RollStatement(arrayExpr, targetRefExpr);
            }
        }
        if (match("Roll", 1) 
                || match("Pop", 1) 
                || match("Pull","from", 1)) {
            VariableReference arrayExpr = ExpressionFactory.tryVariableReferenceFor(getResult()[1], line, block);
            if (arrayExpr != null) {
                return new RollStatement(arrayExpr);
            }
        }
        return null;
    }

}
