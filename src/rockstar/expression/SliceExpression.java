/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;
import rockstar.runtime.RockstarRuntimeException;
import java.util.List;

/**
 *
 * @author Gabor
 */
public class SliceExpression extends CompoundExpression {

    public enum Type {
        SLICE_FROM,
        SLICE_TO,
        SLICE_BOTH
    }

    private Type type;

    public SliceExpression(Type type) {
        this.type = type;
    }

    @Override
    public String getFormat() {
        return "(%s"+ (type != Type.SLICE_TO ? " from %s":"") + (type != Type.SLICE_FROM ? " till %s":"") + ")";
    }

    @Override
    public int getPrecedence() {
        return 80;
    }

    @Override
    public int getParameterCount() {
        return type == Type.SLICE_BOTH ? 3 : 2;
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
        if (baseVal.isListArray()) {
            List<Value> baseList = baseVal.asListArray();
            List<Value> newList = baseList.subList(fromVal == null ? 0 : fromVal, toVal == null ? baseList.size() : toVal + 1);
            retValue = Value.getValue(newList);
        } else {
            throw new RockstarRuntimeException("Invalid argument for from/till operation: " + baseVal.getType());
        }

        return ctx.afterExpression(this, retValue);
    }

}
