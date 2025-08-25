package org.jlab.phaser.swing.table;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.jlab.phaser.model.ResultRecord;

/**
 * A table model for holding Phaser results.
 *
 * @author ryans
 */
public class ResultTableModel extends AbstractTableModel {

  private final String timestampFormat;
  private List<Boolean> checkboxState = new ArrayList<>();

  private static final List<String> COLUMN_NAMES =
      Arrays.asList(
          new String[] {
            "<html></html>",
            "Cavity",
            "<html><center>Phase Error<br/>(Degrees)</center></html>",
            "Outcome",
            "<html><center>Phase<br/>(Degrees)</center></html>",
            "<html><center>Measurement<br/>Start Date</center></html>",
            "<html><center>Duration<br/>(Seconds)</center></html>",
            "Correction Date",
            "<html><center>Correction<br/>Error Reason</center></html>"
          });
  List<ResultRecord> results = new ArrayList<>();

  /**
   * Create a new ResultTableModel with the specified timestamp format.
   *
   * @param timestampFormat The timetamp format to use for formatting date columns
   */
  public ResultTableModel(String timestampFormat) {
    this.timestampFormat = timestampFormat;
  }

  @Override
  public Class getColumnClass(int col) {
    return col == 0 ? Boolean.class : String.class;
  }

  @Override
  public boolean isCellEditable(int row, int col) {
    return col == 0;
  }

  @Override
  public void setValueAt(Object value, int row, int col) {
    /*System.out.println("Setting value at " + row + "," + col
    + " to " + value
    + " (an instance of "
    + value.getClass() + ")");*/

    if (col == 0) {
      Boolean bool = (Boolean) value;
      checkboxState.set(row, bool);
      fireTableCellUpdated(row, col);
    }
  }

  /**
   * Return the list of result records.
   *
   * @return The ResultRecord list
   */
  public List<ResultRecord> getResults() {
    // We make a copy
    // (bad performance, but we don't worry about outside changes)
    return new ArrayList<>(results);
  }

  /**
   * Set the list of result records.
   *
   * @param results The ResultRecord list
   */
  public void setResults(List<ResultRecord> results) {
    // We make a copy
    // (bad performance, but we don't worry about outside changes)
    this.results = new ArrayList<>(results);
    checkboxState = new ArrayList<>(Collections.nCopies(results.size(), false));
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
    return results.size();
  }

  @Override
  public int getColumnCount() {
    return COLUMN_NAMES.size();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    Object value = null;

    // This collection may throw an IndexOutOfBoundsException
    ResultRecord result = results.get(rowIndex);

    // This collection may throw an IndexOutOfBoundsException
    String column = COLUMN_NAMES.get(columnIndex);

    switch (column) {
      case "<html></html>":
        value = checkboxState.get(rowIndex);
        break;
      case "<html><center>Measurement<br/>Start Date</center></html>":
        SimpleDateFormat dateFormatter = new SimpleDateFormat(timestampFormat);
        Date startDate = result.getStartDate();
        value = dateFormatter.format(startDate);
        break;
      case "<html><center>Duration<br/>(Seconds)</center></html>":
        DecimalFormat durationFormatter = new DecimalFormat("#,###,##0");
        long start = result.getStartDate().getTime();
        long end = result.getEndDate().getTime();
        value = durationFormatter.format((end - start) / 1000.0);
        break;
      case "Cavity":
        value = result.getCavity();
        break;
      case "<html><center>Phase<br/>(Degrees)</center></html>":
        Float phase = result.getPhase();
        if (phase != null) {
          DecimalFormat phaseFormatter = new DecimalFormat("##0.00");
          value = phaseFormatter.format(phase);
        }
        break;
      case "<html><center>Phase Error<br/>(Degrees)</center></html>":
        Float phaseError = result.getPhaseError();
        if (phaseError != null) {
          DecimalFormat errorFormatter = new DecimalFormat("##0.00");
          value = errorFormatter.format(phaseError);
        }
        break;
      case "Outcome":
        value = result.getOutcome().name();
        break;
      case "Correction Date":
        Date correctedDate = result.getCorrectionDate();
        if (correctedDate != null) {
          SimpleDateFormat correctedFormatter = new SimpleDateFormat(timestampFormat);
          value = correctedFormatter.format(correctedDate);
        }
        break;
      case "<html><center>Correction<br/>Error Reason</center></html>":
        value = result.getCorrectionErrorReason();
        break;
    }

    return value;
  }

  /**
   * Return the list of ResultRecords which correspond to checked rows.
   *
   * @return The list of checked ResultRecords
   */
  public List<ResultRecord> getCheckedResults() {
    List<ResultRecord> checkedResults = new ArrayList<>();

    if (results != null && checkboxState != null && results.size() == checkboxState.size()) {
      for (int i = 0; i < checkboxState.size(); i++) {
        Boolean checked = checkboxState.get(i);

        if (checked != null && checked) {
          ResultRecord record = results.get(i);
          checkedResults.add(record);
        }
      }
    }

    return checkedResults;
  }

  /** Check all rows of the ResultRecord table. */
  public void checkAll() {
    if (results != null && checkboxState != null && results.size() == checkboxState.size()) {
      for (int i = 0; i < checkboxState.size(); i++) {
        checkboxState.set(i, true);
      }
      fireTableDataChanged();
    }
  }

  /** Un-check all rows of the ResultRecord table */
  public void checkNone() {
    if (results != null && checkboxState != null && results.size() == checkboxState.size()) {
      for (int i = 0; i < checkboxState.size(); i++) {
        checkboxState.set(i, false);
      }
      fireTableDataChanged();
    }
  }
}
