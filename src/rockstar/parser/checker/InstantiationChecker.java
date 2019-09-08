/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.expression.ListExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.InstantiationStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class InstantiationChecker extends Checker {

    @Override
    public Statement check() {
        if (match(0, "wants", "to", "be", 1, "taking", 2)     || match(0, "wants", "to", "be", 1)
            || match(0, "want", "to", "be", 1, "taking", 2)   || match(0, "want", "to", "be", 1)
            || match(0, "wanna", "be", 1, "taking", 2)        || match(0, "wanna", "be", 1)
            || match(0, "will", "be", 1, "taking", 2)         || match(0, "will", "be", 1)
            || match(0, "would", "be", 1, "taking", 2)        || match(0, "would", "be", 1)) {

            // class  name looks like the same as a variable name
            VariableReference variableRef = ExpressionFactory.tryVariableReferenceFor(getResult()[0], line);
            if (variableRef != null) {
                VariableReference classRef = ExpressionFactory.tryVariableReferenceFor(getResult()[1], line);
                if (classRef != null) {
                    String className = classRef.getFunctionName();
                    InstantiationStatement stmt = new InstantiationStatement(variableRef, className);

                    if (getResult()[2] != null) {
                        // has constructor parameters
                        Expression expr = ExpressionFactory.tryExpressionFor(getResult()[2], line);
                        // treats null expression
                        ListExpression listExpr = ListExpression.asListExpression(expr);
                        if (listExpr != null) {
                            for (Expression expression : listExpr.getParameters()) {
                                    stmt.addParameter(expression);
                            }
//                        }
                        }
                    }
                    return stmt;
                }
            }
        }
        return null;
    }
}
