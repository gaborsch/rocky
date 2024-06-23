/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.List;

import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class SliceExpression extends CompoundExpression {

    public enum Type {
        SLICE_FROM,
        SLICE_TO,
        SLICE_FROM_TO
    }

    private Type type;

    public SliceExpression(Type type) {
    	super(Precedence.BUILTIN_FUNCTION);
        this.type = type;
    }
    
    public Type getType() {
		return type;
	}

    @Override
    public String getFormat() {
        return "(%s" + (type != Type.SLICE_TO ? " since %s" : "") + (type != Type.SLICE_FROM ? " till %s" : "") + ")";
    }

    @Override
    public int getParameterCount() {
        return type == Type.SLICE_FROM_TO ? 3 : 2;
    }

    @Override
    public CompoundExpression setupFinished() {
        if (this.type == Type.SLICE_TO && this.getParameters().size() == 2) {
            if (this.getParameters().get(0) instanceof SliceExpression) {
                SliceExpression fromExpr = (SliceExpression) this.getParameters().remove(0);
                if (fromExpr.type == Type.SLICE_FROM) {
                    addParameterReverse(fromExpr.getParameters().get(1));
                    addParameterReverse(fromExpr.getParameters().get(0));
                    this.type = Type.SLICE_FROM_TO;
                } else {
                    addParameterReverse(fromExpr);
                }
            }
        }
        return this;
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        Expression baseExpr = this.getParameters().get(0);
        Expression exprFrom = (type == Type.SLICE_TO) ? null : this.getParameters().get(1);
        Expression exprTo = (type == Type.SLICE_FROM) ? null : this.getParameters().get(type == Type.SLICE_TO ? 1 : 2);
        Value baseVal = baseExpr.evaluate(ctx);
        Integer fromVal = null;
        Integer toVal = null;
        if (exprFrom != null) {
            fromVal = exprFrom.evaluate(ctx).getNumeric().asInt();
        }
        if (exprTo != null) {
            toVal = exprTo.evaluate(ctx).getNumeric().asInt();
        }
        Value retValue = Value.MYSTERIOUS;
        if (baseVal.isArray()) {
            List<Value> baseList = baseVal.asListArray();
            List<Value> newList = baseList.subList(fromVal == null ? 0 : fromVal, toVal == null ? baseList.size() : toVal);
            retValue = Value.getValue(newList);
        } else {
            throw new RockstarRuntimeException("Invalid argument for since/till operation: " + baseVal.getType());
        }

        return ctx.afterExpression(this, retValue);
    }
    
    @Override
    public void accept(ExpressionVisitor visitor) {
    	visitor.visit(this);
    }

}
