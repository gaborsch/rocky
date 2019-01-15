/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Gabor
 */
public class ExpressionFactory {
    
    public static Expression getExpressionFor(List<String> tokens) {
        return new DummyExpression(tokens);
    }

    public static List<String> COMMON_VARIABLE_PREFIXES = Arrays.asList(new String[] {"a", "an", "the", "my", "your"});
    
    public static VariableReference getVariableReferenceFor(List<String> tokens) {
        String name = null;
        if (tokens.isEmpty()) {
            return null;
        }
        String token0 = tokens.get(0);
        if (Character.isUpperCase(token0.charAt(0))) {
            // proper variable
            StringBuilder sb = new StringBuilder();
            for(String token : tokens) {
                // all parts of a Proper Name must start with capital letter
                if (Character.isUpperCase(token.charAt(0))) {
                    sb.append(sb.length() > 0 ? " " : "").append(token);
                } else {
                    return null;
                }
            }
            name = sb.toString();
        } else if (tokens.size() == 2){
            if (COMMON_VARIABLE_PREFIXES.contains(token0)) {
                String token1 = tokens.get(1);
                if (token1.toLowerCase().equals(token1)) {
                    name = token0 + " " + token1;
                }
            }
        } 
        return (name == null) ? null : new VariableReference(name);
    }
    
}
