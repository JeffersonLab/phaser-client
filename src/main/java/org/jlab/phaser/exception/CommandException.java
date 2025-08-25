package org.jlab.phaser.exception;

/**
 * An exception that arises executing a command.
 *
 * @author ryans
 */
public class CommandException extends PhaserException {

  /**
   * Create an exception with only a message.
   *
   * @param message The message.
   */
  public CommandException(String message) {
    super(message);
  }

  /**
   * Create an exception with a message and a cause.
   *
   * @param message The message
   * @param cause The cause
   */
  public CommandException(String message, Throwable cause) {
    super(message, cause);
  }
}
