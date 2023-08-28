package org.jlab.phaser.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An immutable page of jobs as returned from a jobs query.  The number of records in the
 * page is determined based on the paginator, filter, and actual number in the
 * database.
 * 
 * @author ryans
 */
public final class JobPage {
    private final List<JobRecord> records;
    private final Paginator paginator;
    private final JobFilter filter;

    /**
     * Create a new JobPage.
     * 
     * @param records The job records
     * @param paginator The paginator
     * @param filter The filter
     */
    public JobPage(List<JobRecord> records, Paginator paginator, JobFilter filter) {
        this.records = new ArrayList<>(records);
        this.paginator = paginator;
        this.filter = filter;
    }

    /**
     * Return the job records.
     * 
     * @return The records
     */
    public List<JobRecord> getRecords() {
        // This potentially has better performance than doing a copy
        return Collections.unmodifiableList(records); 
    }

    /**
     * Return the paginator.
     * 
     * @return The paginator
     */
    public Paginator getPaginator() {
        return paginator;
    }

    /**
     * Return the filter.
     * 
     * @return The filter
     */
    public JobFilter getFilter() {
        return filter;
    }
}
