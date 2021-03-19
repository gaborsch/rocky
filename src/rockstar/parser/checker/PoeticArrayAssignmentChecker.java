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
import rockstar.runtime.Utils;
import rockstar.statement.ArrayAssignmentStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class PoeticArrayAssignmentChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(1, "is", "containing", 2),
        new ParamList(1, "was", "containing", 2),
        new ParamList(1, "are", "containing", 2),
        new ParamList(1, "were", "containing", 2),
        new ParamList("Within", 1, "is", 2),
        new ParamList("Within", 1, "was", 2),
        new ParamList("Within", 1, "are", 2),
        new ParamList("Within", 1, "were", 2),
        new ParamList(1, "contain", 2),
        new ParamList(1, "contains", 2),
        new ParamList(1, "hold", 2),
        new ParamList(1, "holds", 2)};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference varRef = ExpressionFactory.tryVariableReferenceFor(get1(), line, block);
        if (varRef != null) {
            ArrayAssignmentStatement stmt = new ArrayAssignmentStatement(varRef);
            List<String> valueList = get2();
            int startIdx = 0;
            while (startIdx < valueList.size()) {
                int endIdx = Utils.findInList(valueList, "and", startIdx);
                List<String> exprSubList = valueList.subList(startIdx, endIdx);
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
        return null;
    }

}
