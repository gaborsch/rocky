/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.test;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gabor
 */
public class ClsTest {
    
    public static void main(String[] args) {
        testCls("rockstar.Rockstar");
        testCls("rockstar.parser.Parser");
        testCls("rockstar.runtime.Dec64");
        testCls("rockstar.expression.VariableReference");
        
    }
    
    public static void testCls(String clsName) {
        try {
            System.out.println("Testing "+clsName+": ");
            Class<?> cls = Class.forName(clsName);
            System.out.println((cls.getConstructors().length) + " constructors");
        } catch (ClassNotFoundException ex) {
             System.out.println("Not found");
        } catch (java.lang.ExceptionInInitializerError initEx) {
             System.out.println("ExceptionInInitializerError");
             throw initEx;
        }
    }
    
}
