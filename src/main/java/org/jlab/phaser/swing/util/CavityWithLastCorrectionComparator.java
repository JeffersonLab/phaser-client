package org.jlab.phaser.swing.util;

import java.util.Comparator;
import java.util.List;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import org.jlab.phaser.model.CavityWithLastCorrection;

/**
 * A comparator for programmatically sorting in the same way a JTable RowSorter applied to a table
 * with a CavityWithLastCorrectionTableModel performs.
 *
 * <p>This is useful when moving CavityWithLastCorrection records from the "available" table to the
 * "scheduled" table in the new job dialog via the batch add feature as we must move entities from
 * zones/areas using whatever sort order the user has currently set on the "available" table.
 *
 * <p>CavityWithLastCorrection record sorting is done via cavity name alphabetically (which happens
 * to be s-coordinate order too) and via last phase correction date. Which key is first and which is
 * second and also the direction of each key (ascending or descending) is configurable by the user.
 * The user interacts with the JTable by clicking column headers to toggle primary key and
 * direction. The batch dialog then must be prepared to honor whatever sort order the user has
 * selected in the view, and this comparator takes care of that.
 *
 * @author ryans
 */
public class CavityWithLastCorrectionComparator implements Comparator<CavityWithLastCorrection> {

  private final List<? extends SortKey> keys;

  /**
   * Create a new CavityWithLastCorrectionComparator given a list of Swing sort keys.
   *
   * @param keys The JTable RowSorter SortKeys
   */
  public CavityWithLastCorrectionComparator(List<? extends SortKey> keys) {
    this.keys = keys;
  }

  @Override
  public int compare(CavityWithLastCorrection o1, CavityWithLastCorrection o2) {
    int result = 0;
    boolean nameAsc;
    boolean dateAsc;

    if (keys.size() == 2) {
      if (keys.get(0).getColumn() == 0) { // Sort by name first
        nameAsc = keys.get(0).getSortOrder().equals(SortOrder.ASCENDING);
        dateAsc = keys.get(1).getSortOrder().equals(SortOrder.ASCENDING);

        if (nameAsc) {
          result = o1.getName().compareTo(o2.getName());
        } else {
          result = o2.getName().compareTo(o1.getName());
        }

        if (result == 0) {
          if (o1.getLastCorrection() == null && o2.getLastCorrection() == null) {
            result = 0;
          } else if (o1.getLastCorrection() == null || o2.getLastCorrection() == null) {
            if (o1.getLastCorrection() == null) {
              result = -1;
            } else if (o2.getLastCorrection() == null) {
              result = 1;
            }

            if (!dateAsc) {
              result = result * -1; // Flip sign
            }
          } else {
            if (dateAsc) {
              result = o1.getLastCorrection().compareTo(o2.getLastCorrection());
            } else {
              result = o2.getLastCorrection().compareTo(o1.getLastCorrection());
            }
          }
        }

      } else { // Sort by date first
        nameAsc = keys.get(1).getSortOrder().equals(SortOrder.ASCENDING);
        dateAsc = keys.get(0).getSortOrder().equals(SortOrder.ASCENDING);

        if (o1.getLastCorrection() == null && o2.getLastCorrection() == null) {
          result = 0;
        } else if (o1.getLastCorrection() == null || o2.getLastCorrection() == null) {
          if (o1.getLastCorrection() == null) {
            result = -1;
          } else if (o2.getLastCorrection() == null) {
            result = 1;
          }

          if (!dateAsc) {
            result = result * -1; // Flip sign
          }
        } else {
          if (dateAsc) {
            result = o1.getLastCorrection().compareTo(o2.getLastCorrection());
          } else {
            result = o2.getLastCorrection().compareTo(o1.getLastCorrection());
          }
        }

        if (result == 0) {
          if (nameAsc) {
            result = o1.getName().compareTo(o2.getName());
          } else {
            result = o2.getName().compareTo(o1.getName());
          }
        }
      }
    }

    return result;
  }
}
