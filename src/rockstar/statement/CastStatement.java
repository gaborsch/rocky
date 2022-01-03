/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.List;

import rockstar.expression.Expression;
import rockstar.expression.MutationExpression;
import rockstar.expression.VariableReference;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.NativeObject;
import rockstar.runtime.RockNumber;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class CastStatement extends Statement {

    private final MutationExpression expr;

    public CastStatement(MutationExpression mutationExpression) {
        this.expr = mutationExpression;
    }

    @Override
    public void execute(BlockContext ctx) {
        // Value for the base expression
        Value v = expr.evaluate(ctx);

        // target variable reference
        VariableReference targetRef = expr.getTargetReference();

        // parameter
        Expression paramExpr = expr.getParameterExpr();
        
        // Non-primitive Native object conversion (Array, List, Map, BigDecimal, BigInteger)
        if (v.isNative()) {
        	NativeObject nativeObj = v.getNative();
        	Value newValue = nativeObj.unwrap();
        	if (newValue != null) {
                ctx.setVariable(targetRef, newValue);
                return;
            } else {
            	 throw new RockstarRuntimeException("Cannot cast native " + v.getNative().getNativeClass().getCanonicalName());
            }
        }        
        
        // radix for numeric conversions
        RockNumber radixNumber = null;
        if (paramExpr != null) {
            Value paramValue = paramExpr.evaluate(ctx);
            if (!paramValue.isNumeric()) {
                throw new RockstarRuntimeException("Invalid radix value for conversion: " + paramValue);
            }
            radixNumber = paramValue.getNumeric();
        }

        // numeric to string conversion
        if (v.isNumeric()) {
            // create a string with the given char code
            RockNumber num = v.getNumeric();
            int code = num.asInt();
            String s = new String(new char[]{(char) code});
            ctx.setVariable(targetRef, Value.getValue(s));
            return;
        } 
        // string to numeric conversion
        else if (v.isString()) {
            RockNumber num;
            if (radixNumber != null) {
                // parse long or double with radix
                num = RockNumber.parseWithRadix(v.getString(), radixNumber);
            } else {
                // parse long or double w/o radix
                num = RockNumber.parse(v.getString());
            }
            ctx.setVariable(targetRef, Value.getValue(num));
            return;
        }

        throw new RockstarRuntimeException("Cannot cast " + v.getType());
    }

    @Override
    public List<ASTAware> getASTChildren() {
        return ASTValues.of(expr);
    }
}
