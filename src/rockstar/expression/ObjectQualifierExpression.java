/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

import com.sun.javafx.fxml.expression.VariableExpression;
import rockstar.runtime.BlockContext;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class ObjectQualifierExpression extends CompoundExpression {

    public VariableReference getObjectRef() {
        return (VariableReference) this.getParameters().get(1);
    }

    public VariableReference getQualifierRef() {
        return (VariableReference) this.getParameters().get(0);
    }

    @Override
    public int getPrecedence() {
        return 50;
    }

    @Override
    public int getParameterCount() {
        return 2;
    }

    @Override
    public String getFormat() {
        return String.format("%s.%s", getObjectRef(), getQualifierRef());
    }

    @Override
    public Value evaluate(BlockContext ctx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
