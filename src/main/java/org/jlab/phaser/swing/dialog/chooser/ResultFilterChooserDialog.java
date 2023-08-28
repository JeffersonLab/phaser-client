package org.jlab.phaser.swing.dialog.chooser;

import java.awt.Dialog;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.DefaultFormatterFactory;
import org.jlab.phaser.model.PhaserOutcome;
import org.jlab.phaser.model.ResultFilter;
import org.jlab.phaser.swing.CavityCache;
import org.jlab.phaser.swing.PhaserClientFrame;
import org.jlab.phaser.swing.dialog.ResultsDialog;
import org.jlab.phaser.swing.util.ClearableMaskFormatter;

/**
 * The graphical dialog interface for the ResultFilter chooser.
 * 
 * Layout was done using Netbeans Matisse Swing GUI builder.
 * 
 * @author ryans
 */
public class ResultFilterChooserDialog extends ChooserDialog<ResultFilter> {

    private static final Logger LOGGER = Logger.getLogger(
            ResultFilterChooserDialog.class.getName());

    private boolean okPressed = false;

    /**
     * Creates new form ResultFilterChooserDialog
     *
     * @param parent The parent dialog
     * @param modal true if modal, false otherwise
     */
    public ResultFilterChooserDialog(Dialog parent, boolean modal) {
        super(parent);
        initComponents();

        try {
            ClearableMaskFormatter formatter = new ClearableMaskFormatter(
                    "##-U??-#### ##:##");
            resultsMinDateFilter.setFormatterFactory(
                    new DefaultFormatterFactory(formatter));
            formatter = new ClearableMaskFormatter("##-U??-#### ##:##");
            resultsMaxDateFilter.setFormatterFactory(
                    new DefaultFormatterFactory(formatter));
        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE,
                    "Unable to set formatter factory for date filter input field",
                    e);
        }

        clearForm();
    }
    
    /**
     * Clears the ResultFilter form to empty values.
     */
    public final void clearForm() {
        resultsJobIdFilter.setText("");
        resultsMinDateFilter.setValue(null);
        resultsMaxDateFilter.setValue(null);
        resultsCavityFilter.setText("");
        resultsMinPhaseErrorFilter.setText("");
        resultsOutcomeFilter.setSelectedIndex(0);        
    }

    @Override
    public boolean isOkPressed() {
        return okPressed;
    }

    @Override
    public ResultFilter getSelected() {
        Long jobId = null;

        String jobIdStr = resultsJobIdFilter.getText();
        try {
            if (jobIdStr != null && !jobIdStr.isEmpty()) {
                jobId = Long.valueOf(jobIdStr);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Unable to parse job ID filter", e);
        }

        Date minCavityStartDate = null;
        Date maxCavityStartDate = null;
        String cavity;

        SimpleDateFormat formatter = new SimpleDateFormat(PhaserClientFrame.TIMESTAMP_FORMAT);
        String minStr = (String) resultsMinDateFilter.getValue();
        String maxStr = (String) resultsMaxDateFilter.getValue();

        try {
            if (minStr != null && !minStr.isEmpty()) {
                minCavityStartDate = formatter.parse(minStr);
            }
        } catch (ParseException e) {
            LOGGER.log(Level.WARNING,
                    "Unable to parse min cavity start date filter", e);
        }

        try {
            if (maxStr != null && !maxStr.isEmpty()) {
                maxCavityStartDate = formatter.parse(maxStr);
            }
        } catch (ParseException e) {
            LOGGER.log(Level.WARNING,
                    "Unable to parse max cavity start date filter", e);
        }

        cavity = resultsCavityFilter.getText();

        Float minPhaseError = null;

        String minPhaseErrorStr = resultsMinPhaseErrorFilter.getText();

        if (minPhaseErrorStr != null && !minPhaseErrorStr.isEmpty()) {
            try {
                minPhaseError = Float.valueOf(minPhaseErrorStr);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING,
                        "Unable to parse min phase error from filter", e);
            }
        }

        PhaserOutcome outcome = null;

        String outcomeStr = (String) resultsOutcomeFilter.getSelectedItem();
        if (outcomeStr != null && !outcomeStr.trim().isEmpty()) {
            outcome = PhaserOutcome.valueOf(outcomeStr);
        }

        return new ResultFilter(jobId, minCavityStartDate, maxCavityStartDate,
                cavity, minPhaseError, outcome);
    }

    @Override
    public void setInitiallySelected(ResultFilter selected) {
        SimpleDateFormat formatter = new SimpleDateFormat(PhaserClientFrame.TIMESTAMP_FORMAT);
        
        resultsJobIdFilter.setText(selected.getJobId() == null ? null : selected.getJobId().toString());
        resultsMinDateFilter.setValue(selected.getMinCavityStartDate() == null ? null : formatter.format(selected.getMinCavityStartDate()));
        resultsMaxDateFilter.setValue(selected.getMaxCavityStartDate() == null ? null : formatter.format(selected.getMaxCavityStartDate()));
        resultsCavityFilter.setText(selected.getCavityName());
        resultsMinPhaseErrorFilter.setText(selected.getMinPhaseError() == null ? null : selected.getMinPhaseError().toString());
        resultsOutcomeFilter.setSelectedIndex(selected.getOutcome() == null ? 0 : selected.getOutcome().ordinal() + 1);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        resultsJobIdFilter = new javax.swing.JTextField();
        resultsMinDateChooserButton = new javax.swing.JButton();
        resultsMaxDateChooserButton = new javax.swing.JButton();
        resultsCavityFilter = new javax.swing.JTextField();
        resultsCavityChooserButton = new javax.swing.JButton();
        resultsMinDateFilter = new javax.swing.JFormattedTextField();
        resultsMaxDateFilter = new javax.swing.JFormattedTextField();
        resultsMinPhaseErrorFilter = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        resultsOutcomeFilter = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        clearButton = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        selectButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Result Filter Chooser");
        setPreferredSize(new java.awt.Dimension(650, 350));
        setResizable(false);
        setSize(new java.awt.Dimension(650, 350));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setText("Job #:");

        jLabel2.setText("Min Cavity Start Date:");

        jLabel3.setText("Max Cavity Start Date:");

        jLabel4.setText("Cavity:");

        resultsJobIdFilter.setDocument(new org.jlab.phaser.swing.document.JobIdDocument());
        resultsJobIdFilter.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N

        resultsMinDateChooserButton.setText("Choose...");
        resultsMinDateChooserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultsMinDateChooserButtonActionPerformed(evt);
            }
        });

        resultsMaxDateChooserButton.setText("Choose...");
        resultsMaxDateChooserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultsMaxDateChooserButtonActionPerformed(evt);
            }
        });

        resultsCavityFilter.setDocument(new org.jlab.phaser.swing.document.CavityDocument());
        resultsCavityFilter.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N

        resultsCavityChooserButton.setText("Choose...");
        resultsCavityChooserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultsCavityChooserButtonActionPerformed(evt);
            }
        });

        try {
            resultsMinDateFilter.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##-U??-#### ##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        resultsMinDateFilter.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N

        try {
            resultsMaxDateFilter.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##-U??-#### ##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        resultsMaxDateFilter.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N

        resultsMinPhaseErrorFilter.setDocument(new org.jlab.phaser.swing.document.PhaseErrorDocument());
        resultsMinPhaseErrorFilter.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N

        jLabel5.setText("Min Phase Error:");

        resultsOutcomeFilter.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        resultsOutcomeFilter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "MEASURED", "SKIPPED", "BYPASSED", "CORRECTED", "ERROR", "DEFERRED"}));

        jLabel6.setText("Outcome:");

        jLabel7.setText("(DD-MMM-YYYY hh:mm)");

        jLabel8.setText("(DD-MMM-YYYY hh:mm)");

        jLabel14.setText("(use % as wildcard)");

        clearButton.setText("Clear");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearFormActionPerformed(evt);
            }
        });

        jLabel9.setText("(magnitude)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(resultsMinPhaseErrorFilter)
                    .addComponent(resultsJobIdFilter)
                    .addComponent(resultsCavityFilter)
                    .addComponent(resultsMinDateFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(resultsMaxDateFilter)
                    .addComponent(resultsOutcomeFilter, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(clearButton))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(resultsCavityChooserButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(resultsMinDateChooserButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(resultsMaxDateChooserButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8))
                            .addComponent(jLabel9))
                        .addGap(0, 108, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(resultsJobIdFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(clearButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(resultsMinDateChooserButton)
                    .addComponent(resultsMinDateFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(resultsMaxDateChooserButton)
                    .addComponent(resultsMaxDateFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(resultsCavityFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resultsCavityChooserButton)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resultsMinPhaseErrorFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resultsOutcomeFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(40, 40, 40))
        );

        selectButton.setText("Select");
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(selectButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void resultsMinDateChooserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultsMinDateChooserButtonActionPerformed
        TimestampChooser chooser = new TimestampChooser(this, PhaserClientFrame.TIMESTAMP_FORMAT);
        chooser.setInitiallySelected((String) resultsMinDateFilter.getValue());
        int retval = chooser.showChooserDialog();

        if (retval == Chooser.APPROVE_OPTION) {
            String selected = chooser.getSelected();
            resultsMinDateFilter.setValue(selected);
        }
    }//GEN-LAST:event_resultsMinDateChooserButtonActionPerformed

    private void resultsMaxDateChooserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultsMaxDateChooserButtonActionPerformed
        TimestampChooser chooser = new TimestampChooser(this, PhaserClientFrame.TIMESTAMP_FORMAT);
        chooser.setInitiallySelected((String) resultsMaxDateFilter.getValue());
        int retval = chooser.showChooserDialog();

        if (retval == Chooser.APPROVE_OPTION) {
            String selected = chooser.getSelected();
            resultsMaxDateFilter.setValue(selected);
        }
    }//GEN-LAST:event_resultsMaxDateChooserButtonActionPerformed

    private void resultsCavityChooserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultsCavityChooserButtonActionPerformed
        CavityChooser chooser = new CavityChooser(this,
                CavityCache.getCavities());
        chooser.setInitiallySelected(resultsCavityFilter.getText());
        int retval = chooser.showChooserDialog();

        if (retval == Chooser.APPROVE_OPTION) {
            String selected = chooser.getSelected();
            resultsCavityFilter.setText(selected);
        }
    }//GEN-LAST:event_resultsCavityChooserButtonActionPerformed

    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        okPressed = true;
        dispose();
    }//GEN-LAST:event_selectButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void clearFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearFormActionPerformed
        clearForm();
    }//GEN-LAST:event_clearFormActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton resultsCavityChooserButton;
    private javax.swing.JTextField resultsCavityFilter;
    private javax.swing.JTextField resultsJobIdFilter;
    private javax.swing.JButton resultsMaxDateChooserButton;
    private javax.swing.JFormattedTextField resultsMaxDateFilter;
    private javax.swing.JButton resultsMinDateChooserButton;
    private javax.swing.JFormattedTextField resultsMinDateFilter;
    private javax.swing.JTextField resultsMinPhaseErrorFilter;
    private javax.swing.JComboBox resultsOutcomeFilter;
    private javax.swing.JButton selectButton;
    // End of variables declaration//GEN-END:variables
}
