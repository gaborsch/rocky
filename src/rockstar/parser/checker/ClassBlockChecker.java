/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import java.util.Arrays;
import java.util.List;
import rockstar.expression.ConstantExpression;
import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.runtime.Value;
import rockstar.statement.ClassBlock;
import rockstar.statement.Statement;

/**
 *
 * @author Gabor
 */
public class ClassBlockChecker extends Checker<VariableReference, Expression, Object> {

    private static final List<String> LOOK_LIKE = Arrays.asList("look", "like");
    private static final List<String> LOOKS_LIKE = Arrays.asList("looks", "like");

    private static final ParamList[] PARAM_LIST = new ParamList[]{
        new ParamList(variableAt(1), LOOK_LIKE, at(2, PlaceholderType.LITERAL_OR_VARIABLE)),
        new ParamList(variableAt(1), LOOKS_LIKE, at(2, PlaceholderType.LITERAL_OR_VARIABLE))};

    @Override
    public Statement check() {
        return check(PARAM_LIST, this::validate);
    }

    private Statement validate(ParamList params) {
        VariableReference nameRef = getE1();
        Expression parentNameRef = getE2();

        if (parentNameRef instanceof ConstantExpression) {
            Value v = ((ConstantExpression) parentNameRef).getValue();
            // checking for "nothing" and its aliases
            if (v.isNull()) {
                return new ClassBlock(nameRef.getName(), null);
            }
        } else if (parentNameRef instanceof VariableReference) {
            // parent class name
            // TODO: proper classname check: self, parent, it, ...
            return new ClassBlock(nameRef.getName(), ((VariableReference) parentNameRef).getName());
        }
        return null;
    }

}
