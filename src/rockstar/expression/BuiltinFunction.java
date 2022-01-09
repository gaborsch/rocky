/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class BuiltinFunction extends CompoundExpression {

    public enum Type {
        SORT(1, "sorted %s"),
        SIZEOF(1, "length of %s"),
        PEEK(1, "last of %s"),
        KEYS(1, "all keys of %s"),
        VALUES(1, "all values of %s");

        int paramCount;
        String format;

        private Type(int paramCount, String format) {
            this.paramCount = paramCount;
            this.format = format;
        }

    }

    public BuiltinFunction(Type type, Expression... params) {
        super(Precedence.BUILTIN_FUNCTION, params);
        this.type = type;
    }

    private final Type type;

    @Override
    public int getParameterCount() {
        return type.paramCount;
    }

    @Override
    public String getFormat() {
        return type.format;
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        Expression expr = this.getParameters().get(0);
        Value v = expr.evaluate(ctx);
        Value retValue = null;
        switch (type) {
            case SORT:
                retValue = sort(v);
                break;
            case SIZEOF:
                retValue = sizeof(v);
                break;
            case PEEK:
                retValue = peek(v);
                break;
            case KEYS:
                retValue = keys(v);
                break;
            case VALUES:
                retValue = values(v);
                break;
            default:
        }
        return ctx.afterExpression(this, retValue);
    }

    private Value sort(Value v) {
        if (v.isArray()) {
            List<Value> l = v.asListArray();
            Value[] vals = l.toArray(new Value[l.size()]);
            Arrays.sort(vals);
            return Value.getValue(Arrays.asList(vals));
        }
        throw new RockstarRuntimeException("Invalid type: sorted " + v.getType());
    }

    private Value sizeof(Value v) {
        if (v.isArray()) {
            List<Value> l = v.asListArray();
            Map<Value, Value> m = v.asAssocArray();
            return Value.getValue(l.size() + m.size());
        }
        throw new RockstarRuntimeException("Invalid type: length of " + v.getType());
    }

    private Value peek(Value v) {
        if (v.isArray()) {
            List<Value> l = v.asListArray();
            return l.isEmpty() ? Value.MYSTERIOUS : l.get(l.size()-1);
        }
        throw new RockstarRuntimeException("Invalid type: last of " + v.getType());
    }

    private Value keys(Value v) {
        if (v.isArray()) {
            Map<Value, Value> m = v.asAssocArray();
            return Value.getValue(m.keySet());
        }
        throw new RockstarRuntimeException("Invalid type: all keys of " + v.getType());
    }

    private Value values(Value v) {
        if (v.isArray()) {
            Map<Value, Value> m = v.asAssocArray();
            return Value.getValue(m.values());
        }
        throw new RockstarRuntimeException("Invalid type: all values of " + v.getType());
    }

}
