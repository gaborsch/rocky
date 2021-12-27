/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.List;
import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.parser.Token;
import rockstar.runtime.Utils;
import rockstar.statement.ArrayAssignmentStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class PoeticArrayAssignmentChecker extends Checker<VariableReference, List<Token>, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(variableAt(1), "is", "containing", textAt(2)),
        new ParamList(variableAt(1), "was", "containing", textAt(2)),
        new ParamList(variableAt(1), "are", "containing", textAt(2)),
        new ParamList(variableAt(1), "were", "containing", textAt(2)),
        new ParamList("Within", variableAt(1), "is", textAt(2)),
        new ParamList("Within", variableAt(1), "was", textAt(2)),
        new ParamList("Within", variableAt(1), "are", textAt(2)),
        new ParamList("Within", variableAt(1), "were", textAt(2)),
        new ParamList(variableAt(1), "contain", textAt(2)),
        new ParamList(variableAt(1), "contains", textAt(2)),
        new ParamList(variableAt(1), "hold", textAt(2)),
        new ParamList(variableAt(1), "holds", textAt(2))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference varRef = getE1();
        ArrayAssignmentStatement stmt = new ArrayAssignmentStatement(varRef);
        List<Token> valueList = getE2();
        int startIdx = 0;
        while (startIdx < valueList.size()) {
            int endIdx = Utils.findInList(valueList, "and", startIdx);
            List<Token> exprSubList = valueList.subList(startIdx, endIdx);
            Expression expr = ExpressionFactory.tryExpressionFor(exprSubList, line, block);
            if (expr != null) {
                // variable reference
                stmt.addExpression(expr);
            } else {
                // could not parse expression
                return null;
            }
            startIdx = endIdx + 1; // skip "and"
        }
        return stmt;
    }

}
