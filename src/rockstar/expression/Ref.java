/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

/**
 *
 * @author Gabor
 */
public class Ref {
    
    public enum Type {
        LIST,
        ASSOC_ARRAY
    }
    
    private final Type type;
    private final Expression expression;

    public Ref(Type type, Expression expression) {
        this.type = type;
        this.expression = expression;
    }

    public Type getType() {
        return type;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Ref) {
            Ref o = (Ref) obj;
            return type == o.type 
                    && expression.equals(o.expression);
        }
        return false;
    }
    
    
}
