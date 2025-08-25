package org.jlab.phaser.model;

import java.util.LinkedHashSet;

/**
 * An immutable job specification as required by the Phaser server.
 *
 * <p>This class differs from the JobRecord class. The JobRecord class is an entity representing the
 * audit information stored in the Phaser database. The JobSpecification class is an entity
 * specifying job parameters as understood by the Phaser Server.
 *
 * @author ryans
 */
public final class JobSpecification {

  private final boolean correct;
  private final boolean continuous;
  private final float maxPhaseError;
  private final float maxMomentumError;
  private final int kickSamples;
  private final LinkedHashSet<String> cavities;

  /**
   * Create a new JobSpecification.
   *
   * @param correct true for correction, false for measurement only
   * @param continuous true for continuous, false for once-and-done
   * @param maxPhaseError Initial maximum phase error
   * @param maxMomentumError Initial maximum momentum error
   * @param kickSamples Number of samples per kick
   * @param cavities The ordered set of cavities to phase
   */
  public JobSpecification(
      boolean correct,
      boolean continuous,
      float maxPhaseError,
      float maxMomentumError,
      int kickSamples,
      LinkedHashSet<String> cavities) {
    this.correct = correct;
    this.continuous = continuous;
    this.maxPhaseError = maxPhaseError;
    this.maxMomentumError = maxMomentumError;
    this.kickSamples = kickSamples;
    // A copy is made to preserve immutability
    this.cavities = new LinkedHashSet<>(cavities);
  }

  /**
   * Returns true if corrections should be made, false if only measurements should be taken.
   *
   * @return correct
   */
  public boolean isCorrect() {
    return correct;
  }

  /**
   * Returns true if phasing should run continuously, false if it should run through the set of
   * cavities once only.
   *
   * @return continuous
   */
  public boolean isContinuous() {
    return continuous;
  }

  /**
   * Return the initial maximum phase angle error.
   *
   * @return max phase angle error
   */
  public float getMaxPhaseError() {
    return maxPhaseError;
  }

  /**
   * Return the initial maximum momentum error (dp/p).
   *
   * @return max momentum error (dp/p)
   */
  public float getMaxMomentumError() {
    return maxMomentumError;
  }

  /**
   * Return the number of samples per kick.
   *
   * @return Samples per kick
   */
  public int getKickSamples() {
    return kickSamples;
  }

  /**
   * Return the ordered set of cavities to phase.
   *
   * @return The cavities
   */
  public LinkedHashSet<String> getCavities() {
    // A copy is returned for immutability
    return new LinkedHashSet<>(cavities);
  }
}
