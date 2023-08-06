/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import rockstar.expression.Expression;
import rockstar.expression.QualifierExpression;
import rockstar.expression.VariableReference;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class AssignmentStatement extends Statement {

    final Expression variableExpression;
    final Expression valueExpression;

    public AssignmentStatement(Expression variableExpression, Expression valueExpression) {
        this.valueExpression = valueExpression;
        this.variableExpression = variableExpression;
        if (!(variableExpression instanceof VariableReference || variableExpression instanceof QualifierExpression)) {
            throw new RockstarRuntimeException("Cannot assign to " + variableExpression.format());
        }
    }

    @Override
    public void execute(BlockContext ctx) {
        Value value = valueExpression.evaluate(ctx);
        assign(variableExpression, value, ctx);
    }

    /**
     * Public method to handle array assignments
     *
     * @param variableExpr
     * @param value
     * @param ctx
     */
    public static void assign(Expression variableExpr, Value value, BlockContext ctx) {
        if (variableExpr instanceof VariableReference) {
            ctx.setVariable((VariableReference) variableExpr, value);
        } else if (variableExpr instanceof QualifierExpression) {
            QualifierExpression ref = (QualifierExpression) variableExpr;
            // the array reference
            VariableReference vref = (VariableReference) ref.getArrayBaseRef();
            // the index expression
            Expression indexExpr = ref.getArrayIndexRef();
            // evaluate the index
            Value indexValue = indexExpr.evaluate(ctx);
            // fetch the base variable
            Value baseValue = ctx.getVariableValue(vref);
            // assign the value to the specified index
            Value newBaseValue = (baseValue == null ? Value.NULL : baseValue).assign(indexValue, value);
            // save the new base variable object if changed
            if (newBaseValue != baseValue) {
                ctx.setVariable(vref, newBaseValue);
            }
        } else {
            throw new RockstarRuntimeException("Cannot assign to " + variableExpr.format());
        }
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return ASTValues.of(variableExpression, valueExpression);
    }
    
    @Override
    public void accept(StatementVisitor visitor) {
    	visitor.visit(this);
    }
}
