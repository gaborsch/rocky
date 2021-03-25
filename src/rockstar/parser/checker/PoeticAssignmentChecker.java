/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.ConstantExpression;
import rockstar.expression.VariableReference;
import rockstar.statement.AssignmentStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class PoeticAssignmentChecker extends Checker<VariableReference, ConstantExpression, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(variableAt(1), "is", literalAt(2)),
        new ParamList(variableAt(1), "was", literalAt(2)),
        new ParamList(variableAt(1), "are", literalAt(2)),
        new ParamList(variableAt(1), "were", literalAt(2)),
        new ParamList(variableAt(1), "is", poeticLiteralAt(2)),
        new ParamList(variableAt(1), "was", poeticLiteralAt(2)),
        new ParamList(variableAt(1), "are", poeticLiteralAt(2)),
        new ParamList(variableAt(1), "were", poeticLiteralAt(2))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference varRef = getE1();
        // poetic expressions
        ConstantExpression literalValue = getE2();
        return new AssignmentStatement(varRef, literalValue);

    }
}
