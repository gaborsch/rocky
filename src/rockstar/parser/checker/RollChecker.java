/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.VariableReference;
import rockstar.statement.RollStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class RollChecker extends Checker<VariableReference, VariableReference, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Roll", variableAt(2), "into", variableAt(1)),
        new ParamList("Pop", variableAt(2), "into", variableAt(1)),
        new ParamList("Pull", variableAt(1), "from", variableAt(2))};

    private static final ParamList[] PARAM_LIST2 = new ParamList[]{
        new ParamList("Roll", variableAt(2)),
        new ParamList("Pop", variableAt(2)),
        new ParamList("Pull", "from", variableAt(2))};

    @Override
    public Statement check() {
        Statement stmt = check(PARAM_LIST, this::validate);
        if (stmt == null) {
            stmt = check(PARAM_LIST2, this::validate2);
        }
        return stmt;
    }

    private Statement validate(ParamList params) {
        VariableReference targetRefExpr = getE1();
        VariableReference arrayExpr = getE2();
        return new RollStatement(arrayExpr, targetRefExpr);
    }

    private Statement validate2(ParamList params) {
        VariableReference arrayExpr = getE2();
        return new RollStatement(arrayExpr);
    }

}
