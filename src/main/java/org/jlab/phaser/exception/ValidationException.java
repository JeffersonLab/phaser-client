package org.jlab.phaser.exception;

/**
 * An exception that arises during validation.
 *
 * @author ryans
 */
public class ValidationException extends PhaserException {

  /**
   * Create an exception with only a message.
   *
   * @param message The message.
   */
  public ValidationException(String message) {
    super(message);
  }

  /**
   * Create an exception with a message and a cause.
   *
   * @param message The message
   * @param cause The cause
   */
  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
