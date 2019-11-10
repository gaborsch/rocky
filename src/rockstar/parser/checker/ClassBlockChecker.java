/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.ConstantExpression;
import rockstar.expression.VariableReference;
import rockstar.parser.ExpressionFactory;
import rockstar.runtime.Value;
import rockstar.statement.ClassBlock;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class ClassBlockChecker extends Checker {

    @Override
    public Statement check() {
        if (match(1, "look", "like", 2) || match(1, "looks", "like", 2)) {
            VariableReference nameRef = ExpressionFactory.tryVariableReferenceFor(getResult()[1], line);
            if (nameRef != null) {
                String name = nameRef.getName();
                // checking for "nothing" and aliases
                ConstantExpression literal = ExpressionFactory.tryLiteralFor(getResult()[2], line);
                if (literal != null) {
                    Value v = literal.getValue();
                    if (v.isNull()) {
                        return new ClassBlock(name, null);
                    }
                } else {
                    // checking for class name
                    VariableReference parentRef = ExpressionFactory.tryVariableReferenceFor(getResult()[2], line);
                    if (parentRef != null) {
                        // TODO proper classname check: self, parent, it, ...
                        return new ClassBlock(name, parentRef.getName());
                    }
                }
            }
        }
        return null;
    }

}
