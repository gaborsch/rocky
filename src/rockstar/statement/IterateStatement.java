/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import rockstar.expression.Expression;
import rockstar.expression.ReferenceExpression;
import rockstar.expression.VariableReference;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockNumber;
import rockstar.runtime.RockstarBreakException;
import rockstar.runtime.RockstarContinueException;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class IterateStatement extends Block {


    private final Expression arrayExpr;
    private final Expression asExpr;

    public IterateStatement(Expression arrayExpr, Expression asExpr) {
        this.arrayExpr = arrayExpr;
        this.asExpr = asExpr;
    }

    public Expression getArrayExpr() {
        return arrayExpr;
    }

    public Expression getAsExpr() {
        return asExpr;
    }

    @Override
    public void execute(BlockContext ctx) {
        Value arrayValue = arrayExpr.evaluate(ctx);

        VariableReference keyVar = null;
        VariableReference valueVar = null;
        if (asExpr instanceof VariableReference) {
            valueVar = (VariableReference) asExpr;
            ctx.setLocalVariable(valueVar, Value.MYSTERIOUS);
        } else if (asExpr instanceof ReferenceExpression) {
            Expression valueExpr = ((ReferenceExpression) asExpr).getBaseExpression();
            Expression keyExpr = ((ReferenceExpression) asExpr).getIndexExpression();
            if (valueExpr instanceof VariableReference) {
                valueVar = (VariableReference) valueExpr;
                ctx.setLocalVariable(valueVar, Value.MYSTERIOUS);
            }
            if (keyExpr instanceof VariableReference) {
                keyVar = (VariableReference) keyExpr;
                ctx.setLocalVariable(keyVar, Value.MYSTERIOUS);
            }
        } else {
            throw new RockstarRuntimeException("Invalid iteration variables");
        }
        boolean canContinue = true;
        Value currValue;
        Value currKey;
        switch (arrayValue.getType()) {
            case LIST_ARRAY:
                List<Value> list = arrayValue.asListArray();
                Iterator<Value> listIter = list.iterator();
                for(int i=0; canContinue && listIter.hasNext(); i++) {
                    // initialize local loop variables
                    if (keyVar != null) {
                        currKey = Value.getValue(RockNumber.getValue(i));
                        ctx.setVariable(keyVar, currKey);
                    }
                    currValue = listIter.next();
                    ctx.setVariable(valueVar, currValue);

                    try {
                        super.execute(ctx);
                    } catch (RockstarContinueException rce) {
                        // continue exits the block, but not the loop
                    } catch (RockstarBreakException rbe) {
                        // break exits the loop, too
                        canContinue = false;
                    }                    
                }
                break;
            case ASSOC_ARRAY:
                // in case of assoc array we are iterating through the keys
                if (valueVar != null && keyVar == null) {
                    keyVar = valueVar;
                    valueVar = null;
                }
                Map<Value, Value> map = arrayValue.asAssocArray();
                Iterator<Map.Entry<Value, Value>> mapIter = map.entrySet().iterator();
                for(int i=0; canContinue && mapIter.hasNext(); i++) {
                    Map.Entry<Value, Value> entry = mapIter.next();
                    // initialize local loop variables
                    currKey = entry.getKey();
                    ctx.setVariable(keyVar, currKey);
                    if (valueVar != null) {
                        currValue = entry.getValue();
                        ctx.setVariable(valueVar, currValue);
                    }

                    try {
                        super.execute(ctx);
                    } catch (RockstarContinueException rce) {
                        // continue exits the block, but not the loop
                    } catch (RockstarBreakException rbe) {
                        // break exits the loop, too
                        canContinue = false;
                    }                    
                }
                break;
            case NULL:
            case MYSTERIOUS:
                // NULL and MYSTERIOUS are empty arrays
                break;
            default:
                throw new RockstarRuntimeException("Attempt to iterate through " + arrayValue.getType());
        }
    }

    @Override
    protected String explain() {
        return "until " + arrayExpr.format() + " as " + asExpr.format();
    }

}
