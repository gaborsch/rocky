/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.FunctionBlock;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class FunctionDefChecker extends Checker {

    @Override
    public Statement check() {
        int paramCount = -1;
        if (match(0, "takes", 1, "and", 2, "and", 3, "and", 4, "and", 5, "and", 6, "and", 7, "and", 8, "and", 9)
                || match(0, "takes", 1, "and", 2, "and", 3, "and", 4, "and", 5, "and", 6, "and", 7, "and", 8)
                || match(0, "takes", 1, "and", 2, "and", 3, "and", 4, "and", 5, "and", 6, "and", 7)
                || match(0, "takes", 1, "and", 2, "and", 3, "and", 4, "and", 5, "and", 6)
                || match(0, "takes", 1, "and", 2, "and", 3, "and", 4, "and", 5)
                || match(0, "takes", 1, "and", 2, "and", 3, "and", 4)
                || match(0, "takes", 1, "and", 2, "and", 3)
                || match(0, "takes", 1, "and", 2)
                || match(0, "takes", 1)
                || match(0, "takes", "nothing")) {
            paramCount = 10 - getMatchCounter();
        }

        if (paramCount >= 0) {
            // function name is the same as a variable name
            VariableReference nameRef = ExpressionFactory.tryVariableReferenceFor(getResult()[0], line);
            if (nameRef != null) {
                FunctionBlock fb = new FunctionBlock(nameRef.getFunctionName());
                VariableReference paramRef;
                for (int i = 1; i <= paramCount; i++) {
                    paramRef = ExpressionFactory.tryVariableReferenceFor(getResult()[i], line);
                    if (paramRef != null) {
                        fb.addParameterName(paramRef.getFunctionName());
                    } else {
                        return null;
                    }
                }
                return fb;
            }
        }
        return null;
    }

}
