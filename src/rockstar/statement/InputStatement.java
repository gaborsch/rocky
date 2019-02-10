/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.io.IOException;
import rockstar.expression.VariableReference;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class InputStatement extends Statement {

    private final VariableReference variable;

    public InputStatement(VariableReference variable) {
        this.variable = variable;
    }

    public InputStatement() {
        this.variable = null;
    }

    @Override
    public String toString() {
        return super.toString()
                + "\n    INPUT " + (variable == null ? "" : variable.toString());
    }

    @Override
    public void execute(BlockContext ctx) {
        String inputLine;
        try {
            inputLine = ctx.getInput().readLine();
        } catch (IOException ex) {
            inputLine = "";
        }
        if (variable != null) {
            ctx.setVariable(variable.getName(), Value.getValue(inputLine));
        }
    }

    @Override
    protected String list() {
        return "input " + (variable == null ? "<line>" : variable.getName());
    }
}
