package com.shahzheeb.rules.ruleengine.droolsruleengine;

/**
 * @author ben.hahn
 *
 * ContextInitializationException - exception thrown during Spring context discovery
 * and initialization  
 */
public class ContextInitializationException extends Exception {
	
	private static final long serialVersionUID = 456858756819516741L;
	
	private static final String EXCEPTION_STRING = "Could not create Application Context. Check XML context path";  
	/**
     * Creates a new instance of <code>ContextInitializationException</code>
     * without detail message.
     */
    public ContextInitializationException() {
        super(EXCEPTION_STRING);
    }
    
    /** Constructs an instance of <code>ContextInitializationException</code>
     * with the context location.
     * @param path - the context location tried
     */
    public ContextInitializationException(String  path) {
        super(EXCEPTION_STRING + "\nContext Location is " + path);
    }

    /** Constructs an instance of <code>ContextInitializationException</code>
     * with the context location and cause of the exception.
     * @param path - the context location tried
     * @param cause a Throwable that contains the cause.
     */
    public ContextInitializationException(String  path, Throwable cause) {
        super(EXCEPTION_STRING + "\nContext Location is " + path, cause);
    }
    
    /** Constructs an instance of <code>ContextInitializationException</code>
     * with the cause of the exception.
     * @param cause a Throwable that contains the cause.
     */
    public ContextInitializationException(Throwable cause) {
        super(cause);
    }

    
}
