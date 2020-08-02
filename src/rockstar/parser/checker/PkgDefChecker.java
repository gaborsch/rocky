/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import rockstar.expression.ConstantExpression;
import rockstar.expression.Expression;
import rockstar.expression.ExpressionError;
import rockstar.expression.ListExpression;
import rockstar.expression.SimpleExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.runtime.PackagePath;
import rockstar.statement.PkgDefStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class PkgDefChecker extends Checker {

    @Override
    public Statement check() {
        if (match("Album", 1)) {
            Expression expr = ExpressionFactory.getExpressionFor(getResult()[1], line, block);

            Optional<PackagePath> pathOpt = PackagePath.getPackagetPathFromExpr(expr);
            if (pathOpt.isPresent()) {
                return new PkgDefStatement(pathOpt.get());
            }
            return null;
        }
        return null;
    }



}
