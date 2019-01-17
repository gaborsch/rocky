/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.parser.Line;

/**
 *
 * @author Gabor
 */
public class Statement {
    
    private Line line;

    protected Statement() {}

    public Line getLine() {
        return line;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (line != null) {
            line.getTokens().forEach((token) -> {
                sb.append(token).append("/");
            });
        }
        return sb.toString(); 
    }
    
    public void setDebugInfo(Line line) {
        this.line = line;      
    }
    
}
