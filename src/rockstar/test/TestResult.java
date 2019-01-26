/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.test;

/**
 *
 * @author Gabor
 */
public class TestResult {
    
    boolean isPassed;
    Throwable exception;
    String message;

    public TestResult(Throwable exception, String message) {
        this.isPassed = false;
        this.message = message;
        this.exception = exception;
    }

    public TestResult(Throwable exception) {
        this.isPassed = false;
        this.message = exception.getMessage();
        this.exception = exception;
    }

    public TestResult(String message) {
        this.isPassed = false;
        this.message = message;
    }

    public TestResult() {
        this.isPassed = true;
    }
    
}
