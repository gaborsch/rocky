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
public class PoeticAssignmentChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(1, "is", 2),
        new ParamList(1, "was", 2),
        new ParamList(1, "are", 2),
        new ParamList(1, "were", 2)};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference varRef = ExpressionFactory.tryVariableReferenceFor(get1(), line, block);
        if (varRef != null) {
            List<String> list2 = get2();
            // poetic expressions
            ConstantExpression literalValue = ExpressionFactory.tryLiteralFor(list2.subList(0, 1), line, block);
            if (literalValue != null) {
                if (list2.size() == 1) {
                    return new AssignmentStatement(varRef, literalValue);
                }
            } else {
                // poetic literals
                String matched = (String) params.params[1]; // the matched string: "is", "was", ...
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
            }
        }
        return null;
    }

}
