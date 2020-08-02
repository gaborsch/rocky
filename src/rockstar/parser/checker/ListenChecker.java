/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.List;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.InputStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class ListenChecker extends Checker {
    
    @Override
    public Statement check() {
        if (match("Listen", 1)) {
            List<String> rest = getResult()[1];
            if (rest.isEmpty()) {
                return new InputStatement();
            }
            if (rest.size() >= 2) {
                if (rest.get(0).equals("to")) {
                    VariableReference varRef = ExpressionFactory.tryVariableReferenceFor(rest.subList(1, rest.size()), line, block);
                    if (varRef != null) {
                        return new InputStatement(varRef);
                    }
                }
            }
        }
        return null;
    }
    
}
