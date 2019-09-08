/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.List;
import rockstar.expression.VariableReference;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class InstantiationStatement extends Statement {

    private final VariableReference variable;
    private final String className;
    private final List<VariableReference> ctorParameterRefs = new ArrayList<>();

    public InstantiationStatement(VariableReference variable, String className) {
        this.variable = variable;
        this.className = className;
    }

    public void addParameterName(VariableReference paramRef) {
        ctorParameterRefs.add(paramRef);
    }

    @Override
    public void execute(BlockContext ctx) {
        ClassBlock block = ctx.retrieveClass(className);
        if (block != null) {
            Value instance = block.instantiate(ctx, null);
            ctx.setVariable(this.variable, instance);
        } else {
            throw new RockstarRuntimeException("Undefined class: " + className);
        }
    }

    @Override
    protected String explain() {
        String paramsList = ctorParameterRefs.toString();
        return variable.format() + " := new " + className + "(" + paramsList.substring(1, paramsList.length() - 1) + ")";
    }

}
