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
import rockstar.parser.ExpressionFactory;
import rockstar.runtime.PackagePath;
import rockstar.statement.ImportStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class ImportChecker extends Checker {

    @Override
    public Statement check() {
        if (match("from", 1, "play", 2)
                || match("off", 1, "play", 2)
                || match("play", 2)) {

            // determine package path, if present
            PackagePath path = PackagePath.DEFAULT;
            if (getResult()[1] != null) {
                Expression pkgExpr = ExpressionFactory.getExpressionFor(getResult()[1], line);
                Optional<PackagePath> pathOpt = PackagePath.getPackagetPathFromExpr(pkgExpr);
                if (!pathOpt.isPresent()) {
                    return null;
                }
                path = pathOpt.get();
                
            }
            // Process class list expression
            Expression classesExpr = ExpressionFactory.getExpressionFor(getResult()[2], line);
            List<String> clsList = new LinkedList<>();
            if(classesExpr instanceof ListExpression) {
                // if it is a proper list expression
                for (Expression cls : ((ListExpression) classesExpr).getParameters()) {
                    if (cls instanceof VariableReference) {
                        // variable name in a list
                        clsList.add(((VariableReference) cls).getName());
                    } else {
                        // if it's something else, it's not allowed
                        return null;
                    }
                }
            } else if(classesExpr instanceof VariableReference) {
                // if it is a single variable epression
                clsList.add(((VariableReference) classesExpr).getName());
            } else {
                // others are not allowed
                return null;
            }
            
            if (! clsList.isEmpty()) {
                return new ImportStatement(path, clsList);
            }

        }
        return null;
    }

}
