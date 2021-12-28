/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.Arrays;
import java.util.List;
import rockstar.expression.Expression;
import rockstar.expression.ListExpression;
import rockstar.expression.VariableReference;
import rockstar.statement.InstantiationStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class InstantiationChecker extends Checker<VariableReference, VariableReference, Expression> {

    private static final List<String> WANTS_TO_BE = Arrays.asList("wants", "to", "be");
    private static final List<String> WANT_TO_BE = Arrays.asList("want", "to", "be");
    private static final List<String> WANNA_BE = Arrays.asList("wanna", "be");
    private static final List<String> WILL_BE = Arrays.asList("will", "be");
    private static final List<String> WOULD_BE = Arrays.asList("would", "be");

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(variableAt(1), WANTS_TO_BE, variableAt(2), "taking", expressionAt(3)),
        new ParamList(variableAt(1), WANTS_TO_BE, variableAt(2)),
        new ParamList(variableAt(1), WANT_TO_BE, variableAt(2), "taking", expressionAt(3)),
        new ParamList(variableAt(1), WANT_TO_BE, variableAt(2)),
        new ParamList(variableAt(1), WANNA_BE, variableAt(2), "taking", expressionAt(3)),
        new ParamList(variableAt(1), WANNA_BE, variableAt(2)),
        new ParamList(variableAt(1), WILL_BE, variableAt(2), "taking", expressionAt(3)),
        new ParamList(variableAt(1), WILL_BE, variableAt(2)),
        new ParamList(variableAt(1), WOULD_BE, variableAt(2), "taking", expressionAt(3)),
        new ParamList(variableAt(1), WOULD_BE, variableAt(2))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        // class  name looks like the same as a variable name
        VariableReference variableRef = getE1();
        VariableReference classRef = getE2();
        Expression constructorParamList = getE3();

        InstantiationStatement stmt = new InstantiationStatement(variableRef, classRef);

        if (constructorParamList != null) {
            // has constructor parameters
            // treats null expression
            ListExpression listExpr = ListExpression.asListExpression(constructorParamList);
            if (listExpr != null) {
                listExpr.getParameters()
                        .forEach(e -> stmt.addParameter(e));
            }
        }
        return stmt;
    }
}
