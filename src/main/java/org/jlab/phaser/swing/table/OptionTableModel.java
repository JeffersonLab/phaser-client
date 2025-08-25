package org.jlab.phaser.swing.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * A table model for holding options (boolean fields).
 *
 * @author ryans
 */
public class OptionTableModel extends AbstractTableModel {

  private final List<String> columnNames = Arrays.asList(new String[] {"", "Option"});
  List<OptionRow> options = new ArrayList<>();

  /**
   * Create a new OptionTableModel with specified header.
   *
   * @param header The header
   */
  public OptionTableModel(String header) {
    columnNames.set(1, header);
  }

  /**
   * Return the list of OptionRows.
   *
   * @return The list of OptionRows
   */
  public LinkedHashSet<OptionRow> getOptions() {
    // We make a copy
    // (bad performance, but we don't worry about outside changes)
    return new LinkedHashSet<>(options);
  }

  /**
   * Replace the list of OptionRows with the specified list.
   *
   * @param options The list of OptionRows
   */
  public void setOptions(LinkedHashSet<OptionRow> options) {
    // We make a copy
    // (bad performance, but we don't worry about outside changes)
    // We also store internally as list, but require a linkedhashset
    // to ensure it at least comes in unique and ordered
    this.options = new ArrayList<>(options);
    fireTableDataChanged();
  }

  /**
   * Add the specified list of OptionRows to the existing list.
   *
   * @param options The list of OptionRows to add
   */
  public void addAll(LinkedHashSet<OptionRow> options) {
    this.options.addAll(options);
    fireTableDataChanged();
  }

  /**
   * Remove all OptionRows from the list and return them.
   *
   * @return The list of OptionRows
   */
  public LinkedHashSet<OptionRow> removeAll() {
    LinkedHashSet<OptionRow> result = new LinkedHashSet<>(options);
    options.clear();
    fireTableDataChanged();
    return result;
  }

  /**
   * Add the OptionRow to the end of the list.
   *
   * @param row The OptionRow to add
   */
  public void add(OptionRow row) {
    options.add(row);
    fireTableDataChanged();
  }

  /**
   * Remove the OptionRow at the specified index.
   *
   * @param index The index
   * @return The OptionRow or null if not found
   */
  public OptionRow remove(int index) {
    OptionRow result = options.remove(index);
    fireTableDataChanged();
    return result;
  }

  /**
   * Return the index of the specified OptionRow.
   *
   * @param row The OptionRow to search for
   * @return The index of the row or -1 if not found
   */
  public int findOptionRowIndex(OptionRow row) {
    return options.indexOf(row);
  }

  /**
   * Returns the column name at the specified index.
   *
   * @param columnIndex The column index
   * @return The column name
   */
  @Override
  public String getColumnName(int columnIndex) {
    return columnNames.get(columnIndex);
  }

  @Override
  public int getRowCount() {
    return options.size();
  }

  @Override
  public int getColumnCount() {
    return columnNames.size();
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return columnIndex == 0 ? Boolean.class : String.class;
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex == 0;
  }

  @Override
  public void setValueAt(Object value, int rowIndex, int columnIndex) {

    // This collection may throw an IndexOutOfBoundsException
    OptionRow row = options.get(rowIndex);

    // This collection may throw an IndexOutOfBoundsException
    columnNames.get(columnIndex);

    if (columnIndex == 0 && value instanceof Boolean) {
      Boolean v = (Boolean) value;
      row.setSelected(v);
    }
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    Object value = null;

    // This collection may throw an IndexOutOfBoundsException
    OptionRow row = options.get(rowIndex);

    // This collection may throw an IndexOutOfBoundsException
    String column = columnNames.get(columnIndex);

    switch (columnIndex) {
      case 0:
        value = row.isSelected();
        break;
      case 1:
        value = row.getName();
        break;
    }

    return value;
  }

  /** A boolean option. */
  public static class OptionRow {
    private String name;
    private boolean selected;

    /**
     * Create a new OptionRow with the specified name and selection state.
     *
     * @param name The option name
     * @param selected true if selected, false otherwise
     */
    public OptionRow(String name, boolean selected) {
      this.name = name;
      this.selected = selected;
    }

    /**
     * Return the option name.
     *
     * @return The name
     */
    public String getName() {
      return name;
    }

    /**
     * Set the option name.
     *
     * @param name The name
     */
    public void setName(String name) {
      this.name = name;
    }

    /**
     * Returns whether the option is selected.
     *
     * @return true if selected, false otherwise
     */
    public boolean isSelected() {
      return selected;
    }

    /**
     * Set whether the option is selected.
     *
     * @param selected true if selected, false otherwise
     */
    public void setSelected(boolean selected) {
      this.selected = selected;
    }
  }
}
