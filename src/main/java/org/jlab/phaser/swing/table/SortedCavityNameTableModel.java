package org.jlab.phaser.swing.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * A table model for holding cavities by name, which are sorted alphabetically.
 * 
 * Alphabetical order is also s-coordinate order, a.k.a. distance from injector.
 *
 * @author ryans
 */
public class SortedCavityNameTableModel extends CavityNameTableModel {

    @Override
    public void setCavities(LinkedHashSet<String> cavities) {
        // We don't modify the passed-in list, we need to store as List for
        // easy indexing, but we need unique check so we use intermediary Set
        LinkedHashSet<String> tmp = new LinkedHashSet<>(this.cavities);
        tmp.addAll(cavities);
        this.cavities = new ArrayList<>(tmp);
        Collections.sort(this.cavities);
        fireTableDataChanged();
    }

    @Override
    public void addAll(LinkedHashSet<String> cavities) {
        this.cavities.addAll(cavities);
        Collections.sort(this.cavities);
        fireTableDataChanged();
    }

    @Override
    public void add(String cavity) {
        if (!cavities.contains(cavity)) {
            cavities.add(cavity);
            Collections.sort(cavities);
            fireTableDataChanged();
        }
    }
}
