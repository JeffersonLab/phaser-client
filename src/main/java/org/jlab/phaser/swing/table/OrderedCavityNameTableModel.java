package org.jlab.phaser.swing.table;

/**
 * A table model for holding cavities by name, which are ordered based on
 * insertion.
 *
 * @author ryans
 */
public class OrderedCavityNameTableModel extends CavityNameTableModel {

    public void moveToTop(int rowIndex) {
        String cavity = cavities.remove(rowIndex);
        cavities.add(0, cavity);

        fireTableDataChanged();
    }

    public void moveUp(int rowIndex) {
        if (rowIndex > 0) {
            String cavity = cavities.remove(rowIndex);

            cavities.add(rowIndex - 1, cavity);

            fireTableDataChanged();
        }

    }

    public void moveDown(int rowIndex) {
        if (rowIndex < cavities.size() - 1) {
            String cavity = cavities.remove(rowIndex);

            cavities.add(rowIndex + 1, cavity);

            fireTableDataChanged();
        }
    }

    public void moveToBottom(int rowIndex) {
        String cavity = cavities.remove(rowIndex);
        cavities.add(cavity);

        fireTableDataChanged();
    }
}
