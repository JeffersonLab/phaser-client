package org.jlab.phaser.model;

import java.util.Date;

/**
 * An immutable job record.
 *
 * This class represents a row of data from the JOB table of the Phaser database plus a count of
 * results associated with the job.
 *
 * @author ryans
 */
public final class JobRecord {

    private final long id;
    private final Date startDate;
    private final Date endDate;
    private final boolean correct;
    private final boolean continuous;
    private final float maxPhaseError;
    private final float maxMomentumError;
    private final int kickSamples;
    private final long count;

    /**
     * Create a new JobRecord.
     *
     * @param id The job ID
     * @param startDate The job start date
     * @param endDate The job end date
     * @param correct true to correct, false to measure only
     * @param continuous true for continuous running, false for once-and-done
     * @param maxPhaseError The maximum phase angle error
     * @param maxMomentumError The maximum momentum error (dp/p)
     * @param kickSamples The number of samples per kick
     * @param count The number of results
     */
    public JobRecord(long id, Date startDate, Date endDate, boolean continuous, boolean correct,
            float maxPhaseError, float maxMomentumError, int kickSamples, long count) {
        this.id = id;
        this.startDate = startDate == null ? null : new Date(startDate.getTime());
        this.endDate = endDate == null ? null : new Date(endDate.getTime());
        this.correct = correct;
        this.continuous = continuous;
        this.maxPhaseError = maxPhaseError;
        this.maxMomentumError = maxMomentumError;
        this.kickSamples = kickSamples;
        this.count = count;
    }

    /**
     * Return the maximum momentum error (dp/p).
     *
     * @return The maximum momentum error (dp/p)
     */
    public float getMaxMomentumError() {
        return maxMomentumError;
    }

    /**
     * Return the number of samples per kick.
     *
     * @return The kick samples
     */
    public int getKickSamples() {
        return kickSamples;
    }

    /**
     * Return the job ID.
     *
     * @return The ID
     */
    public long getId() {
        return id;
    }

    /**
     * Return the job start date.
     *
     * @return The start date
     */
    public Date getStartDate() {
        return startDate == null ? null : new Date(startDate.getTime());
    }

    /**
     * Return the job end date.
     *
     * @return The end date
     */
    public Date getEndDate() {
        return endDate == null ? null : new Date(endDate.getTime());
    }

    /**
     * Return true to correct and false to measure only.
     *
     * @return correct
     */
    public boolean isCorrect() {
        return correct;
    }

    /**
     * Return true for continuous running, false for once-and-done
     *
     * @return continuous
     */
    public boolean isContinuous() {
        return continuous;
    }

    /**
     * Return the initial maximum phase angle error.
     *
     * @return max phase angle error (degrees)
     */
    public float getMaxPhaseError() {
        return maxPhaseError;
    }

    /**
     * Return a count of the number of results associated with this job.
     *
     * @return The count
     */
    public long getCount() {
        return count;
    }
}
