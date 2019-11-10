/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.runtime.BlockContext;
import rockstar.runtime.QualifiedClassName;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class InstantiationStatement extends Statement {

    private final VariableReference variable;
    private final String className;
    private final List<Expression> ctorParameterExprs = new ArrayList<>();

    public InstantiationStatement(VariableReference variable, String className) {
        this.variable = variable;
        this.className = className;
    }

    public void addParameter(Expression expr) {
        ctorParameterExprs.add(expr);
    }

    @Override
    public void execute(BlockContext ctx) {
        // get the class
        QualifiedClassName qcn = ctx.findClass(className);
        ClassBlock classBlock = ctx.getRootCtx().retrieveClass(qcn);
        if (classBlock != null) {
            // evaluate constructor expressions
            List<Value> paramValues = ctorParameterExprs.stream()
                    .map(expr -> expr.evaluate(ctx))
                    .collect(Collectors.toList());
            // instantiate the class
            Value instance = classBlock.instantiate(ctx, paramValues);
            // assign the instance to the variable
            ctx.setVariable(this.variable, instance);
        } else {
            throw new RockstarRuntimeException("Undefined class: " + className);
        }
    }

    @Override
    protected String explain() {
        String paramsList = ctorParameterExprs.toString();
        return variable.format() + " := new " + className + "(" + paramsList.substring(1, paramsList.length() - 1) + ")";
    }

}
