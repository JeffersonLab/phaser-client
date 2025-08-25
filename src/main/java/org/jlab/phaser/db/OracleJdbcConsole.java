package org.jlab.phaser.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import org.jlab.phaser.DatabaseConsole;
import org.jlab.phaser.exception.CommandException;
import org.jlab.phaser.model.CavityWithLastCorrection;
import org.jlab.phaser.model.JobFilter;
import org.jlab.phaser.model.JobPage;
import org.jlab.phaser.model.JobRecord;
import org.jlab.phaser.model.Paginator;
import org.jlab.phaser.model.ResultFilter;
import org.jlab.phaser.model.ResultPage;
import org.jlab.phaser.model.ResultRecord;
import org.jlab.phaser.swing.CavityCache;

/**
 * Provides access to the Phaser database.
 *
 * <p>This class delegates the heavy lifting to the OracleService, but provides services of its own
 * including serializing access to the database and wrapping exceptions in CommandExceptions.
 *
 * <p>To serialize access to the database all of the methods are synchronized. This will prevent
 * competing SwingWorker threads from executing database queries concurrently.
 *
 * @author ryans
 */
public class OracleJdbcConsole implements DatabaseConsole {

  private final OracleService service = new OracleService();

  @Override
  public synchronized ResultPage results(ResultFilter filter, Paginator paginator)
      throws CommandException {
    try {
      long count = service.countResults(filter);
      List<ResultRecord> records = service.findResults(filter, paginator);
      return new ResultPage(
          records, new Paginator(count, paginator.getOffset(), paginator.getMaxPerPage()), filter);
    } catch (SQLException e) {
      throw new CommandException("Unable to query phasing results", e);
    }
  }

  @Override
  public synchronized JobPage jobs(JobFilter filter, Paginator paginator) throws CommandException {
    try {
      long count = service.countJobs(filter);
      List<JobRecord> records = service.findJobs(filter, paginator);
      return new JobPage(
          records, new Paginator(count, paginator.getOffset(), paginator.getMaxPerPage()), filter);
    } catch (SQLException e) {
      throw new CommandException("Unable to query jobs", e);
    }
  }

  @Override
  public synchronized LinkedHashSet<CavityWithLastCorrection> cavitiesWithLastCorrection()
      throws CommandException {

    LinkedHashSet<CavityWithLastCorrection> records = new LinkedHashSet<>();

    LinkedHashSet<String> allCavityNames = CavityCache.getCavities();

    List<CavityWithLastCorrection> dbList;

    try {
      dbList = service.findCavitiesWithLastCorrection();
    } catch (SQLException e) {
      throw new CommandException("Unable to query phasing results");
    }

    HashMap<String, CavityWithLastCorrection> dbMap = new HashMap<>();

    for (CavityWithLastCorrection correction : dbList) {
      dbMap.put(correction.getName(), correction);
    }

    for (String cavity : allCavityNames) {
      CavityWithLastCorrection correction = dbMap.get(cavity);

      if (correction == null) {
        correction = new CavityWithLastCorrection(cavity, null);
      }

      records.add(correction);
    }

    return records;
  }
}
