/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import rockstar.expression.Expression;
import rockstar.expression.ListExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.Token;
import rockstar.runtime.PackagePath;
import rockstar.statement.ImportStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class ImportChecker extends Checker<Expression, Expression, Object> {

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("from", expressionAt(1), "play", expressionAt(2)),
        new ParamList("off", expressionAt(1), "play", expressionAt(2)),
        new ParamList("play", expressionAt(2))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        Expression pkgExpr = getE1();
        Expression classesExpr = getE2();

        // determine package path, if present
        PackagePath path = null;
        if (pkgExpr != null) {
            Optional<PackagePath> pathOpt = PackagePath.getPackagetPathFromExpr(pkgExpr);
            if (!pathOpt.isPresent()) {
                return null;
            }
            path = pathOpt.get();

        }
        // Process class list expression
        List<List<Token>> clsList = new LinkedList<>();
        if (classesExpr instanceof ListExpression) {
            // if it is a proper list expression
            for (Expression cls : ((ListExpression) classesExpr).getParameters()) {
                if (cls instanceof VariableReference) {
                    // variable name in a list
                    clsList.add(((VariableReference) cls).getTokens());
                } else {
                    // if it's something else, it's not allowed
                    return null;
                }
            }
        } else if (classesExpr instanceof VariableReference) {
            // if it is a single variable epression
            clsList.add(((VariableReference) classesExpr).getTokens());
        } else {
            // others are not allowed
            return null;
        }

        if (!clsList.isEmpty()) {
            return new ImportStatement(path, clsList);
        }
        return null;
    }

}
