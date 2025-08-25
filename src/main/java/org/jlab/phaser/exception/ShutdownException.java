package org.jlab.phaser.exception;

/**
 * An exception that arises during program shutdown.
 *
 * @author ryans
 */
public class ShutdownException extends PhaserException {

  /**
   * Create an exception with only a message.
   *
   * @param message The message.
   */
  public ShutdownException(String message) {
    super(message);
  }

  /**
   * Create an exception with a message and a cause.
   *
   * @param message The message
   * @param cause The cause
   */
  public ShutdownException(String message, Throwable cause) {
    super(message, cause);
  }
}
