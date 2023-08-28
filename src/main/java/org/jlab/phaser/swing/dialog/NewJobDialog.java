package org.jlab.phaser.swing.dialog;

import java.awt.Component;
import org.jlab.phaser.model.CavityWithLastCorrection;
import org.jlab.phaser.swing.util.FrostedGlassPane;
import org.jlab.phaser.swing.table.CavityWithLastCorrectionTableModel;
import org.jlab.phaser.swing.table.OrderedCavityWithLastCorrectionTableModel;
import java.awt.EventQueue;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.jlab.phaser.PhaserServerConsole;
import org.jlab.phaser.PhaserSwingClient;
import org.jlab.phaser.exception.ValidationException;
import org.jlab.phaser.model.JobSpecification;
import org.jlab.phaser.swing.PhaserClientFrame;
import org.jlab.phaser.swing.action.StartAction;

/**
 * Provides a form for job specification and submission.
 *
 * Layout was done using Netbeans Matisse Swing GUI builder.
 *
 * @author ryans
 */
public final class NewJobDialog extends JDialog {

    private final CavityWithLastCorrectionTableModel availableModel
            = new CavityWithLastCorrectionTableModel();
    private final OrderedCavityWithLastCorrectionTableModel scheduledModel
            = new OrderedCavityWithLastCorrectionTableModel();
    private final PhaserClientFrame frame;
    private final FrostedGlassPane frostedPane = new FrostedGlassPane();
    private final WaitDialog waitDialog = new WaitDialog(this);
    private LinkedHashSet<CavityWithLastCorrection> cavityCollection;

    /**
     * Creates a new job dialog.
     *
     * @param frame The parent frame
     */
    public NewJobDialog(PhaserClientFrame frame) {
        super(frame, true);
        this.frame = frame;
        setGlassPane(frostedPane);
        initComponents();
        initActions();

        initTables();

        continuousCheckbox.setVisible(false);
        correctCheckbox.setVisible(false);
    }

    @Override
    public final void setGlassPane(Component pane) {
        // We make this method final since we call it from the constructor
        super.setGlassPane(pane);
    }

    private void initActions() {
        okButton.setAction(new StartAction(this));
    }

    private void initTables() {
        TableCellRenderer dateCellRenderer = new DefaultTableCellRenderer() {
            SimpleDateFormat formatter = new SimpleDateFormat(PhaserClientFrame.TIMESTAMP_FORMAT);

            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                if (value instanceof Date) {
                    value = formatter.format(value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
            }
        };

        availableTable.getColumnModel().getColumn(1).setCellRenderer(dateCellRenderer);
        scheduledTable.getColumnModel().getColumn(1).setCellRenderer(dateCellRenderer);
    }

    public void resetForm() {
        String defaultMaxPhaseError = PhaserSwingClient.CLIENT_PROPERTIES.getProperty("default.max-phase-error");
        String defaultMaxMomentumError = PhaserSwingClient.CLIENT_PROPERTIES.getProperty("default.max-momentum-error");
        String defaultKickSamples = PhaserSwingClient.CLIENT_PROPERTIES.getProperty("default.kick-samples");

        availableModel.setCavities(cavityCollection);
        scheduledModel.setCavities(new LinkedHashSet<>());
        maxPhaseErrorInput.setText(defaultMaxPhaseError);
        maxMomentumErrorInput.setText(defaultMaxMomentumError);
        kickSamplesInput.setText(defaultKickSamples);
        continuousCheckbox.setSelected(false);
        correctCheckbox.setSelected(false);

        List<SortKey> keys = new ArrayList<>();
        
        keys.add(new SortKey(1, SortOrder.ASCENDING)); // date
        keys.add(new SortKey(0, SortOrder.ASCENDING)); // name
        
        availableTable.getRowSorter().setSortKeys(keys);
    }

    public JobSpecification getJob() throws ValidationException {

        String maxPhaseErrorStr = maxPhaseErrorInput.getText();
        if (maxPhaseErrorStr == null || maxPhaseErrorStr.isEmpty()) {
            throw new ValidationException("Max Phase Error must not be empty");
        }
        float maxPhaseError;

        try {
            maxPhaseError = Float.valueOf(maxPhaseErrorStr);
        } catch (NumberFormatException e) {
            throw new ValidationException("Max Phase Error must be a number");
        }

        String maxMomentumErrorStr = maxMomentumErrorInput.getText();
        if (maxMomentumErrorStr == null || maxMomentumErrorStr.isEmpty()) {
            throw new ValidationException("Max Momentum Error must not be empty");
        }
        float maxMomentumError;

        //System.out.println("dp/p raw: " + maxMomentumErrorStr);        
        try {
            maxMomentumError = Float.valueOf(maxMomentumErrorStr);
        } catch (NumberFormatException e) {
            throw new ValidationException("Max Momentum Error must be a number");
        }

        //System.out.println("dp/p float: " + maxMomentumError);        
        if (maxMomentumError > 1 || maxMomentumError < 0.0000001) {
            throw new ValidationException("Max Momentum Error must be between 1 and 0.000001");
        }

        String kickSamplesStr = kickSamplesInput.getText();
        if (kickSamplesStr == null || kickSamplesStr.isEmpty()) {
            throw new ValidationException("Kick Samples must not be empty");
        }
        int kickSamples = Integer.valueOf(kickSamplesStr);

        boolean continuous = continuousCheckbox.isSelected();
        boolean correct = correctCheckbox.isSelected();
        LinkedHashSet<CavityWithLastCorrection> cavities = scheduledModel.getCavities();

        if (cavities.isEmpty()) {
            throw new ValidationException("No cavities have been selected");
        }

        LinkedHashSet<String> cavityNames = new LinkedHashSet<>();

        for (CavityWithLastCorrection c : cavities) {
            cavityNames.add(c.getName());
        }

        return new JobSpecification(correct, continuous, maxPhaseError, maxMomentumError, kickSamples, cavityNames);
    }

    public PhaserServerConsole getCommandConsole() {
        return frame.getPhaserServerConsole();
    }

    public void queueShowModalWait() {
        frostedPane.setVisible(true);

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                waitDialog.pack();
                waitDialog.setLocationRelativeTo(NewJobDialog.this);
                waitDialog.setVisible(true);
            }
        });
    }

    public void hideModalWait() {
        waitDialog.setVisible(false);
        frostedPane.setVisible(false);
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
        okButton = new javax.swing.JButton();
        cavitiesPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        availableTable = new javax.swing.JTable();
        allRightButton = new javax.swing.JButton();
        rightButton = new javax.swing.JButton();
        leftButton = new javax.swing.JButton();
        allLeftButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        scheduledTable = new javax.swing.JTable();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        bottomButton = new javax.swing.JButton();
        topButton = new javax.swing.JButton();
        batchSelectButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        continuousCheckbox = new javax.swing.JCheckBox();
        correctCheckbox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        maxPhaseErrorInput = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        maxMomentumErrorInput = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        kickSamplesInput = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Phasing Job");
        setMinimumSize(new java.awt.Dimension(800, 600));
        setModal(true);
        setPreferredSize(new java.awt.Dimension(800, 600));

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("Start");

        cavitiesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Cavities"));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Available");

        availableTable.setAutoCreateRowSorter(true);
        availableTable.setModel(availableModel);
        availableTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        availableTable.getTableHeader().setResizingAllowed(false);
        availableTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(availableTable);

        allRightButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/doubleeast.png"))); // NOI18N
        allRightButton.setToolTipText("Move All Right");
        allRightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allRightButtonActionPerformed(evt);
            }
        });

        rightButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/east.png"))); // NOI18N
        rightButton.setToolTipText("Move Selected Right");
        rightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightButtonActionPerformed(evt);
            }
        });

        leftButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/west.png"))); // NOI18N
        leftButton.setToolTipText("Move Selected Left");
        leftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftButtonActionPerformed(evt);
            }
        });

        allLeftButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/doublewest.png"))); // NOI18N
        allLeftButton.setToolTipText("Move All Left");
        allLeftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allLeftButtonActionPerformed(evt);
            }
        });

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Scheduled");

        scheduledTable.setModel(scheduledModel);
        scheduledTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        scheduledTable.getTableHeader().setResizingAllowed(false);
        scheduledTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(scheduledTable);

        upButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/north.png"))); // NOI18N
        upButton.setToolTipText("Move Selected Up One");
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        downButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/south.png"))); // NOI18N
        downButton.setToolTipText("Move Selected Down One");
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        bottomButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/doublesouth.png"))); // NOI18N
        bottomButton.setToolTipText("Move Selected to Bottom");
        bottomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bottomButtonActionPerformed(evt);
            }
        });

        topButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/doublenorth.png"))); // NOI18N
        topButton.setToolTipText("Move Selected to Top");
        topButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                topButtonActionPerformed(evt);
            }
        });

        batchSelectButton.setText("Batch Select...");
        batchSelectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batchSelectButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout cavitiesPanelLayout = new javax.swing.GroupLayout(cavitiesPanel);
        cavitiesPanel.setLayout(cavitiesPanelLayout);
        cavitiesPanelLayout.setHorizontalGroup(
            cavitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cavitiesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cavitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cavitiesPanelLayout.createSequentialGroup()
                        .addGroup(cavitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE))
                        .addGroup(cavitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(cavitiesPanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(cavitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(allLeftButton)
                                    .addGroup(cavitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(leftButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(rightButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(allRightButton, javax.swing.GroupLayout.Alignment.LEADING)))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cavitiesPanelLayout.createSequentialGroup()
                                .addGap(73, 73, 73)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(cavitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(topButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(downButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bottomButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(upButton)))
                    .addComponent(batchSelectButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cavitiesPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jScrollPane1, jScrollPane2});

        cavitiesPanelLayout.setVerticalGroup(
            cavitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cavitiesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cavitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(cavitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cavitiesPanelLayout.createSequentialGroup()
                        .addComponent(topButton)
                        .addGap(18, 18, 18)
                        .addComponent(upButton)
                        .addGap(18, 18, 18)
                        .addComponent(downButton)
                        .addGap(18, 18, 18)
                        .addComponent(bottomButton))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(cavitiesPanelLayout.createSequentialGroup()
                        .addComponent(allRightButton)
                        .addGap(18, 18, 18)
                        .addComponent(rightButton)
                        .addGap(18, 18, 18)
                        .addComponent(leftButton)
                        .addGap(18, 18, 18)
                        .addComponent(allLeftButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(batchSelectButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));

        continuousCheckbox.setText("Continuous");
        continuousCheckbox.setToolTipText("Should the job be executed continuously in a loop or just once");

        correctCheckbox.setText("Incrementally Correct");
        correctCheckbox.setToolTipText("Should phase error correction be applied or simply calculated and documented");

        jLabel1.setText("Max Phase Error:");

        maxPhaseErrorInput.setDocument(new org.jlab.phaser.swing.document.PhaseErrorDocument());
        maxPhaseErrorInput.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setText("degrees");

        jLabel3.setText("Max Momentum Error:");

        maxMomentumErrorInput.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setText("dp/p");

        jLabel5.setText("Kick Samples:");

        kickSamplesInput.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(maxPhaseErrorInput, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                            .addComponent(maxMomentumErrorInput)
                            .addComponent(kickSamplesInput))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)))
                    .addComponent(continuousCheckbox)
                    .addComponent(correctCheckbox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(maxPhaseErrorInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(maxMomentumErrorInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(kickSamplesInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(continuousCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(correctCheckbox)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cavitiesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cavitiesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void allRightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allRightButtonActionPerformed
        // Can't just grab all rows from model - must iterate over them in view-order due to sorting        
        //LinkedHashSet<CavityWithLastCorrection> cavities = availableModel.removeAll();

        LinkedHashSet<CavityWithLastCorrection> selectedCavities = new LinkedHashSet<>();

        RowSorter<? extends TableModel> sorter = availableTable.getRowSorter();

        for (int i = 0; i < sorter.getViewRowCount(); i++) {
            int modelIndex = sorter.convertRowIndexToModel(i);
            CavityWithLastCorrection cavity = (CavityWithLastCorrection) availableModel.getRow(modelIndex);
            selectedCavities.add(cavity);
        }

        availableModel.removeAll(selectedCavities);
        scheduledModel.addAll(selectedCavities);
    }//GEN-LAST:event_allRightButtonActionPerformed

    private void rightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightButtonActionPerformed
        int[] indicies = availableTable.getSelectedRows();
        RowSorter<? extends TableModel> sorter = availableTable.getRowSorter();

        LinkedHashSet<CavityWithLastCorrection> selectedCavities = new LinkedHashSet<>();

        for (int i = 0; i < indicies.length; i++) {
            int modelIndex = sorter.convertRowIndexToModel(indicies[i]);
            CavityWithLastCorrection cavity = (CavityWithLastCorrection) availableModel.getRow(modelIndex);
            selectedCavities.add(cavity);
        }

        availableModel.removeAll(selectedCavities);
        scheduledModel.addAll(selectedCavities);

        for (CavityWithLastCorrection cavity : selectedCavities) {
            int index = scheduledModel.findCavityRowIndex(cavity);
            scheduledTable.getSelectionModel().addSelectionInterval(index, index);
        }
    }//GEN-LAST:event_rightButtonActionPerformed

    private void leftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftButtonActionPerformed
        int[] indicies = scheduledTable.getSelectedRows();
        RowSorter<? extends TableModel> sorter = availableTable.getRowSorter();

        LinkedHashSet<CavityWithLastCorrection> selectedCavities = new LinkedHashSet<>();

        for (int i = 0; i < indicies.length; i++) {
            CavityWithLastCorrection cavity = (CavityWithLastCorrection) scheduledModel.getRow(indicies[i]);
            selectedCavities.add(cavity);
        }

        scheduledModel.removeAll(selectedCavities);
        availableModel.addAll(selectedCavities);

        for (CavityWithLastCorrection cavity : selectedCavities) {
            int modelIndex = availableModel.findCavityRowIndex(cavity);
            int index = sorter.convertRowIndexToView(modelIndex);
            availableTable.getSelectionModel().addSelectionInterval(index, index);
        }
    }//GEN-LAST:event_leftButtonActionPerformed

    private void allLeftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allLeftButtonActionPerformed
        LinkedHashSet<CavityWithLastCorrection> cavities = scheduledModel.removeAll();
        availableModel.addAll(cavities);
    }//GEN-LAST:event_allLeftButtonActionPerformed

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        int[] indicies = scheduledTable.getSelectedRows();

        if (indicies.length > 0 && indicies[0] > 0) {
            for (int i = 0; i < indicies.length; i++) {
                scheduledModel.moveUp(indicies[i]);
            }

            for (int i = 0; i < indicies.length; i++) {
                if (indicies[i] > 0) {
                    scheduledTable.getSelectionModel().addSelectionInterval(indicies[i] - 1, indicies[i] - 1);
                }
            }

            scheduledTable.scrollRectToVisible(scheduledTable.getCellRect(
                    indicies[0] - 1, 0, true));
        }
    }//GEN-LAST:event_upButtonActionPerformed

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        int[] indicies = scheduledTable.getSelectedRows();

        if (indicies.length > 0 && indicies[indicies.length - 1] < scheduledModel.getRowCount() - 1) {
            for (int i = indicies.length - 1; i >= 0; i--) {
                scheduledModel.moveDown(indicies[i]);
            }

            for (int i = indicies.length - 1; i >= 0; i--) {
                if (indicies[i] < (scheduledModel.getRowCount() - 1)) {
                    scheduledTable.getSelectionModel().addSelectionInterval(indicies[i] + 1, indicies[i] + 1);
                }
            }

            scheduledTable.scrollRectToVisible(scheduledTable.getCellRect(
                    indicies[indicies.length - 1] + 1, 0, true));
        }
    }//GEN-LAST:event_downButtonActionPerformed

    private void bottomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bottomButtonActionPerformed
        int[] indicies = scheduledTable.getSelectedRows();

        if (indicies.length > 0 && indicies[indicies.length - 1] < scheduledModel.getRowCount() - 1) {
            for (int i = 0; i < indicies.length; i++) {
                scheduledModel.moveToBottom(indicies[0]);
            }

            scheduledTable.getSelectionModel().setSelectionInterval(
                    (scheduledModel.getRowCount() - 1) - indicies.length + 1,
                    scheduledModel.getRowCount() - 1);
            scheduledTable.scrollRectToVisible(scheduledTable.getCellRect(
                    scheduledModel.getRowCount() - 1, 0, true));
        }
    }//GEN-LAST:event_bottomButtonActionPerformed

    private void topButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topButtonActionPerformed
        int[] indicies = scheduledTable.getSelectedRows();

        if (indicies.length > 0 && indicies[0] > 0) {
            for (int i = indicies.length - 1; i >= 0; i--) {
                scheduledModel.moveToTop(indicies[indicies.length - 1]);
            }

            scheduledTable.getSelectionModel().setSelectionInterval(0,
                    indicies.length - 1);
            scheduledTable.scrollRectToVisible(scheduledTable.getCellRect(0, 0,
                    true));
        }
    }//GEN-LAST:event_topButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void batchSelectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batchSelectButtonActionPerformed
        BatchSelectDialog batchDialog = new BatchSelectDialog(this,
                cavityCollection, availableTable, scheduledTable);
        batchDialog.pack();
        batchDialog.setLocationRelativeTo(this);
        batchDialog.setVisible(true);
    }//GEN-LAST:event_batchSelectButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton allLeftButton;
    private javax.swing.JButton allRightButton;
    private javax.swing.JTable availableTable;
    private javax.swing.JButton batchSelectButton;
    private javax.swing.JButton bottomButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel cavitiesPanel;
    private javax.swing.JCheckBox continuousCheckbox;
    private javax.swing.JCheckBox correctCheckbox;
    private javax.swing.JButton downButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField kickSamplesInput;
    private javax.swing.JButton leftButton;
    private javax.swing.JTextField maxMomentumErrorInput;
    private javax.swing.JTextField maxPhaseErrorInput;
    private javax.swing.JButton okButton;
    private javax.swing.JButton rightButton;
    private javax.swing.JTable scheduledTable;
    private javax.swing.JButton topButton;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables

    public void setCavityWithLastCorrectionCollection(LinkedHashSet<CavityWithLastCorrection> cavityCollection) {
        this.cavityCollection = cavityCollection;
    }
}
