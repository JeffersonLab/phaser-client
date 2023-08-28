package org.jlab.phaser.swing.table;

import org.jlab.phaser.model.CavityWithLastCorrection;

/**
 * A table model for holding cavities by name and last correction date, which
 * are ordered based on insertion.
 *
 * @author ryans
 */
public class OrderedCavityWithLastCorrectionTableModel extends CavityWithLastCorrectionTableModel {

    public void moveToTop(int rowIndex) {
        CavityWithLastCorrection cavity = cavities.remove(rowIndex);
        cavities.add(0, cavity);

        fireTableDataChanged();
    }

    public void moveUp(int rowIndex) {
        if (rowIndex > 0) {
            CavityWithLastCorrection cavity = cavities.remove(rowIndex);

            cavities.add(rowIndex - 1, cavity);

            fireTableDataChanged();
        }

    }

    public void moveDown(int rowIndex) {
        if (rowIndex < cavities.size() - 1) {
            CavityWithLastCorrection cavity = cavities.remove(rowIndex);

            cavities.add(rowIndex + 1, cavity);

            fireTableDataChanged();
        }
    }

    public void moveToBottom(int rowIndex) {
        CavityWithLastCorrection cavity = cavities.remove(rowIndex);
        cavities.add(cavity);

        fireTableDataChanged();
    }
}
