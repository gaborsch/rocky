/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.expression.ListExpression;
import rockstar.expression.VariableReference;
import rockstar.statement.RockStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class RockChecker extends Checker<VariableReference, Expression, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Rock", variableAt(1), "with", expressionAt(2)),
        new ParamList("Push", variableAt(1), "with", expressionAt(2)),
        new ParamList("Rock", expressionAt(2), "into", variableAt(1)),
        new ParamList("Push", expressionAt(2), "into", variableAt(1))};

    private static final ParamList[] PARAM_LIST2 = new ParamList[]{
        new ParamList("Rock", at(1, PlaceholderType.VARIABLE_OR_LIST)),
        new ParamList("Push", at(1, PlaceholderType.VARIABLE_OR_LIST))};

    @Override
    public Statement check() {
        Statement stmt = check(PARAM_LIST, this::validate);
        if (stmt == null) {
            stmt = check(PARAM_LIST2, this::validate2);
        }
        return stmt;
    }

    private Statement validate(ParamList params) {
        VariableReference varExpr = getE1();
        Expression valueExpr = getE2();
        return new RockStatement((VariableReference) varExpr, valueExpr);
    }

    private Statement validate2(ParamList params) {
        Expression varExpr = getE1();
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
        return null;
    }

}
