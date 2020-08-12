/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import rockstar.expression.Expression;
import rockstar.expression.ExpressionType;
import rockstar.expression.QualifierExpression;
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
        } else if (asExpr instanceof QualifierExpression) {
            Expression valueExpr = ((QualifierExpression) asExpr).getArrayBaseRef();
            Expression keyExpr = ((QualifierExpression) asExpr).getArrayIndexRef();
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
        ExpressionType type = arrayValue.getType();
        if (type == ExpressionType.NULL || type == ExpressionType.MYSTERIOUS) {
            // NULL and MYSTERIOUS are empty arrays
            return;
        }
        if (type != ExpressionType.ARRAY) {
            throw new RockstarRuntimeException("Attempt to iterate through " + type);
        }
        
        // in case of assoc array we are iterating through the elements
        List<Value> list = arrayValue.asListArray();
        iterateList(list, keyVar, ctx, valueVar);

        // in case of assoc array we are iterating through the keys
        Map<Value, Value> map = arrayValue.asAssocArray();
        if (!map.isEmpty()) {
            iterateAssocArray(valueVar, keyVar, map, ctx);
        }
    }

    private boolean iterateList(List<Value> list, VariableReference keyVar, BlockContext ctx, VariableReference valueVar) {
        boolean canContinue = true;
        Value currKey;
        Value currValue;
        if (!list.isEmpty() ) {
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
        }
        return canContinue;
    }

    private void iterateAssocArray(VariableReference valueVar, VariableReference keyVar, Map<Value, Value> map, BlockContext ctx) {
        Value currKey;
        Value currValue;
        boolean canContinue = true;
        if (valueVar != null && keyVar == null) {
            keyVar = valueVar;
            valueVar = null;
        }
        Value[] keys = new Value[map.size()];
        Arrays.sort(map.keySet().toArray(keys));        
        for(int i=0; canContinue && i < keys.length; i++) {
            // initialize local loop variables
            currKey = keys[i];
            ctx.setVariable(keyVar, currKey);
            if (valueVar != null) {
                currValue = map.get(currKey);
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
    }

    @Override
    protected String explain() {
        return "until " + arrayExpr.format() + " as " + asExpr.format();
    }

}
