/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.ArrayList;
import java.util.List;
import rockstar.parser.Line;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Utils;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class ExpressionError extends Expression {

    private final List<String> tokens;
    private String errorMsg;
    private Line line;

    public ExpressionError(List<String> tokens, Line line) {
        this.tokens = new ArrayList<>(tokens);
        this.line = line;
        setErrorIndex(0);

        StringBuilder sb = new StringBuilder("Parse error near ");

        tokens.forEach((token) -> {
            sb.append(token).append(" ");
        });

        errorMsg = sb.toString();
    }

    public ExpressionError(List<String> tokens, int errorIdx, String errorMsg) {
        this.tokens = new ArrayList<>(tokens);
        setErrorIndex(errorIdx);

        StringBuilder sb = new StringBuilder();
        int idx = 0;
        for (String token : tokens) {
            if (idx == errorIdx) {
                sb.append(">>>").append(token).append("<<");
            } else {
                sb.append(token);
            }
            sb.append(" ");
            idx++;
        }
        this.errorMsg = errorMsg + " at " + sb.toString();
    }

    final void setErrorIndex(int errorIdx) {
        tokens.set(errorIdx, ">>>" + tokens.get(errorIdx) + "<<<");
    }

    @Override
    public String toString() {
        return errorMsg;
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        ctx.beforeExpression(this);
        throw new RockstarRuntimeException(errorMsg);
    }

    @Override
    public String format() {
        return "!!!ExpressionError!!!";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ExpressionError) {
            ExpressionError o = (ExpressionError) obj;
            return Utils.isListEquals(tokens, o.tokens);
        }
        return false;
    }
    
    

}
