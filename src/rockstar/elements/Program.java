/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.elements;

/**
 *
 * @author Gabor
 */
public class Program extends Block {
    
    private String name;

    public Program(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "( Program " + name + ")\n" +
                super.toString(); 
    }
    
    
    
}
