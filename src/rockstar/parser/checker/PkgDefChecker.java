/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.Optional;
import rockstar.expression.Expression;
import rockstar.parser.Keyword;
import rockstar.runtime.PackagePath;
import rockstar.statement.PkgDefStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class PkgDefChecker extends Checker<Expression, Object, Object> {

	// Package definition keywords must be parsed in strict mode, too, to be able to trigger extended mode
    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList("Album", expressionAt(1))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        Expression expr = getE1();
        Optional<PackagePath> pathOpt = PackagePath.getPackagetPathFromExpr(expr);
        if (pathOpt.isPresent()) {
        	// PackageDef statement triggers extended mode
        	Keyword.setStrictMode(false);

            return new PkgDefStatement(pathOpt.get());
        }
        return null;
    }

}
