package org.jlab.phaser.swing.generated.dialog;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import org.jlab.phaser.model.CavityWithLastCorrection;
import org.jlab.phaser.swing.table.CavityWithLastCorrectionTableModel;
import org.jlab.phaser.swing.table.OptionTableModel;
import org.jlab.phaser.swing.table.OptionTableModel.OptionRow;
import org.jlab.phaser.swing.util.CavityWithLastCorrectionComparator;

/**
 * Provides a form for users to select a group of cavities at once via zones and
 * areas.
 *
 * Layout was done using Netbeans Matisse Swing GUI builder.
 *
 * @author ryans
 */
public final class BatchSelectDialog extends javax.swing.JDialog {

    private final LinkedHashSet<CavityWithLastCorrection> cavities;
    private final OptionTableModel areaTableModel
            = new OptionTableModel("Areas");
    private final OptionTableModel zoneTableModel
            = new OptionTableModel("Zones");
    private final JTable availableTable;
    private final JTable scheduledTable;
    private final CavityWithLastCorrectionTableModel availableModel;
    private final CavityWithLastCorrectionTableModel scheduledModel;

    /**
     * Creates new form QuickSelectDialog
     *
     * @param parent The parent dialog
     * @param cavities The list of cavities
     * @param availableTable The available cavities table
     * @param scheduledTable The scheduled cavities table
     */
    public BatchSelectDialog(Dialog parent, LinkedHashSet<CavityWithLastCorrection> cavities, JTable availableTable, JTable scheduledTable) {
        super(parent, true);
        this.cavities = cavities;
        this.availableTable = availableTable;
        this.scheduledTable = scheduledTable;
        this.availableModel = (CavityWithLastCorrectionTableModel)availableTable.getModel();
        this.scheduledModel = (CavityWithLastCorrectionTableModel)scheduledTable.getModel();
        initComponents();
        initAreas();
        initZones();
    }

    private void initAreas() {
        LinkedHashSet<OptionRow> options = new LinkedHashSet<>();

        options.add(new OptionRow("0 - Injector", false));
        options.add(new OptionRow("1 - North Linac", false));
        options.add(new OptionRow("2 - South Linac", false));

        areaTableModel.setOptions(options);

        areaTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        areaTable.getColumnModel().getColumn(0).setMaxWidth(50);
    }

    private void initZones() {
        LinkedHashSet<String> zones = new LinkedHashSet<>();

        for (CavityWithLastCorrection c : cavities) {
            String name = c.getName();
            String zone = name.split("-")[0];
            zones.add(zone); // duplicates are automatically ignored
        }

        LinkedHashSet<OptionRow> options = new LinkedHashSet<>();

        for (String zone : zones) {
            options.add(new OptionRow(zone, false));
        }

        zoneTableModel.setOptions(options);

        zoneTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        zoneTable.getColumnModel().getColumn(0).setMaxWidth(50);
    }

    private void doSelectAreas() {
        LinkedHashSet<OptionRow> options = areaTableModel.getOptions();

        RowSorter<? extends TableModel> sorter = availableTable.getRowSorter();        
        List<? extends SortKey> keys = sorter.getSortKeys();        
        
        CavityWithLastCorrectionComparator comp = new CavityWithLastCorrectionComparator(keys);
        
        for (OptionRow row : options) {
            if (row.isSelected()) {
                char first = row.getName().charAt(0);

                List<CavityWithLastCorrection> cavitiesToSelect = new ArrayList<>();

                for (CavityWithLastCorrection c : cavities) {
                    String name = c.getName();
                    if (name.charAt(0) == first) {
                        cavitiesToSelect.add(c);
                    }
                }

                cavitiesToSelect.sort(comp); // Insertion order matters for scheduledModel so we sort
                
                LinkedHashSet<CavityWithLastCorrection> selectedCavities = new LinkedHashSet<>(cavitiesToSelect);
                
                availableModel.removeAll(selectedCavities);
                scheduledModel.addAll(selectedCavities);
            }
        }
    }

    private void doSelectZones() {
        LinkedHashSet<OptionRow> options = zoneTableModel.getOptions();

        RowSorter<? extends TableModel> sorter = availableTable.getRowSorter();        
        List<? extends SortKey> keys = sorter.getSortKeys();        
        
        CavityWithLastCorrectionComparator comp = new CavityWithLastCorrectionComparator(keys);        
        
        for (OptionRow row : options) {
            if (row.isSelected()) {
                String zone = row.getName();

                List<CavityWithLastCorrection> cavitiesToSelect = new ArrayList<>();

                for (CavityWithLastCorrection c : cavities) {
                    String name = c.getName();
                    if (name.contains(zone)) {
                        cavitiesToSelect.add(c);
                    }
                }

                cavitiesToSelect.sort(comp); // Insertion order matters for scheduledModel so we sort
                
                LinkedHashSet<CavityWithLastCorrection> selectedCavities = new LinkedHashSet<>(cavitiesToSelect);
                
                availableModel.removeAll(selectedCavities);
                scheduledModel.addAll(selectedCavities);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        selectButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        areaTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        zoneTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Batch Select");
        setMinimumSize(new java.awt.Dimension(300, 425));

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        selectButton.setText("Select");
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });

        areaTable.setModel(areaTableModel);
        areaTable.setRowSelectionAllowed(false);
        areaTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(areaTable);

        zoneTable.setModel(zoneTableModel);
        zoneTable.setRowSelectionAllowed(false);
        zoneTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(zoneTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 148, Short.MAX_VALUE)
                        .addComponent(selectButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(selectButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        doSelectAreas();
        doSelectZones();
        dispose();
    }//GEN-LAST:event_selectButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable areaTable;
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton selectButton;
    private javax.swing.JTable zoneTable;
    // End of variables declaration//GEN-END:variables
}
