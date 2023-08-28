package org.jlab.phaser.exception;

/**
 * An exception that arises during program initialization.
 * 
 * @author ryans
 */
public class InitializationException extends PhaserException {

    /**
     * Create an exception with only a message.
     * 
     * @param message The message.
     */    
    public InitializationException(String message) {
        super(message);
    }
    
    /**
     * Create an exception with a message and a cause.
     * 
     * @param message The message
     * @param cause The cause 
     */    
    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
