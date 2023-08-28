package org.jlab.phaser.swing.dialog;

import java.awt.Component;
import java.awt.Dimension;
import org.jlab.phaser.swing.util.FrostedGlassPane;
import org.jlab.phaser.swing.table.ResultTableModel;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.jlab.phaser.DatabaseConsole;
import org.jlab.phaser.PhaserServerConsole;
import org.jlab.phaser.model.JobFilter;
import org.jlab.phaser.model.JobPage;
import org.jlab.phaser.model.ResultRecord;
import org.jlab.phaser.model.ResultFilter;
import org.jlab.phaser.model.ResultPage;
import org.jlab.phaser.model.Paginator;
import org.jlab.phaser.swing.PhaserClientFrame;
import org.jlab.phaser.swing.action.ApplyCorrectionsAction;
import org.jlab.phaser.swing.action.ExportElogAction;
import org.jlab.phaser.swing.action.ExportExcelAction;
import org.jlab.phaser.swing.action.JobsAction;
import org.jlab.phaser.swing.action.NextJobsAction;
import org.jlab.phaser.swing.action.NextResultsAction;
import org.jlab.phaser.swing.action.PreviousJobsAction;
import org.jlab.phaser.swing.action.PreviousResultsAction;
import org.jlab.phaser.swing.action.ResultsAction;
import org.jlab.phaser.swing.dialog.chooser.Chooser;
import org.jlab.phaser.swing.dialog.chooser.JobFilterChooser;
import org.jlab.phaser.swing.dialog.chooser.ResultFilterChooser;
import org.jlab.phaser.swing.table.JobTableModel;
import org.jlab.phaser.swing.util.PopupMenuTableHeaderRenderer;

/**
 * Provides a form for users to search the results database and display results.
 *
 * Layout was done using Netbeans Matisse Swing GUI builder.
 *
 * @author ryans
 */
public final class ResultsDialog extends javax.swing.JDialog {

    private static final Logger LOGGER = Logger.getLogger(
            ResultsDialog.class.getName());

    public static final long MAX_RECORDS_PER_PAGE = 500;
    private final FrostedGlassPane frostedPane = new FrostedGlassPane();
    private final WaitDialog waitDialog = new WaitDialog(this);
    private final PhaserClientFrame frame;
    private final ResultTableModel resultTableModel = new ResultTableModel(
            PhaserClientFrame.TIMESTAMP_FORMAT);
    private final JobTableModel jobTableModel = new JobTableModel(
            PhaserClientFrame.TIMESTAMP_FORMAT);
    private long resultsCurrentOffset = 0;
    private long resultsTotalRecords = 0;
    private long jobsCurrentOffset = 0;
    private long jobsTotalRecords = 0;
    private ResultFilter resultFilter;
    private JobFilter jobFilter;

    /**
     * Creates new form ResultsDialog
     *
     * @param frame The parent frame
     */
    public ResultsDialog(final PhaserClientFrame frame) {
        super(frame, false);
        this.frame = frame;
        setGlassPane(frostedPane);
        initComponents();
        initActions();
        initTableFormat();
        initDefaultFilter();
    }

    @Override
    public final void setGlassPane(Component pane) {
        // We make this method final since we call it from the constructor
        super.setGlassPane(pane);
    }
    
    private void initActions() {
        resultsNextButton.setAction(new NextResultsAction(this));
        resultsPreviousButton.setAction(new PreviousResultsAction(this));
        resultsElogButton.setAction(new ExportElogAction(this));
        resultsExcelButton.setAction(new ExportExcelAction(this));
        jobsNextButton.setAction(new NextJobsAction(this));
        jobsPreviousButton.setAction(new PreviousJobsAction(this));
        applyCorrectionsButton.setAction(new ApplyCorrectionsAction(this));
    }

    /**
     * Return the command console.
     * 
     * @return The command console
     */
    public PhaserServerConsole getCommandConsole() {
        return frame.getPhaserServerConsole();
    }

    private void initTableFormat() {
        resultsTable.getTableHeader().setReorderingAllowed(false);
        resultsTable.getTableHeader().setResizingAllowed(false);
        jobsTable.getTableHeader().setReorderingAllowed(false);
        jobsTable.getTableHeader().setResizingAllowed(false);

        TableColumnModel resultsTableColumnModel = resultsTable.getColumnModel();
        TableColumnModel jobsTableColumnModel = jobsTable.getColumnModel();

        DefaultTableCellRenderer rightAlignedRenderer = new DefaultTableCellRenderer();
        rightAlignedRenderer.setHorizontalAlignment(JLabel.RIGHT);

        // Set header height due to multi-line headers
        resultsTable.getTableHeader().setPreferredSize(new Dimension(100, 50));
        jobsTable.getTableHeader().setPreferredSize(new Dimension(100, 50));

        // Set Select All/None Header
        JPopupMenu menu = new JPopupMenu();
        JMenuItem selectAll = new JMenuItem("Select All");
        JMenuItem selectNone = new JMenuItem("Select None");
        selectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((ResultTableModel) resultsTable.getModel()).checkAll();
            }
        });
        selectNone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((ResultTableModel) resultsTable.getModel()).checkNone();
            }
        });
        menu.add(selectAll);
        menu.add(selectNone);
        resultsTableColumnModel.getColumn(0).setHeaderRenderer(
                PopupMenuTableHeaderRenderer.create(menu));

        // Right align results table number columns
        resultsTableColumnModel.getColumn(2).setCellRenderer(rightAlignedRenderer); // Phase Error        
        resultsTableColumnModel.getColumn(4).setCellRenderer(rightAlignedRenderer); // Phase  
        resultsTableColumnModel.getColumn(5).setCellRenderer(rightAlignedRenderer); // Start Date 
        resultsTableColumnModel.getColumn(6).setCellRenderer(rightAlignedRenderer); // Duration (Seconds)        
        resultsTableColumnModel.getColumn(7).setCellRenderer(rightAlignedRenderer); // Correction Date

        // Right align jobs table number columns
        jobsTableColumnModel.getColumn(0).setCellRenderer(rightAlignedRenderer); // Job #
        jobsTableColumnModel.getColumn(2).setCellRenderer(rightAlignedRenderer); // Duration (Minutes)
        jobsTableColumnModel.getColumn(3).setCellRenderer(rightAlignedRenderer); // Max Phase Error
        jobsTableColumnModel.getColumn(4).setCellRenderer(rightAlignedRenderer); // Max Momentum Error
        jobsTableColumnModel.getColumn(5).setCellRenderer(rightAlignedRenderer); // Kick Samples
        jobsTableColumnModel.getColumn(6).setCellRenderer(rightAlignedRenderer); // # Results

        // Set results table column width
        resultsTableColumnModel.getColumn(0).setPreferredWidth(50); // Checkbox
        resultsTableColumnModel.getColumn(1).setPreferredWidth(75); // Cavity
        resultsTableColumnModel.getColumn(2).setPreferredWidth(100); // Phase Error       
        resultsTableColumnModel.getColumn(3).setPreferredWidth(100); // Outcome        
        resultsTableColumnModel.getColumn(4).setPreferredWidth(75); // Phase        
        resultsTableColumnModel.getColumn(5).setPreferredWidth(150); // Start Date
        resultsTableColumnModel.getColumn(6).setPreferredWidth(75); // Duration (Seconds)
        resultsTableColumnModel.getColumn(7).setPreferredWidth(150); // Correction Date

        resultsTableColumnModel.getColumn(0).setMinWidth(50); // Checkbox
        resultsTableColumnModel.getColumn(1).setMinWidth(75); // Cavity
        resultsTableColumnModel.getColumn(2).setMinWidth(100); // Phase Error 
        resultsTableColumnModel.getColumn(3).setMinWidth(100); // Outcome
        resultsTableColumnModel.getColumn(4).setMinWidth(75); // Phase        
        resultsTableColumnModel.getColumn(5).setMinWidth(150); // Start Date
        resultsTableColumnModel.getColumn(6).setMinWidth(75); // Duration (Seconds)
        resultsTableColumnModel.getColumn(7).setMinWidth(150); // Correction Date        

        resultsTableColumnModel.getColumn(0).setMaxWidth(50); // Checkbox
        resultsTableColumnModel.getColumn(1).setMaxWidth(75); // Cavity
        resultsTableColumnModel.getColumn(2).setMaxWidth(100); // Phase Error 
        resultsTableColumnModel.getColumn(3).setMaxWidth(100); // Outcome        
        resultsTableColumnModel.getColumn(4).setMaxWidth(75); // Phase       
        resultsTableColumnModel.getColumn(5).setMaxWidth(150); // Start Date
        resultsTableColumnModel.getColumn(6).setMaxWidth(75); // Duration (Seconds)
        resultsTableColumnModel.getColumn(7).setMaxWidth(150); // Correction Date

        // Set jobs table column width
        jobsTableColumnModel.getColumn(0).setPreferredWidth(75); // Job #
        jobsTableColumnModel.getColumn(1).setPreferredWidth(150); // Start Date
        jobsTableColumnModel.getColumn(2).setPreferredWidth(75); // Duration (Minutes)
        jobsTableColumnModel.getColumn(3).setPreferredWidth(100); // Max Phase Error 
        jobsTableColumnModel.getColumn(4).setPreferredWidth(125); // Max Momentum Error
        jobsTableColumnModel.getColumn(5).setPreferredWidth(75); // Kick Samples

        jobsTableColumnModel.getColumn(0).setMinWidth(75); // Job #
        jobsTableColumnModel.getColumn(1).setMinWidth(150); // Start Date
        jobsTableColumnModel.getColumn(2).setMinWidth(75); // Duration (Minutes)
        jobsTableColumnModel.getColumn(3).setMinWidth(100); // Max Phase Error 
        jobsTableColumnModel.getColumn(4).setMinWidth(125); // Max Momentum Error
        jobsTableColumnModel.getColumn(5).setMinWidth(75); // Kick Samples          

        jobsTableColumnModel.getColumn(0).setMaxWidth(75); // Job #
        jobsTableColumnModel.getColumn(1).setMaxWidth(150); // Start Date
        jobsTableColumnModel.getColumn(2).setMaxWidth(75); // Duration (Minutes)
        jobsTableColumnModel.getColumn(3).setMaxWidth(100); // Max Phase Error 
        jobsTableColumnModel.getColumn(4).setMaxWidth(125); // Max Momentum Error
        jobsTableColumnModel.getColumn(5).setMaxWidth(75); // Kick Samples          
    }

    private void initDefaultFilter() {
        resultFilter = new ResultFilter(null, null, null, null, null, null);
        jobFilter = new JobFilter(null, null, null);
    }

    public void resetForm() {

        resultTableModel.setResults(new ArrayList<ResultRecord>());
        resultsCurrentOffset = 0;
        resultsTotalRecords = 0;
        Paginator paginator = new Paginator(resultsTotalRecords,
                resultsCurrentOffset, MAX_RECORDS_PER_PAGE);
        updateResultsStatusLabel(paginator, getResultFilter());
        updateResultsPaginationButtons(paginator);
        jobsCurrentOffset = 0;
        jobsTotalRecords = 0;
        updateJobsStatusLabel(paginator, getJobFilter());
        updateJobsPaginationButtons(paginator);

        resultsTabPane.setSelectedIndex(0);
    }

    public void setMostRecentJobId(Long jobId) {
        if (jobId != null) {
            resultFilter = new ResultFilter(jobId, null, null, null, null, null);
            //jobFilter = new JobFilter(jobId, null, null);
        }
    }

    public ResultFilter getResultFilter() {
        return resultFilter;
    }

    public JobFilter getJobFilter() {
        return jobFilter;
    }

    public Paginator getResultsPaginator() {
        return new Paginator(resultsTotalRecords, resultsCurrentOffset,
                MAX_RECORDS_PER_PAGE);
    }

    public Paginator getJobsPaginator() {
        return new Paginator(jobsTotalRecords, jobsCurrentOffset,
                MAX_RECORDS_PER_PAGE);
    }

    private void updateResultsStatusLabel(Paginator paginator, ResultFilter filter) {
        String where = filter.toHumanWhereClause(PhaserClientFrame.TIMESTAMP_FORMAT);
        String count;

        if (paginator.getTotalRecords() <= paginator.getMaxPerPage()) {
            count = "{" + paginator.getTotalRecords() + "}";
        } else {
            count = "{" + paginator.getStartNumber() + " - "
                    + paginator.getEndNumber() + " of " + paginator.getTotalRecords() + "}";
        }

        resultsStatusLabel.setText(
                "<html>Results " + where + count
                + " </html>");
    }

    private void updateJobsStatusLabel(Paginator paginator, JobFilter filter) {
        String where = filter.toHumanWhereClause(PhaserClientFrame.TIMESTAMP_FORMAT);
        String count;

        if (paginator.getTotalRecords() <= paginator.getMaxPerPage()) {
            count = "{" + paginator.getTotalRecords() + "}";
        } else {
            count = "{" + paginator.getStartNumber() + " - "
                    + paginator.getEndNumber() + " of " + paginator.getTotalRecords() + "}";
        }

        jobsStatusLabel.setText(
                "<html>Jobs " + where + count
                + " </html>");
    }

    private void updateResultsPaginationButtons(Paginator paginator) {

        if (paginator.getTotalRecords() <= paginator.getMaxPerPage()) {
            resultsNextButton.setVisible(false);
            resultsPreviousButton.setVisible(false);
        } else {
            resultsNextButton.setVisible(true);
            resultsPreviousButton.setVisible(true);

            if (paginator.isNext()) {
                resultsNextButton.setEnabled(true);
            } else {
                resultsNextButton.setEnabled(false);
            }

            if (paginator.isPrevious()) {
                resultsPreviousButton.setEnabled(true);
            } else {
                resultsPreviousButton.setEnabled(false);
            }
        }
    }

    private void updateJobsPaginationButtons(Paginator paginator) {

        if (paginator.getTotalRecords() <= paginator.getMaxPerPage()) {
            jobsNextButton.setVisible(false);
            jobsPreviousButton.setVisible(false);
        } else {
            jobsNextButton.setVisible(true);
            jobsPreviousButton.setVisible(true);

            if (paginator.isNext()) {
                jobsNextButton.setEnabled(true);
            } else {
                jobsNextButton.setEnabled(false);
            }

            if (paginator.isPrevious()) {
                jobsPreviousButton.setEnabled(true);
            } else {
                jobsPreviousButton.setEnabled(false);
            }
        }
    }

    public void setResultPage(ResultPage page) {
        resultTableModel.setResults(page.getRecords());
        resultsCurrentOffset = page.getPaginator().getOffset();
        resultsTotalRecords = page.getPaginator().getTotalRecords();
        updateResultsStatusLabel(page.getPaginator(), page.getFilter());
        updateResultsPaginationButtons(page.getPaginator());
    }

    public void setJobPage(JobPage page) {
        jobTableModel.setJobs(page.getRecords());
        jobsCurrentOffset = page.getPaginator().getOffset();
        jobsTotalRecords = page.getPaginator().getTotalRecords();
        updateJobsStatusLabel(page.getPaginator(), page.getFilter());
        updateJobsPaginationButtons(page.getPaginator());
    }

    public List<ResultRecord> getCheckedResults() {
        return ((ResultTableModel) resultsTable.getModel()).getCheckedResults();
    }

    public DatabaseConsole getResultsCommandConsole() {
        return frame.getDatabaseConsole();
    }

    public void queueShowModalWait() {
        frostedPane.setVisible(true);

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                waitDialog.pack();
                waitDialog.setLocationRelativeTo(ResultsDialog.this);
                waitDialog.setVisible(true);
            }
        });
    }

    public void hideModalWait() {
        waitDialog.setVisible(false);
        frostedPane.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        resultsTabPane = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        resultsStatusLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultsTable = new javax.swing.JTable();
        resultsPreviousButton = new javax.swing.JButton();
        resultsNextButton = new javax.swing.JButton();
        resultsExcelButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        applyCorrectionsButton = new javax.swing.JButton();
        resultsElogButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jobsStatusLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jobsTable = new javax.swing.JTable();
        jobsPreviousButton = new javax.swing.JButton();
        jobsNextButton = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Phaser Results");
        setMinimumSize(new java.awt.Dimension(1000, 625));
        setPreferredSize(new java.awt.Dimension(1000, 625));

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        resultsStatusLabel.setText("Search Result Info Goes Here");

        resultsTable.setModel(resultTableModel);
        resultsTable.setFocusable(false);
        resultsTable.setRowSelectionAllowed(false);
        resultsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(resultsTable);

        resultsPreviousButton.setText("Previous");
        resultsPreviousButton.setEnabled(false);

        resultsNextButton.setText("Next");
        resultsNextButton.setEnabled(false);

        resultsExcelButton.setText("Excel...");

        jButton1.setText("Filter...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showResultsFilterButtonActionPerformed(evt);
            }
        });

        applyCorrectionsButton.setText("Apply Corrections");

        resultsElogButton.setText("eLog");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 905, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(resultsExcelButton))
                    .addComponent(resultsStatusLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(applyCorrectionsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resultsElogButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resultsPreviousButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resultsNextButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resultsExcelButton)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultsStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resultsPreviousButton)
                    .addComponent(resultsNextButton)
                    .addComponent(applyCorrectionsButton)
                    .addComponent(resultsElogButton))
                .addContainerGap())
        );

        resultsTabPane.addTab("Cavity Results", jPanel2);

        jobsStatusLabel.setText("<html>Search Result Info Goes Here</html>");

        jobsTable.setModel(jobTableModel);
        jobsTable.setFocusable(false);
        jobsTable.setRowSelectionAllowed(false);
        jobsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jobsTable);

        jobsPreviousButton.setText("Previous");
        jobsPreviousButton.setEnabled(false);

        jobsNextButton.setText("Next");
        jobsNextButton.setEnabled(false);

        jButton2.setText("Filter...");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showJobsFilterButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jobsStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 905, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jobsPreviousButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jobsNextButton))
                            .addComponent(jButton2))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jobsStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jobsPreviousButton)
                    .addComponent(jobsNextButton))
                .addContainerGap())
        );

        resultsTabPane.addTab("Job Details", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resultsTabPane)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resultsTabPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(okButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void showResultsFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showResultsFilterButtonActionPerformed
        ResultFilterChooser chooser = new ResultFilterChooser(this);
        chooser.setInitiallySelected(resultFilter);
        int retval = chooser.showChooserDialog();

        if (retval == Chooser.APPROVE_OPTION) {
            resultFilter = chooser.getSelected();
            ResultsAction action = new ResultsAction(this);
            action.actionPerformed(evt);
        }
    }//GEN-LAST:event_showResultsFilterButtonActionPerformed

    private void showJobsFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showJobsFilterButtonActionPerformed
        JobFilterChooser chooser = new JobFilterChooser(this);
        chooser.setInitiallySelected(jobFilter);
        int retval = chooser.showChooserDialog();

        if (retval == Chooser.APPROVE_OPTION) {
            jobFilter = chooser.getSelected();
            JobsAction action = new JobsAction(this);
            action.actionPerformed(evt);
        }
    }//GEN-LAST:event_showJobsFilterButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyCorrectionsButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jobsNextButton;
    private javax.swing.JButton jobsPreviousButton;
    private javax.swing.JLabel jobsStatusLabel;
    private javax.swing.JTable jobsTable;
    private javax.swing.JButton okButton;
    private javax.swing.JButton resultsElogButton;
    private javax.swing.JButton resultsExcelButton;
    private javax.swing.JButton resultsNextButton;
    private javax.swing.JButton resultsPreviousButton;
    private javax.swing.JLabel resultsStatusLabel;
    private javax.swing.JTabbedPane resultsTabPane;
    private javax.swing.JTable resultsTable;
    // End of variables declaration//GEN-END:variables
}
