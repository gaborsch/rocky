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
import rockstar.statement.RockStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class RockChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Rock", 1, "with", 2),
        new ParamList("Push", 1, "with", 2),
        new ParamList("Rock", 2, "into", 1),
        new ParamList("Push", 2, "into", 1)};

    private static final ParamList[] PARAM_LIST2 = new ParamList[]{
        new ParamList("Rock", 1),
        new ParamList("Push", 1)};

    @Override
    public Statement check() {
        Statement stmt = check(PARAM_LIST, this::validate);
        if (stmt == null) {
            stmt = check(PARAM_LIST2, this::validate2);
        }
        return stmt;
    }

    private Statement validate(ParamList params) {
        Expression varExpr = ExpressionFactory.getExpressionFor(get1(), line, block);
        Expression valueExpr = ExpressionFactory.getExpressionFor(get2(), line, block);
        if (varExpr != null && valueExpr != null) {
            if (varExpr instanceof VariableReference) {
                return new RockStatement((VariableReference) varExpr, valueExpr);
            }
        }
        return null;
    }

    private Statement validate2(ParamList params) {
        Expression varExpr = ExpressionFactory.getExpressionFor(get1(), line, block);
        if (varExpr != null) {
            if (varExpr instanceof VariableReference) {
                // rock <variable>
                return new RockStatement((VariableReference) varExpr);
            } else if (varExpr instanceof ListExpression) {
                ListExpression listExpr = (ListExpression) varExpr;
                // the first item should be treated as a variable reference, the remaining will be the list
                varExpr = listExpr.getParameters().remove(0);
                if (varExpr instanceof VariableReference) {
                    return new RockStatement((VariableReference) varExpr, listExpr);
                }
            }
        }
        return null;
    }

}
