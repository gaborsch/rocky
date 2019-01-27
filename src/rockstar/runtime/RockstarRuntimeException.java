/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

/**
 * Denotes a runtime exception while executing a parsed Rockstar program
 * @author Gabor
 */
public class RockstarRuntimeException extends RuntimeException {

    public RockstarRuntimeException(String message) {
        super(message);
    }
    
}
