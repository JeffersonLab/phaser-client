package org.jlab.phaser.swing.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * A table model for holding cavities by name only.
 *
 * @author ryans
 */
public abstract class CavityNameTableModel extends AbstractTableModel {

  /** Column names */
  protected static final List<String> COLUMN_NAMES = Arrays.asList(new String[] {"Cavities"});

  List<String> cavities = new ArrayList<>();

  /**
   * Return the list of cavities.
   *
   * @return The list of cavities
   */
  public LinkedHashSet<String> getCavities() {
    // We make a copy
    // (bad performance, but we don't worry about outside changes)
    return new LinkedHashSet<>(cavities);
  }

  /**
   * Set the list of cavities.
   *
   * @param cavities The list of cavities
   */
  public void setCavities(LinkedHashSet<String> cavities) {
    // We make a copy
    // (bad performance, but we don't worry about outside changes)
    // We also store internally as list, but require a linkedhashset
    // to ensure it at least comes in unique and ordered
    this.cavities = new ArrayList<>(cavities);
    fireTableDataChanged();
  }

  /**
   * Add all cavities in the supplied list to the current list.
   *
   * @param cavities The list of cavities to add
   */
  public void addAll(LinkedHashSet<String> cavities) {
    // We don't modify the passed-in list, we need to store as List for
    // easy indexing, but we need unique check so we use intermediary Set
    LinkedHashSet<String> tmp = new LinkedHashSet<>(this.cavities);
    tmp.addAll(cavities);
    this.cavities = new ArrayList<>(tmp);
    fireTableDataChanged();
  }

  /**
   * Remove all cavities from the current list and return them.
   *
   * @return The list of all cavities
   */
  public LinkedHashSet<String> removeAll() {
    LinkedHashSet<String> result = new LinkedHashSet<>(cavities);
    cavities.clear();
    fireTableDataChanged();
    return result;
  }

  /**
   * Remove all cavities that match the supplied list and return true if data was changed.
   *
   * @param cavitiesToSelect The cavities to remove
   * @return true if something changed, false otherwise
   */
  public boolean removeAll(LinkedHashSet<String> cavitiesToSelect) {
    boolean changed = cavities.removeAll(cavitiesToSelect);

    if (changed) {
      fireTableDataChanged();
    }

    return changed;
  }

  /**
   * Add the supplied cavity to the list.
   *
   * @param cavity The cavity to add to the list
   */
  public void add(String cavity) {
    if (!cavities.contains(cavity)) {
      cavities.add(cavity);
      fireTableDataChanged();
    }
  }

  /**
   * Remove the cavity at the specified index and return it.
   *
   * @param index The index of the cavity to remove
   * @return The cavity (name) that was removed
   */
  public String remove(int index) {
    String result = cavities.remove(index);
    fireTableDataChanged();
    return result;
  }

  /**
   * Return the row index that corresponds to the cavity with the specified name.
   *
   * @param cavity The cavity to search for
   * @return The index at which the cavity was found, or -1 if not found.
   */
  public int findCavityRowIndex(String cavity) {
    return cavities.indexOf(cavity);
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
    return cavities.size();
  }

  @Override
  public int getColumnCount() {
    return COLUMN_NAMES.size();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (columnIndex != 0) {
      throw new IndexOutOfBoundsException("Column index must be 0");
    }

    // This collection may throw an IndexOutOfBoundsException
    return cavities.get(rowIndex);
  }
}
