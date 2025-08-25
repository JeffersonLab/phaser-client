package org.jlab.phaser.model;

/**
 * The states of the Phaser process.
 *
 * @author ryans
 */
public enum PhaserJobState {
  /** The server is idle and ready for a new job submission. */
  IDLE,
  /** The server is working on a job. */
  WORKING,
  /** The server has a job, but is paused. */
  PAUSED,
  /** The server has a job, but a problem has occurred so the server is waiting before retry. */
  ERROR_RETRY_WAIT,
  /**
   * The server state is unknown because the client hasn't received a state notification from the
   * server yet.
   */
  UNKNOWN
}
