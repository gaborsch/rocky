/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.elements;

import rockstar.parser.Line;

/**
 *
 * @author Gabor
 */
public class Statement {
    
    private Line l;

    protected Statement() {}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        l.getTokens().forEach((token) -> {
            sb.append(token).append("/");
        });
        return "[" + l.getLnum() + "] " + getClass().getSimpleName() + ": " + sb /*+ line*/ +  "\n"; 
    }
    
    public void setDebugInfo(Line line) {
        this.l = line;      
    }
    
}
