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
public class RockPoeticChecker extends Checker<VariableReference, List<String>, Object> {
    
    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Rock", variableAt(1), "like", poeticLiteralAt(2)),
        new ParamList("Push", variableAt(1), "like", poeticLiteralAt(2))};
    
    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }
    
    private Statement validate(ParamList params) {
        VariableReference varRef = getE1();
        
        List<String> list2 = getE2();
        // poetic expressions
        ConstantExpression literalValue = ExpressionFactory.tryLiteralFor(list2.subList(0, 1), line, block);
        if (literalValue != null) {
            if (list2.size() == 1) {
                return new RockStatement(varRef, literalValue);
            }
        } else {
            // poetic literals
            List<String> matchedList = (List<String>) params.getParams()[2]; // the matched string: "like"
            String matched = matchedList.get(0);
            String orig = line.getOrigLine();
            int p = orig.indexOf(" " + matched + " ") + matched.length() + 2;
            String origEnd = orig.substring(p);
            ConstantExpression constValue = ExpressionFactory.getPoeticLiteralFor(list2, line, origEnd, block);
            if (constValue != null) {
                return new RockStatement(varRef, constValue);
            }
        }
        return null;
    }
    
}
