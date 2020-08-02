/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.ArrayList;
import java.util.List;
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

    private static final List<String> WANTS_TO_BE = new ArrayList<String>();
    private static final List<String> WANT_TO_BE = new ArrayList<String>();
    private static final List<String> WANNA_BE = new ArrayList<String>();
    private static final List<String> WILL_BE = new ArrayList<String>();
    private static final List<String> WOULD_BE = new ArrayList<String>();
    
    static {
        WANTS_TO_BE.add("wants");
        WANTS_TO_BE.add("to");
        WANTS_TO_BE.add("be");

        WANT_TO_BE.add("want");
        WANT_TO_BE.add("to");
        WANT_TO_BE.add("be");

        WANNA_BE.add("wanna");
        WANNA_BE.add("be");

        WILL_BE.add("will");
        WILL_BE.add("be");

        WOULD_BE.add("would");
        WOULD_BE.add("be");
    }    
    
    @Override
    public Statement check() {
        if (match(0, WANTS_TO_BE, 1, "taking", 2)     || match(0, WANTS_TO_BE, 1)
            || match(0, WANT_TO_BE, 1, "taking", 2)   || match(0, WANT_TO_BE, 1)
            || match(0, WANNA_BE, 1, "taking", 2)        || match(0, WANNA_BE, 1)
            || match(0, WILL_BE, 1, "taking", 2)         || match(0, WILL_BE, 1)
            || match(0, WOULD_BE, 1, "taking", 2)        || match(0, WOULD_BE, 1)) {

            // class  name looks like the same as a variable name
            VariableReference variableRef = ExpressionFactory.tryVariableReferenceFor(getResult()[0], line, block);
            if (variableRef != null) {
                VariableReference classRef = ExpressionFactory.tryVariableReferenceFor(getResult()[1], line, block);
                if (classRef != null) {
                    String className = classRef.getName();
                    InstantiationStatement stmt = new InstantiationStatement(variableRef, className);

                    if (getResult()[2] != null) {
                        // has constructor parameters
                        Expression expr = ExpressionFactory.tryExpressionFor(getResult()[2], line, block);
                        // treats null expression
                        ListExpression listExpr = ListExpression.asListExpression(expr);
                        if (listExpr != null) {
                            for (Expression expression : listExpr.getParameters()) {
                                    stmt.addParameter(expression);
                            }
                        }
                    }
                    return stmt;
                }
            }
        }
        return null;
    }
}
