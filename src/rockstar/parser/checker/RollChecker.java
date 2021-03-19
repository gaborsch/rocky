/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.RollStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class RollChecker extends Checker {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Roll", 2, "into", 1),
        new ParamList("Pop", 2, "into", 1),
        new ParamList("Pull", 1, "from", 2)};

    private static final ParamList[] PARAM_LIST2 = new ParamList[]{
        new ParamList("Roll", 1),
        new ParamList("Pop", 1),
        new ParamList("Pull", "from", 1)};

    @Override
    public Statement check() {
        Statement stmt = check(PARAM_LIST, this::validate);
        if (stmt == null) {
            stmt = check(PARAM_LIST2, this::validate2);
        }
        return stmt;
    }

    private Statement validate(ParamList params) {
        VariableReference targetRefExpr = ExpressionFactory.tryVariableReferenceFor(get1(), line, block);
        VariableReference arrayExpr = ExpressionFactory.tryVariableReferenceFor(get2(), line, block);
        if (arrayExpr != null && targetRefExpr != null) {
            return new RollStatement(arrayExpr, targetRefExpr);
        }

        return null;
    }

    private Statement validate2(ParamList params) {
        VariableReference arrayExpr = ExpressionFactory.tryVariableReferenceFor(get1(), line, block);
        if (arrayExpr != null) {
            return new RollStatement(arrayExpr);
        }
        return null;
    }

}
