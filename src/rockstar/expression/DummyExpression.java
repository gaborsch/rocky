/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.List;

/**
 *
 * @author Gabor
 */
public class DummyExpression extends Expression {

    private final List<String> tokens;

    public DummyExpression(List<String> tokens) {
        this.tokens = tokens;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DUMMYEXPR:/");
        tokens.forEach((token) -> {
            sb.append(token).append("/");
        });
        return sb.toString(); 
    }
}
