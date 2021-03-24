/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.ConstantExpression;
import rockstar.expression.Expression;
import rockstar.expression.ExpressionType;
import rockstar.expression.ListExpression;
import rockstar.expression.VariableReference;
import rockstar.statement.FunctionBlock;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class FunctionDefChecker extends Checker<VariableReference, Expression, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(variableAt(1), "takes", expressionAt(2)),
        new ParamList(variableAt(1), "wants", expressionAt(2))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        // function name looks the same as a variable name
        VariableReference nameRef = getE1();
        Expression paramList = getE2();

        FunctionBlock fb = new FunctionBlock(nameRef.getName());
        // treats null expression
        if (paramList instanceof ConstantExpression) {
            ConstantExpression constExpr = (ConstantExpression) paramList;
            if (!constExpr.getValue().getType().equals(ExpressionType.NULL)) {
                // only NULL values are allowed
                return null;
            }
            // for NULLs, the parameter list remains empty
        } else {
            ListExpression listExpr = ListExpression.asListExpression(paramList);
            if (listExpr == null) {
                return null;
            }
            for (Expression expression : listExpr.getParameters()) {
                if (!(expression instanceof VariableReference)) {
                    return null;
                }
                // it is a variable reference
                VariableReference paramRef = (VariableReference) expression;
                fb.addParameterName(paramRef);
            }
        }
        return fb;
    }

}
