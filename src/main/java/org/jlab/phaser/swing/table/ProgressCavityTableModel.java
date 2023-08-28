package org.jlab.phaser.swing.table;

/**
 * A table model for holding cavities that can also show which cavity is currently selected in the
 * header.
 *
 * @author ryans
 */
public class ProgressCavityTableModel extends OrderedCavityNameTableModel {

    static {
        COLUMN_NAMES.set(0, "<html><center>Cavities</center></html>");
        //COLUMN_NAMES.add("<html><center>Outcome / Phase<br/>Error (Degrees)</center></html>");
    }

    /**
     * Updated the header to indicate the cavity at the specified index is selected.
     * 
     * @param rowIndex The cavity row index
     */
    public void updateHeading(int rowIndex) {
        if (rowIndex > -1) {
            COLUMN_NAMES.set(0, "<html><center>Cavity {" + (rowIndex + 1) + " of " + (cavities
                    == null ? 0 : cavities.size()) + "}</center></html>");
        } else {
            COLUMN_NAMES.set(0, "<html><center>Cavities</center></html>");
        }
        fireTableStructureChanged();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        switch (columnIndex) {
            case 0:
                // This collection may throw an IndexOutOfBoundsException
                value = cavities.get(rowIndex);
                break;
            case 1:
                break;
            default:
                throw new IndexOutOfBoundsException("Column index must be 0 or 1");
        }
        return value;
    }
}
