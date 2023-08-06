/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import rockstar.parser.Line;
import rockstar.parser.Token;
import rockstar.runtime.ASTAware;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Utils;
import rockstar.runtime.Value;
import rockstar.statement.ASTValues;

/**
 *
 * @author Gabor
 */
public class ExpressionError extends Expression {

    private final List<Token> tokens;
    private String errorMsg;
    private Line line;

    public ExpressionError(Line line, List<Token> tokens, int errorIdx, String errorMsg) {
    	this.line = line;
        this.tokens = new ArrayList<>(tokens);
        setErrorIndex(errorIdx);

        StringBuilder sb = new StringBuilder();
        int idx = 0;
        for (Token token : tokens) {
            if (idx == errorIdx) {
                sb.append(">>>").append(token.getValue()).append("<<<");
            } else {
                sb.append(token);
            }
            sb.append(" ");
            idx++;
        }
        this.errorMsg = errorMsg + " at " + sb.toString();
    }

    final void setErrorIndex(int errorIdx) {    	
        if (errorIdx < tokens.size()) {
        	Token t = tokens.get(errorIdx);
        	t.setValue(">>>" + tokens.get(errorIdx) + "<<<");
            tokens.set(errorIdx, t);
        } else {
        	Token t = new Token(line.getLnum(), line.getLine().length(), 0, ">>><<<");
            tokens.add(t);
        }
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

    @Override
    public List<ASTAware> getASTChildren() {
        return ASTValues.of(tokens.stream().map(Token::getValue).collect(Collectors.joining(" ")));
    }
    
    @Override
    public void accept(ExpressionVisitor visitor) {
    	visitor.visit(this);
    }

}
