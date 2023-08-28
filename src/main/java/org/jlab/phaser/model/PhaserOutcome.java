package org.jlab.phaser.model;

/**
 * The outcomes of the Phaser process.
 *
 * Note: the order declared matters because we use PhaserOutcome.ordinal() to match GUI select box
 * index. The order that they are declared does not match the alphabetical order in javadoc. The
 * declared order is: MEASURED, SKIPPED, BYPASSED, CORRECTED, ERROR, DEFERRED.
 *
 * @author ryans
 */
public enum PhaserOutcome {
    /**
     * Cavity was successfully measured and results recorded in the database.
     */
    MEASURED,
    /**
     * Cavity was skipped because it had a problem and an operator requested skip after one or more
     * attempts.
     */
    SKIPPED,
    /**
     * Cavity was bypassed (skipped) because the control system has designated the cavity as
     * bypassed.
     */
    BYPASSED,
    /**
     * Cavity correction was applied and result record updated in the database. Implies cavity had
     * previously been measured or deferred.
     */
    CORRECTED,
    /**
     * Server determined that the cavity could not be corrected and cannot be tried again (phase
     * angle error too great or measure date too old for example).
     */
    ERROR, // Rename CORRECTON_ERROR?
    /**
     * Server determined that the cavity could not be corrected, but could be attempted at a future
     * date (temporary control system or database unavailable scenario for example).
     */
    DEFERRED // Rename CORRECTION_DEFERRED?
}
