/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.List;
import rockstar.expression.ConstantExpression;
import rockstar.expression.Ref;
import rockstar.expression.SimpleExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.runtime.Utils;
import rockstar.runtime.Value;
import rockstar.statement.AssignmentStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class PoeticArrayAssignmentChecker extends Checker {

    @Override
    public Statement check() {
        if (match(1, "is", "containing", 2)
                || match(1, "was", "containing", 2)
                || match(1, "are", "containing", 2)
                || match(1, "were", "containing", 2)
                || match(1, "contain", 2)
                || match(1, "contains", 2)) {
            VariableReference varRef = ExpressionFactory.tryVariableReferenceFor(getResult()[1], line);
            if (varRef != null) {
                Value v = Value.NULL;
                List<String> valueList = getResult()[2];
                int startIdx = 0;
                while (startIdx < valueList.size()) {
                    int endIdx = Utils.findInList(valueList, "and", startIdx);
                    List<String> exprSubList = valueList.subList(startIdx, endIdx);
                    SimpleExpression expr = ExpressionFactory.tryArrayExpressionFor(exprSubList, line);
                    if (expr != null) {
                        if (expr instanceof VariableReference) {
                            VariableReference vref = (VariableReference) expr;
                            Ref ref = vref.getRef();
                            if (ref == null) {
                                // simple variable as array element
                            }
                        } else if (expr instanceof ConstantExpression) {
                            // append value to list
                            v = v.plus(((ConstantExpression) expr).getValue());
                        }
                    } else {
                        // could not parse expression
                       return null;
                    }
                    startIdx = endIdx+1; // skip "and"
                }
                return new AssignmentStatement(varRef, new ConstantExpression(v));
            }
        }
        return null;
    }

}
