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
import rockstar.parser.ExpressionFactory;
import rockstar.statement.FunctionBlock;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class FunctionDefChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(1, "takes", 2),
        new ParamList(1, "wants", 2)};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        // function name is the same as a variable name
        VariableReference nameRef = ExpressionFactory.tryVariableReferenceFor(get1(), line, block);
        if (nameRef != null) {
            FunctionBlock fb = new FunctionBlock(nameRef.getName());
            // parse the expression, for an expression list
            Expression expr = ExpressionFactory.tryExpressionFor(get2(), line, block);
            // treats null expression
            if (expr instanceof ConstantExpression) {
                ConstantExpression constExpr = (ConstantExpression) expr;
                if (!constExpr.getValue().getType().equals(ExpressionType.NULL)) {
                    // only NULL values are allowed
                    return null;
                }
                // for NULLs, the parameter list remains empty
            } else {
                ListExpression listExpr = ListExpression.asListExpression(expr);
                if (listExpr != null) {
                    for (Expression expression : listExpr.getParameters()) {
                        if (expression instanceof VariableReference) {
                            // it is a variable reference
                            VariableReference paramRef = (VariableReference) expression;
                            fb.addParameterName(paramRef);
                        } else {
                            return null;
                        }
                    }
                } else {
                    return null;
                }
            }
            return fb;
        }
        return null;
    }

}
