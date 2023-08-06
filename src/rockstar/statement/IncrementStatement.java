/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.expression.ConstantExpression;
import rockstar.expression.PlusExpression;
import rockstar.expression.VariableReference;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockNumber;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class IncrementStatement extends Statement {

    final VariableReference variable;
    final int count;
    private PlusExpression plus;

    public IncrementStatement(VariableReference variable, int count) {
        this.variable = variable;
        this.count = count;
    }

    private PlusExpression getPlus() {
        if (plus == null) {
            plus = new PlusExpression();
            plus.addParameter(variable);
            plus.addParameter(new ConstantExpression(count));
        }
        return plus;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value v = ctx.getVariableValue(variable);

        // default to numeric 0 value
        if (v.isNull()) {
            v = Value.getValue(RockNumber.ZERO());
            // v is set to a numeric value
            ctx.setVariable(variable, v);
        }

        if (v.isNumeric()) {
            // increment by count
            Value value = getPlus().evaluate(ctx);
            ctx.setVariable(variable, value);
            return;
        } else if (v.isBoolean()) {
            // convert to boolean
            v = v.asBoolean();
            if (count % 2 == 1) {
                // negate boolean
                v = v.negate();
            }
            ctx.setVariable(variable, v);
            return;
        }
        throw new RockstarRuntimeException("Cannot increment " + v.getType());
    }

    @Override
    public String getASTNodeText() {
        return super.getASTNodeText() + (count != 1 ? (" by " + count) : "");
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return ASTValues.of(variable);
    }
    
    @Override
    public void accept(StatementVisitor visitor) {
    	visitor.visit(this);
    }

}
