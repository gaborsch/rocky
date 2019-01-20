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
public class LogicalExpression extends CompoundExpression {
    
    public enum LogicalType {
        AND,
        OR,
        NOR
    }
    
    private LogicalType type;

    public LogicalExpression(LogicalType type) {
        super();
        this.type = type;
    }
    
    
            
    @Override
    protected String getFormat() {
        return "(%s "+type+" %s)";
    }
    
    @Override
    public int getPrecedence() {
        return 800;
    }
    
    @Override
    public int getParameterCount() {
        return 2;
    }
   
}
