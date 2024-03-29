/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.List;
import java.util.StringJoiner;

import rockstar.parser.Token;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public abstract class Expression implements VisitableExpression {
	
	private List<Token> tokens;
	
	public List<Token> getTokens() {
		return tokens;
	}
	
	public String geTokensAsString() {
		StringJoiner sj = new StringJoiner(" ");
		tokens.forEach(t -> sj.add(t.getValue()));
		return sj.toString();
	}
	
	public Expression withTokens(List<Token> tokens, int start, int end) {
		this.tokens = tokens.subList(start, end);
		return this;
	}
	
    public abstract Value evaluate(BlockContext ctx);

    public abstract String format();

}
