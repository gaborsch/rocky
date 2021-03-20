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
import rockstar.statement.RockStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class RockPoeticChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Rock", 1, "like", 2),
        new ParamList("Push", 1, "like", 2)};

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
                    return new RockStatement(varRef, literalValue);
                }
            } else {
                // poetic literals
                String matched = (String) params.getParams()[2]; // the matched string: "like"
                String orig = line.getOrigLine();
                int p = orig.indexOf(" " + matched + " ") + matched.length() + 2;
                String origEnd = orig.substring(p);
                ConstantExpression constValue = ExpressionFactory.getPoeticLiteralFor(list2, line, origEnd, block);
                if (constValue != null) {
                    return new RockStatement(varRef, constValue);
                }
            }
        }
        return null;
    }

}
