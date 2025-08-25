package org.jlab.phaser.exception;

/**
 * The root of the Phaser exception hierarchy.
 *
 * @author ryans
 */
public class PhaserException extends Exception {

  /**
   * Create an exception with only a message.
   *
   * @param message The message.
   */
  public PhaserException(String message) {
    super(message);
  }

  /**
   * Create an exception with a message and a cause.
   *
   * @param message The message
   * @param cause The cause
   */
  public PhaserException(String message, Throwable cause) {
    super(message, cause);
  }
}
