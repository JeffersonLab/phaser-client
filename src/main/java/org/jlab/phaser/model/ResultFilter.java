package org.jlab.phaser.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An immutable result query filter.
 *
 * <p>Individual filter parameters are ignored if null. The parameters which are not null are
 * combined with SQL "and".
 *
 * @author ryans
 */
public final class ResultFilter {

  private final Long jobId;
  private final Date minCavityStartDate;
  private final Date maxCavityStartDate;
  private final String cavityName;
  private final Float minPhaseError;
  private final PhaserOutcome outcome;

  /**
   * Create a new ResultFilter.
   *
   * @param jobId The jobId
   * @param minCavityStartDate The minimum cavity start date
   * @param maxCavityStartDate The maximum cavity start date
   * @param cavityName The cavity name
   * @param minPhaseError The minimum phase angle error
   * @param outcome The outcome
   */
  public ResultFilter(
      Long jobId,
      Date minCavityStartDate,
      Date maxCavityStartDate,
      String cavityName,
      Float minPhaseError,
      PhaserOutcome outcome) {
    this.jobId = jobId;
    this.minCavityStartDate =
        minCavityStartDate == null ? null : new Date(minCavityStartDate.getTime());
    this.maxCavityStartDate =
        maxCavityStartDate == null ? null : new Date(maxCavityStartDate.getTime());
    this.cavityName = cavityName;
    this.minPhaseError = minPhaseError;
    this.outcome = outcome;
  }

  /**
   * Return the job ID.
   *
   * @return The job ID
   */
  public Long getJobId() {
    return jobId;
  }

  /**
   * Return the minimum cavity start date.
   *
   * @return The minimum cavity start date
   */
  public Date getMinCavityStartDate() {
    return minCavityStartDate == null ? null : new Date(minCavityStartDate.getTime());
  }

  /**
   * Return the maximum cavity start date.
   *
   * @return The maximum cavity start date
   */
  public Date getMaxCavityStartDate() {
    return maxCavityStartDate == null ? null : new Date(maxCavityStartDate.getTime());
  }

  /**
   * Return the cavity name.
   *
   * @return The cavity name
   */
  public String getCavityName() {
    return cavityName;
  }

  /**
   * Return the minimum phase angle error.
   *
   * @return The minimum phase angle error
   */
  public Float getMinPhaseError() {
    return minPhaseError;
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
   * Return the human readable where clause based on this filter. This is useful for echoing the
   * user's selection.
   *
   * @param timestampFormat The SimpeDateFormat timestamp format to use
   * @return The where clause
   */
  public String toHumanWhereClause(String timestampFormat) {
    List<String> whereList = new ArrayList<>();

    String w;

    SimpleDateFormat dateFormatter = new SimpleDateFormat(timestampFormat);
    DecimalFormat decimalFormatter = new DecimalFormat("#0.##");

    if (getJobId() != null) {
      w = "Job # is " + getJobId();
      whereList.add(w);
    }

    if (getMinCavityStartDate() != null) {
      w = "Min Cavity Start Date is " + dateFormatter.format(getMinCavityStartDate());
      whereList.add(w);
    }

    if (getMaxCavityStartDate() != null) {
      w = "Max Cavity Start Date is " + dateFormatter.format(getMaxCavityStartDate());
      whereList.add(w);
    }

    if (getCavityName() != null && !getCavityName().isEmpty()) {
      w = "Cavity is " + getCavityName();
      whereList.add(w);
    }

    if (getMinPhaseError() != null) {
      w = "Min Phase Error is " + decimalFormatter.format(getMinPhaseError());
      whereList.add(w);
    }

    if (getOutcome() != null) {
      w = "Outcome is " + getOutcome();
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
   * Returns the SQL where clause based on this filter. The clause can be used in a
   * PreparedStatement as it uses "?" where parameters go.
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

    if (getMinCavityStartDate() != null) {
      w = "start_date >= ?";
      whereList.add(w);
    }

    if (getMaxCavityStartDate() != null) {
      w = "start_date <= ?";
      whereList.add(w);
    }

    if (getCavityName() != null && !getCavityName().isEmpty()) {
      w = "cavity like ?";
      whereList.add(w);
    }

    if (getMinPhaseError() != null) {
      w = "ABS(phase_error) >= ?";
      whereList.add(w);
    }

    if (getOutcome() != null) {
      w = "outcome = ?";
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
   * Assigns prepared statement parameters for the SQL where clause returned from toSqlWhereClause.
   *
   * @param stmt The PreparedStatement
   * @throws SQLException If unable to assign the parameter
   */
  public void assignStatementParameters(PreparedStatement stmt) throws SQLException {
    int parameterIndex = 1;

    if (getJobId() != null) {
      stmt.setLong(parameterIndex++, getJobId());
    }

    if (getMinCavityStartDate() != null) {
      stmt.setDate(parameterIndex++, new java.sql.Date(getMinCavityStartDate().getTime()));
    }

    if (getMaxCavityStartDate() != null) {
      stmt.setDate(parameterIndex++, new java.sql.Date(getMaxCavityStartDate().getTime()));
    }

    if (getCavityName() != null && !getCavityName().isEmpty()) {
      stmt.setString(parameterIndex++, getCavityName().toUpperCase());
    }

    if (getMinPhaseError() != null) {
      stmt.setFloat(parameterIndex++, getMinPhaseError());
    }

    if (getOutcome() != null) {
      stmt.setString(parameterIndex++, getOutcome().name());
    }
  }
}
