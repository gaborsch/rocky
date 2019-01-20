/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gabor
 */
public class DummyExpression extends Expression {
    
    private final List<String> tokens;
    private String errorMsg;
    
    public DummyExpression(List<String> tokens) {
        this.tokens = new ArrayList<>(tokens);
    }
    
    public DummyExpression(List<String> tokens, int errorIdx, String errorMsg) {
        this.tokens = new ArrayList<>(tokens);
        this.errorMsg = errorMsg;
        setErrorIndex(errorIdx);
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
        return sb.toString() + (errorMsg == null ? "" : ("\n    "+errorMsg)) ;
    }
}
