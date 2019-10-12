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

    private final RockstarTest.Expected expected;

    private boolean isExecuted;
    private Throwable exception;
    private String message;
    private String debugInfo;

    public TestResult(RockstarTest.Expected expected) {
        this.isExecuted = true;
        this.expected = expected;
    }

//    public void setExecuted(boolean isExecuted) {
//        this.isExecuted = isExecuted;
//    }
    public void setException(Throwable exception) {
        this.isExecuted = false;
        this.exception = exception;
    }

    public void setMessage(String message) {
        this.isExecuted = false;
        this.message = message;
    }

    public String getMessage() {
        return message != null ? message
                : ("<" + (isExecuted ? "" : "not ") + "executed"
                + (exception == null ? "" : " with " + exception.getClass().getSimpleName()) + ">");
    }

    public Throwable getException() {
        return exception;
    }

    /**
     * whether the program was run correctly
     *
     * @return
     */
    public boolean isExecuted() {
        return isExecuted;
    }

    /**
     * Whether the test met the expectations
     *
     * @return
     */
    public boolean isPassed() {
        switch (expected) {
            case CORRECT:
                return isExecuted && (message == null) && (exception == null);
            case PARSE_ERROR:
                return !isExecuted && (message != null) && (exception == null);
            case RUNTIME_ERROR:
                return !isExecuted && (exception != null);
            default:
                throw new RuntimeException("Unkonwn expected:" + expected);
        }
    }

    public String getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(String debugInfo) {
        this.debugInfo = debugInfo;
    }

}
