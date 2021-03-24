/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.List;
import rockstar.expression.ConstantExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.parser.ParseException;
import rockstar.statement.AssignmentStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class PoeticAssignmentChecker extends Checker<VariableReference, List<String>, ConstantExpression> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(variableAt(1), "is", literalAt(3)),
        new ParamList(variableAt(1), "was", literalAt(3)),
        new ParamList(variableAt(1), "are", literalAt(3)),
        new ParamList(variableAt(1), "were", literalAt(3))};

    private static final ParamList[] PARAM_LIST_POETIC = new ParamList[]{
        new ParamList(variableAt(1), "is", poeticLiteralAt(2)),
        new ParamList(variableAt(1), "was", poeticLiteralAt(2)),
        new ParamList(variableAt(1), "are", poeticLiteralAt(2)),
        new ParamList(variableAt(1), "were", poeticLiteralAt(2))};

    @Override
    public Statement check() {
        Statement stmt = check(PARAM_LIST, this::validateLiteral);
        if (stmt == null) {
            stmt = check(PARAM_LIST_POETIC, this::validatePoetic);
        }
        return stmt;
    }

    private Statement validateLiteral(ParamList params) {
        VariableReference varRef = getE1();
        // poetic expressions
        ConstantExpression literalValue = getE3();
        return new AssignmentStatement(varRef, literalValue);

    }

    private Statement validatePoetic(ParamList params) {
        VariableReference varRef = getE1();
        List<String> list2 = getE2();
        // poetic literals
        List<String> matchedList = (List<String>) params.getParams()[1]; // the matched string: "is", "was", ...
        String matched = matchedList.get(0);
        String orig = line.getOrigLine();
        int p1 = orig.indexOf(" " + matched + " ");
        int p2 = orig.indexOf("'s "); // maybe "'s" was expanded to " is "
        // find out which one was the first, "is" or "'s"
        int p = (p2 < 0) ? p1 : ((p1 < 0) ? p2 : ((p1 < p2) ? p1 : p2));
        if (p >= 0) {
            if (p == p1) {
                p = p + matched.length() + 1;
            } else if (p == p2) {
                p = p + 3; // "'s ".length()
            }
        } else {
            // was expecting either the matching word, or "'s", neither found
            throw new ParseException("Unparsed poetic number assignment", line);
        }

        String origEnd = orig.substring(p);
        ConstantExpression constValue = ExpressionFactory.getPoeticLiteralFor(list2, line, origEnd, block);
        if (constValue != null) {
            return new AssignmentStatement(varRef, constValue);
        }
        return null;
    }
}
