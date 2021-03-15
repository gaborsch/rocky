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
public class RollEmptyChecker extends Checker {

    @Override
    public Statement check() {
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
