/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.ArrayList;
import java.util.List;
import rockstar.parser.Line;
import rockstar.parser.ParseException;

/**
 *
 * @author Gabor
 */
public class DummyExpression extends Expression {

    private final List<String> tokens;
    private String errorMsg;
    private Line line;

    public DummyExpression(List<String> tokens, Line line) {
        this.tokens = new ArrayList<>(tokens);
        this.line = line;
        
        StringBuilder sb = new StringBuilder();

        tokens.forEach((token) -> {
            sb.append(token).append("/");
        });

        throw new ParseException("Expression parsing" + sb.toString(), line);
    }

    public DummyExpression(List<String> tokens, int errorIdx, String errorMsg) {
        this.tokens = new ArrayList<>(tokens);
        this.errorMsg = errorMsg;
        setErrorIndex(errorIdx);

        StringBuilder sb = new StringBuilder();
        int idx = 0;
        for (String token : tokens) {
            if (idx == errorIdx) {
                sb.append(">>>").append(token).append("<<");
            } else {
                sb.append(token);
            }
            sb.append("/");
            idx++;
        }
//        throw new ParseException("Expression parsing: " + errorMsg + " " + sb.toString());
    }

    final void setErrorIndex(int errorIdx) {
        tokens.set(errorIdx, ">>>" + tokens.get(errorIdx) + "<<<");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DUMMYEXPR:/");

        tokens.forEach((token) -> {
            sb.append(token).append("/");
        });
        return sb.toString() + (errorMsg == null ? "" : ("\n    " + errorMsg));
    }
}
