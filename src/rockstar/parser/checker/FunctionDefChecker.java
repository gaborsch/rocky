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
        if (match(0, "takes", 1, "and", 2, "and", 3, "and", 4, "and", 5, "and", 6, "and", 7, "and", 8, "and", 9)) {
            paramCount = 9;
        } else if (match(0, "takes", 1, "and", 2, "and", 3, "and", 4, "and", 5, "and", 6, "and", 7, "and", 8)) {
            paramCount = 8;
        } else if (match(0, "takes", 1, "and", 2, "and", 3, "and", 4, "and", 5, "and", 6, "and", 7)) {
            paramCount = 7;
        } else if (match(0, "takes", 1, "and", 2, "and", 3, "and", 4, "and", 5, "and", 6)) {
            paramCount = 6;
        } else if (match(0, "takes", 1, "and", 2, "and", 3, "and", 4, "and", 5)) {
            paramCount = 5;
        } else if (match(0, "takes", 1, "and", 2, "and", 3, "and", 4)) {
            paramCount = 4;
        } else if (match(0, "takes", 1, "and", 2, "and", 3)) {
            paramCount = 3;
        } else if (match(0, "takes", 1, "and", 2)) {
            paramCount = 2;
        } else if (match(0, "takes", 1)) {
            paramCount = 1;
        } else if (match(0, "takes", "nothing")) {
            paramCount = 0;
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
