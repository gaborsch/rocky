/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.ArrayList;
import java.util.Arrays;
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

    private static final List<String> WANTS_TO_BE = Arrays.asList("wants", "to", "be");
    private static final List<String> WANT_TO_BE = Arrays.asList("want", "to", "be");
    private static final List<String> WANNA_BE = Arrays.asList("wanna", "be");
    private static final List<String> WILL_BE = Arrays.asList("will", "be");
    private static final List<String> WOULD_BE = Arrays.asList("would", "be");

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(1, WANTS_TO_BE, 2, "taking", 3),
        new ParamList(1, WANTS_TO_BE, 2),
        new ParamList(1, WANT_TO_BE, 2, "taking", 3),
        new ParamList(1, WANT_TO_BE, 2),
        new ParamList(1, WANNA_BE, 2, "taking", 3),
        new ParamList(1, WANNA_BE, 2),
        new ParamList(1, WILL_BE, 2, "taking", 3),
        new ParamList(1, WILL_BE, 2),
        new ParamList(1, WOULD_BE, 2, "taking", 3),
        new ParamList(1, WOULD_BE, 2)};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        // class  name looks like the same as a variable name
        VariableReference variableRef = ExpressionFactory.tryVariableReferenceFor(get1(), line, block);
        if (variableRef != null) {
            VariableReference classRef = ExpressionFactory.tryVariableReferenceFor(get2(), line, block);
            if (classRef != null) {
                String className = classRef.getName();
                InstantiationStatement stmt = new InstantiationStatement(variableRef, className);

                if (get3() != null) {
                    // has constructor parameters
                    Expression expr = ExpressionFactory.tryExpressionFor(get3(), line, block);
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
        return null;
    }
}
