/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.expression.QualifierExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.AssignmentStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class AssignmentChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Let", 2, "be", 1),
        new ParamList("Put", 1, "into", 2),
        new ParamList("Put", 1, "in", 2),
        new ParamList(2, "thinks", 1)};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        Expression varExpr = ExpressionFactory.getExpressionFor(get2(), line, block);
        Expression valueExpr = ExpressionFactory.getExpressionFor(get1(), line, varExpr, block);
        if (varExpr != null && valueExpr != null) {
            if (varExpr instanceof VariableReference || varExpr instanceof QualifierExpression) {
                return new AssignmentStatement(varExpr, valueExpr);
            }
        }
        return null;
    }

}
