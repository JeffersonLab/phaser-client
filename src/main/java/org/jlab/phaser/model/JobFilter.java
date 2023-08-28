package org.jlab.phaser.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An immutable job query filter.
 *
 * Individual filter parameters are ignored if null. The parameters which are
 * not null are combined with SQL "and".
 *
 * @author ryans
 */
public final class JobFilter {

    private final Long jobId;
    private final Date minJobStartDate;
    private final Date maxJobStartDate;

    /**
     * Creates a new job query filter.
     *
     * @param jobId The job ID
     * @param minJobStartDate The minimum job start date
     * @param maxJobStartDate The maximum job start date
     */
    public JobFilter(Long jobId, Date minJobStartDate, Date maxJobStartDate) {
        this.jobId = jobId;
        this.minJobStartDate = minJobStartDate == null ? null : new Date(minJobStartDate.getTime());
        this.maxJobStartDate = maxJobStartDate == null ? null : new Date(maxJobStartDate.getTime());
    }

    /**
     * Return the job ID.
     *
     * @return The ID
     */
    public Long getJobId() {
        return jobId;
    }

    /**
     * Return the minimum job start date.
     *
     * @return The date
     */
    public Date getMinJobStartDate() {
        return minJobStartDate == null ? null : new Date(minJobStartDate.getTime());
    }

    /**
     * Return the maximum job start date.
     *
     * @return The date
     */
    public Date getMaxJobStartDate() {
        return maxJobStartDate == null ? null : new Date(maxJobStartDate.getTime());
    }

    /**
     * Return the human readable where clause based on this filter. This is
     * useful for echoing the user's selection.
     *
     * @param timestampFormat The SimpeDateFormat timestamp format to use
     * @return The where clause
     */
    public String toHumanWhereClause(String timestampFormat) {
        List<String> whereList = new ArrayList<>();

        String w;

        SimpleDateFormat dateFormatter = new SimpleDateFormat(timestampFormat);

        if (getJobId() != null) {
            w = "Job # is " + getJobId();
            whereList.add(w);
        }

        if (getMinJobStartDate() != null) {
            w = "Min Job Start Date is " + dateFormatter.format(
                    getMinJobStartDate());
            whereList.add(w);
        }

        if (getMaxJobStartDate() != null) {
            w = "Max Job Start Date is " + dateFormatter.format(
                    getMaxJobStartDate());
            whereList.add(w);
        }

        String where = "";

        if (!whereList.isEmpty()) {
            where = "where " + whereList.get(0) + " ";

            for (int i = 1; i < whereList.size(); i++) {
                String wh = whereList.get(i);
                where = where + "and " + wh + " ";
            }
        }

        return where;
    }

    /**
     * Returns the SQL where clause based on this filter. The clause can be used
     * in a PreparedStatement as it uses "?" where parameters go.
     *
     * @return The where clause
     */
    public String toSqlWhereClause() {
        List<String> whereList = new ArrayList<>();

        String w;

        if (getJobId() != null) {
            w = "job_id = ?";
            whereList.add(w);
        }

        if (getMinJobStartDate() != null) {
            w = "start_date >= ?";
            whereList.add(w);
        }

        if (getMaxJobStartDate() != null) {
            w = "start_date <= ?";
            whereList.add(w);
        }

        String where = "";

        if (!whereList.isEmpty()) {
            where = " where ";
            for (String wh : whereList) {
                where = where + wh + " and ";
            }

            where = where.substring(0, where.length() - 5);
        }

        return where;
    }

    /**
     * Assigns prepared statement parameters for the SQL where clause
     * returned from toSqlWhereClause.
     * 
     * @param stmt The PreparedStatement
     * @throws SQLException If unable to assign the parameter
     */
    public void assignStatementParameters(PreparedStatement stmt) throws
            SQLException {
        int parameterIndex = 1;

        if (getJobId() != null) {
            stmt.setLong(parameterIndex++, getJobId());
        }

        if (getMinJobStartDate() != null) {
            stmt.setDate(parameterIndex++, new java.sql.Date(
                    getMinJobStartDate().getTime()));
        }

        if (getMaxJobStartDate() != null) {
            stmt.setDate(parameterIndex++, new java.sql.Date(
                    getMaxJobStartDate().getTime()));
        }
    }
}
