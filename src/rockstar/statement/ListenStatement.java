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
public class ListenStatement extends Statement {

    final VariableReference variable;

    public ListenStatement(VariableReference variable) {
        this.variable = variable;
    }

    public ListenStatement() {
        this.variable = null;
    }

    @Override
    public void execute(BlockContext ctx) {
        String inputLine;
        try {
            inputLine = ctx.getEnv().getInput().readLine();
        } catch (IOException ex) {
            inputLine = "";
        }
        if (inputLine == null) {
            inputLine = "";
        }
        if (variable != null) {
            ctx.setVariable(variable, Value.parse(inputLine));
        }
    }

    
    @Override
    public void accept(StatementVisitor visitor) {
    	visitor.visit(this);
    }
}
