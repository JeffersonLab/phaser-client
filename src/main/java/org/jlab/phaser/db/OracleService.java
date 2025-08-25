package org.jlab.phaser.db;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.phaser.PhaserSwingClient;
import org.jlab.phaser.model.CavityWithLastCorrection;
import org.jlab.phaser.model.JobFilter;
import org.jlab.phaser.model.JobRecord;
import org.jlab.phaser.model.Paginator;
import org.jlab.phaser.model.PhaserOutcome;
import org.jlab.phaser.model.ResultFilter;
import org.jlab.phaser.model.ResultRecord;

/**
 * Manages connections and queries the Oracle database used to store the Phaser results and jobs.
 * This class has package visibility as it is intended to be used by the OracleJdbcConsole only and
 * is final because it isn't intended to be extended.
 *
 * @author ryans
 */
final class OracleService {

  private static final Logger LOGGER = Logger.getLogger(OracleService.class.getName());

  private Connection getConnection() throws SQLException {
    String url = PhaserSwingClient.CLIENT_PROPERTIES.getProperty("db.url");
    String user = PhaserSwingClient.CLIENT_PROPERTIES.getProperty("db.user");
    String password = PhaserSwingClient.CLIENT_PROPERTIES.getProperty("db.password");

    long start = System.currentTimeMillis();
    Connection con = DriverManager.getConnection(url, user, password);
    long end = System.currentTimeMillis();

    LOGGER.log(Level.FINEST, "Database Connection Obtained in {0} seconds", (end - start) / 1000.0);

    return con;
  }

  /**
   * Queries the database for a count of results based on the supplied filter.
   *
   * @param filter The filter
   * @return The count
   * @throws SQLException If unable to query the database
   */
  long countResults(ResultFilter filter) throws SQLException {
    long count = 0L;

    String where = filter.toSqlWhereClause();
    String query = "select count(*) from phaser_owner.result" + where;

    LOGGER.log(Level.FINEST, "Executing query: {0}", query);

    try (Connection con = getConnection()) {
      PreparedStatement stmt = con.prepareStatement(query);

      filter.assignStatementParameters(stmt);

      long start = System.currentTimeMillis();
      ResultSet rs = stmt.executeQuery();
      long end = System.currentTimeMillis();

      LOGGER.log(Level.FINEST, "Query Result Obtained in {0} seconds", (end - start) / 1000.0);

      if (rs.next()) {
        count = rs.getLong(1);
      }
    }

    return count;
  }

  /**
   * Queries the database for results based on the supplied filter and paginator.
   *
   * @param filter The filter
   * @param paginator The paginator
   * @return The results
   * @throws SQLException If unable to query the database
   */
  List<ResultRecord> findResults(ResultFilter filter, Paginator paginator) throws SQLException {
    List<ResultRecord> records = new ArrayList<>();

    String where = filter.toSqlWhereClause();
    String query = "select * from phaser_owner.result" + where + " order by result_id desc";
    String paginatedQuery =
        "select * from (select z.*, ROWNUM rnum from ("
            + query
            + ") z where ROWNUM <= "
            + (paginator.getOffset() + paginator.getMaxPerPage())
            + ") where rnum > "
            + paginator.getOffset();

    LOGGER.log(Level.FINEST, "Executing query: {0}", paginatedQuery);

    try (Connection con = getConnection()) {
      PreparedStatement stmt = con.prepareStatement(paginatedQuery);

      filter.assignStatementParameters(stmt);

      long start = System.currentTimeMillis();
      ResultSet rs = stmt.executeQuery();
      long end = System.currentTimeMillis();

      LOGGER.log(Level.FINEST, "Query Result Obtained in {0} seconds", (end - start) / 1000.0);

      while (rs.next()) {
        BigInteger resultId = rs.getBigDecimal("RESULT_ID").toBigInteger();
        Date startDate = rs.getDate("START_DATE");
        Date endDate = rs.getDate("END_DATE");
        Date correctionDate = rs.getDate("CORRECTION_DATE");
        String cavity = rs.getString("CAVITY");
        Float phase = rs.getFloat("PHASE"); // JDBC API for null is weird as hell (returns 0)
        if (rs.wasNull()) {
          phase = null;
        }
        Float phaseError = rs.getFloat("PHASE_ERROR");
        if (rs.wasNull()) {
          phaseError = null;
        }
        PhaserOutcome outcome = PhaserOutcome.valueOf(rs.getString("OUTCOME"));
        String correctionErrorReason = rs.getString("CORRECTION_ERROR_REASON");

        records.add(
            new ResultRecord(
                resultId,
                startDate,
                endDate,
                correctionDate,
                cavity,
                phase,
                phaseError,
                outcome,
                correctionErrorReason));
      }
    }

    return records;
  }

  /**
   * Queries the database for a count of jobs based on the supplied filter.
   *
   * @param filter The filter
   * @return The count
   * @throws SQLException If unable to query the database
   */
  long countJobs(JobFilter filter) throws SQLException {
    long count = 0L;

    String where = filter.toSqlWhereClause();
    String query = "select count(*) from phaser_owner.job" + where;

    LOGGER.log(Level.FINEST, "Executing query: {0}", query);

    try (Connection con = getConnection()) {
      PreparedStatement stmt = con.prepareStatement(query);

      filter.assignStatementParameters(stmt);

      long start = System.currentTimeMillis();
      ResultSet rs = stmt.executeQuery();
      long end = System.currentTimeMillis();

      LOGGER.log(Level.FINEST, "Query Result Obtained in {0} seconds", (end - start) / 1000.0);

      if (rs.next()) {
        count = rs.getLong(1);
      }
    }

    return count;
  }

  /**
   * Queries the database for jobs based on the supplied filter and paginator.
   *
   * @param filter The filter
   * @param paginator The paginator
   * @return The jobs
   * @throws SQLException If unable to query the database
   */
  List<JobRecord> findJobs(JobFilter filter, Paginator paginator) throws SQLException {
    List<JobRecord> records = new ArrayList<>();

    String where = filter.toSqlWhereClause();
    String query =
        "select j.*, (select count(*) from phaser_owner.result r where r.job_id = j.job_id) num_records from phaser_owner.job j"
            + where
            + " order by job_id desc";
    String paginatedQuery =
        "select * from (select z.*, ROWNUM rnum from ("
            + query
            + ") z where ROWNUM <= "
            + (paginator.getOffset() + paginator.getMaxPerPage())
            + ") where rnum > "
            + paginator.getOffset();

    LOGGER.log(Level.FINEST, "Executing query: {0}", paginatedQuery);

    try (Connection con = getConnection()) {
      PreparedStatement stmt = con.prepareStatement(paginatedQuery);

      filter.assignStatementParameters(stmt);

      long start = System.currentTimeMillis();
      ResultSet rs = stmt.executeQuery();
      long end = System.currentTimeMillis();

      LOGGER.log(Level.FINEST, "Query Result Obtained in {0} seconds", (end - start) / 1000.0);

      while (rs.next()) {
        long jobId = rs.getLong("JOB_ID");
        Date startDate = rs.getDate("START_DATE");
        Date endDate = rs.getDate("END_DATE");
        float maxPhaseError = rs.getFloat("MAX_PHASE_ERROR");
        float maxMomentumError = rs.getFloat("MAX_MOMENTUM_ERROR");
        int kickSamples = rs.getInt("KICK_SAMPLES");
        boolean continuous = "Y".equals(rs.getString("CONTINUOUS_YN"));
        boolean correct = "Y".equals(rs.getString("CORRECT_INCREMENTALLY_YN"));
        long numRecords = rs.getLong("NUM_RECORDS");

        records.add(
            new JobRecord(
                jobId,
                startDate,
                endDate,
                continuous,
                correct,
                maxPhaseError,
                maxMomentumError,
                kickSamples,
                numRecords));
      }
    }

    return records;
  }

  /**
   * Queries the results database table for the most recent correction date of each cavity having
   * result data.
   *
   * @return A list of cavities with their last correction date
   * @throws SQLException If unable to query the results
   */
  List<CavityWithLastCorrection> findCavitiesWithLastCorrection() throws SQLException {
    List<CavityWithLastCorrection> records = new ArrayList<>();

    String query =
        "select cavity, max(correction_date) as "
            + "correction_date from phaser_owner.result group by cavity";

    LOGGER.log(Level.FINEST, "Executing query: {0}", query);

    try (Connection con = getConnection()) {
      PreparedStatement stmt = con.prepareStatement(query);

      long start = System.currentTimeMillis();
      ResultSet rs = stmt.executeQuery();
      long end = System.currentTimeMillis();

      LOGGER.log(Level.FINEST, "Query Result Obtained in {0} seconds", (end - start) / 1000.0);

      while (rs.next()) {
        String cavity = rs.getString("CAVITY");
        Date correctionDate = rs.getDate("CORRECTION_DATE");

        records.add(new CavityWithLastCorrection(cavity, correctionDate));
      }
    }

    return records;
  }
}
