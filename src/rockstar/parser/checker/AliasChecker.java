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
import rockstar.statement.AliasStatement;
import rockstar.statement.ImportStatement;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class AliasChecker extends Checker {

    @Override
    public Statement check() {
        if (match("for", "me", 1, "means", 2)
            || match(1, "means", 2)) {

            List<String> alias = getResult()[1];
            List<String> keyword = getResult()[2];
            if (!alias.isEmpty() && !keyword.isEmpty()) {
                return new AliasStatement(alias, keyword);
            }
        }
        return null;
    }

}
