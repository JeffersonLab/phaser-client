package org.jlab.phaser;

import java.util.Calendar;
import org.jlab.phaser.model.JobSpecification;

/**
 * A contract for classes interested in server notifications.
 *
 * @author ryans
 */
public interface NotificationListener {

  /**
   * Notification of a new status message from the server.
   *
   * <p>Only one status message can be active at any time and this message should replace any
   * previous message. A null or empty String are equivalent and indicate that the active message
   * should be cleared.
   *
   * @param message The message
   * @param error true if error message, false otherwise
   */
  public void statusNotification(String message, Boolean error);

  /**
   * Notification of a job change.
   *
   * <p>If the job parameter is null then all associated parameters should also be null.
   *
   * @param job The PhaserJob
   * @param jobId The job ID
   * @param jobStart The job start date
   * @param paused true if the job is paused, false otherwise
   * @param loop The current loop number
   * @param cavity The current cavity being phased
   * @param cavityStart The start date of the current cavity
   * @param progress The percent complete (0-100) of the current cavity
   * @param label The progress label
   */
  public void jobNotification(
      JobSpecification job,
      Long jobId,
      Calendar jobStart,
      Boolean paused,
      Integer loop,
      String cavity,
      Calendar cavityStart,
      Integer progress,
      String label);

  /**
   * Notification of paused / not paused state.
   *
   * @param paused true if paused, false otherwise
   */
  public void pausedNotification(Boolean paused);

  /**
   * Notification of phaser loop number.
   *
   * @param loop Phaser loop number
   */
  public void loopNotification(Integer loop);

  /**
   * Notification of the start of a new cavity phasing.
   *
   * @param start The start date
   * @param cavity The cavity name
   */
  public void cavityNotification(Calendar start, String cavity);

  /**
   * Notification of current cavity progress (or waiting progress). Progress is an integer between 0
   * and 100.
   *
   * @param value The progress percent
   * @param label The progress label
   */
  public void progressNotification(Integer value, String label);
}
