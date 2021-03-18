/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.Optional;
import rockstar.expression.Expression;
import rockstar.parser.ExpressionFactory;
import rockstar.runtime.PackagePath;
import rockstar.statement.PkgDefStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class PkgDefChecker extends Checker {

    private static final ParamList[] PARAM_LIST
            = new ParamList[]{
                new ParamList()};

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
