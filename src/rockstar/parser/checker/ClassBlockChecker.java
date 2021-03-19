/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private static final List<String> LOOK_LIKE = Arrays.asList("look", "like");
    private static final List<String> LOOKS_LIKE = Arrays.asList("looks", "like");

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(1, LOOK_LIKE, 2),
        new ParamList(1, LOOKS_LIKE, 2)};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference nameRef = ExpressionFactory.tryVariableReferenceFor(get1(), line, block);
        if (nameRef != null) {
            String name = nameRef.getName();
            // checking for "nothing" and aliases
            ConstantExpression literal = ExpressionFactory.tryLiteralFor(get2(), line, block);
            if (literal != null) {
                Value v = literal.getValue();
                if (v.isNull()) {
                    return new ClassBlock(name, null);
                }
            } else {
                // checking for class name
                VariableReference parentRef = ExpressionFactory.tryVariableReferenceFor(get2(), line, block);
                if (parentRef != null) {
                    // TODO proper classname check: self, parent, it, ...
                    return new ClassBlock(name, parentRef.getName());
                }
            }
        }
        return null;
    }

}
