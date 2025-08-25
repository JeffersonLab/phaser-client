package org.jlab.phaser.swing.table;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.jlab.phaser.model.JobRecord;

/**
 * A table model for holding jobs.
 *
 * @author ryans
 */
public class JobTableModel extends AbstractTableModel {

  private final String timestampFormat;

  private static final List<String> COLUMN_NAMES =
      Arrays.asList(
          new String[] {
            "Job #",
            "Start Date",
            "<html><center>Duration<br/>(Minutes)</center></html>",
            "<html><center>Max Phase<br/>Error (Degrees)</center></html>",
            "<html><center>Max Momentum<br/>Error (dp/p)</center></html>",
            "<html><center>Kick<br/>Samples</center></html>",
            "# Results"
          });
  List<JobRecord> records = new ArrayList<>();

  /**
   * Create a new JobTableModel.
   *
   * @param timestampFormat The timestamp format
   */
  public JobTableModel(String timestampFormat) {
    this.timestampFormat = timestampFormat;
  }

  /**
   * Return the list of jobs.
   *
   * @return The list of jobs
   */
  public List<JobRecord> getJobs() {
    // We make a copy
    // (bad performance, but we don't worry about outside changes)
    return new ArrayList<>(records);
  }

  /**
   * Set the list of jobs.
   *
   * @param jobs The list of jobs
   */
  public void setJobs(List<JobRecord> jobs) {
    // We make a copy
    // (bad performance, but we don't worry about outside changes)
    this.records = new ArrayList<>(jobs);
    fireTableDataChanged();
  }

  /**
   * Returns the column name at the specified index.
   *
   * @param columnIndex The column index
   * @return The column name
   */
  @Override
  public String getColumnName(int columnIndex) {
    return COLUMN_NAMES.get(columnIndex);
  }

  @Override
  public int getRowCount() {
    return records.size();
  }

  @Override
  public int getColumnCount() {
    return COLUMN_NAMES.size();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    String value = null;

    // This collection may throw an IndexOutOfBoundsException
    JobRecord record = records.get(rowIndex);

    // This collection may throw an IndexOutOfBoundsException
    String column = COLUMN_NAMES.get(columnIndex);

    switch (column) {
      case "Job #":
        value = String.valueOf(record.getId());
        break;
      case "Start Date":
        SimpleDateFormat dateFormatter = new SimpleDateFormat(timestampFormat);
        Date startDate = record.getStartDate();
        value = dateFormatter.format(startDate);
        break;
      case "<html><center>Duration<br/>(Minutes)</center></html>":
        Date start = record.getStartDate();
        Date end = record.getEndDate();
        if (end != null) {
          DecimalFormat durationFormatter = new DecimalFormat("#,###,##0");
          value = durationFormatter.format((end.getTime() - start.getTime()) / 1000.0 / 60.0);
        }
        break;
      case "<html><center>Max Phase<br/>Error (Degrees)</center></html>":
        DecimalFormat phaseErrorFormatter = new DecimalFormat("#0.##");
        float phaseError = record.getMaxPhaseError();
        value = phaseErrorFormatter.format(phaseError);
        break;
      case "<html><center>Max Momentum<br/>Error (dp/p)</center></html>":
        DecimalFormat momentumErrorFormatter = new DecimalFormat("#0.######");
        float momentumError = record.getMaxMomentumError();
        value = momentumErrorFormatter.format(momentumError);
        break;
      case "<html><center>Kick<br/>Samples</center></html>":
        value = String.valueOf(record.getKickSamples());
        break;
      case "Continuous":
        value = record.isContinuous() ? "Yes" : "No";
        break;
      case "Correct":
        value = record.isCorrect() ? "Yes" : "No";
        break;
      case "# Results":
        value = String.valueOf(record.getCount());
        break;
    }

    return value;
  }
}
