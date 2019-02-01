/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import rockstar.runtime.BlockContext;

/**
 *
 * @author Gabor
 */
public class Program extends Block {
    
    private final String name;

    public String getName() {
        return name;
    }
    
    public Program(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "( Program " + name + ")";
    }

    @Override
    public String explain(BlockContext ctx) {
        return null;
    }
    
}
