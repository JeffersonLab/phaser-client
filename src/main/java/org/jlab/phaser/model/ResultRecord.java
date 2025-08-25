package org.jlab.phaser.model;

import java.math.BigInteger;
import java.util.Date;

/**
 * An immutable result record.
 *
 * <p>This class represents a row of data from the RESULT table of the Phaser database.
 *
 * @author ryans
 */
public final class ResultRecord {

  private final BigInteger resultId;
  private final Date startDate;
  private final Date endDate;
  private final Date correctionDate;
  private final String cavity;
  private final Float phase;
  private final Float phaseError;
  private final PhaserOutcome outcome;
  private final String correctionErrorReason;

  /**
   * Create a new ResultRecord.
   *
   * @param resultId The result ID
   * @param startDate The start date
   * @param endDate The end date
   * @param correctionDate The date the corrections were applied
   * @param cavity The cavity name
   * @param phase The phase angle
   * @param phaseError The phase angle error
   * @param outcome The outcome
   * @param correctionErrorReason The correction error reason or null if none
   */
  public ResultRecord(
      BigInteger resultId,
      Date startDate,
      Date endDate,
      Date correctionDate,
      String cavity,
      Float phase,
      Float phaseError,
      PhaserOutcome outcome,
      String correctionErrorReason) {
    this.resultId = resultId;
    this.startDate = startDate == null ? null : new Date(startDate.getTime());
    this.endDate = endDate == null ? null : new Date(endDate.getTime());
    this.correctionDate = correctionDate == null ? null : new Date(correctionDate.getTime());
    this.cavity = cavity;
    this.phase = phase;
    this.phaseError = phaseError;
    this.outcome = outcome;
    this.correctionErrorReason = correctionErrorReason;
  }

  /**
   * Return the result ID (unique / primary key).
   *
   * @return The result ID
   */
  public BigInteger getResultId() {
    return resultId;
  }

  /**
   * Return the start date.
   *
   * @return The start date
   */
  public Date getStartDate() {
    return startDate == null ? null : new Date(startDate.getTime());
  }

  /**
   * Return the end date.
   *
   * @return The end date
   */
  public Date getEndDate() {
    return endDate == null ? null : new Date(endDate.getTime());
  }

  /**
   * Return the correction date.
   *
   * @return The correction date
   */
  public Date getCorrectionDate() {
    return correctionDate;
  }

  /**
   * Return the cavity name.
   *
   * @return The cavity name
   */
  public String getCavity() {
    return cavity;
  }

  /**
   * Return the measured phase angle.
   *
   * @return The phase angle
   */
  public Float getPhase() {
    return phase;
  }

  /**
   * Return the phase angle error.
   *
   * @return The phase angle error
   */
  public Float getPhaseError() {
    return phaseError;
  }

  /**
   * Return the outcome.
   *
   * @return The outcome
   */
  public PhaserOutcome getOutcome() {
    return outcome;
  }

  /**
   * Return the correction error reason or null if none.
   *
   * @return The correction error reason
   */
  public String getCorrectionErrorReason() {
    return correctionErrorReason;
  }
}
