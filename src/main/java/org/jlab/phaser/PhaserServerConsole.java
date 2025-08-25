package org.jlab.phaser;

import java.math.BigInteger;
import java.util.LinkedHashSet;
import org.jlab.phaser.exception.CommandException;
import org.jlab.phaser.model.JobSpecification;

/**
 * A contract for classes to issue commands to the phaser server.
 *
 * <p>When a method from this class returns without exception it means that the command was
 * successfully completed and all notifications arising as a result have already been dispatched.
 *
 * @author ryans
 */
public interface PhaserServerConsole {

  /**
   * Returns the set of all cavities which are eligible for phasing. The set is ordered
   * alphabetically, which happens to also be S-coordinate order (distance from injector).
   *
   * @return The ordered set of cavities
   * @throws CommandException If unable to obtain the set
   */
  public LinkedHashSet<String> cavities() throws CommandException;

  /**
   * Apply phase error corrections for the result IDs in the supplied array.
   *
   * @param resultIdArray The result ID array
   * @throws CommandException If unable to apply the corrections
   */
  public void applyCorrections(BigInteger[] resultIdArray) throws CommandException;

  /**
   * Starts the phasing process with the supplied job specification.
   *
   * @param job The job specification
   * @throws CommandException If unable to start the phasing process
   */
  public void start(JobSpecification job) throws CommandException;

  /**
   * Pause the phasing process.
   *
   * @throws CommandException If unable to pause the phasing process
   */
  public void pause() throws CommandException;

  /**
   * Resume the phasing process.
   *
   * @throws CommandException If unable to resume the phasing process.
   */
  public void resume() throws CommandException;

  /**
   * Fetch the server version from the server.
   *
   * @return The server version string
   * @throws CommandException If unable to query the server for it's version string.
   */
  public String serverVersion() throws CommandException;

  /**
   * Stop the phasing process.
   *
   * @throws CommandException If unable to stop the phasing process.
   */
  public void stop() throws CommandException;

  /**
   * Skip phasing of the current cavity.
   *
   * @throws CommandException If unable to skip phasing of the current cavity.
   */
  public void skip() throws CommandException;
}
