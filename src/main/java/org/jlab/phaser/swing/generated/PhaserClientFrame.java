package org.jlab.phaser.swing.generated;

import java.awt.Color;
import org.jlab.phaser.swing.generated.dialog.NewJobDialog;
import org.jlab.phaser.swing.generated.dialog.HelpDialog;
import org.jlab.phaser.swing.generated.dialog.ResultsDialog;
import org.jlab.phaser.swing.generated.dialog.WaitDialog;
import org.jlab.phaser.swing.worker.MinimumExecutionSwingWorker;
import org.jlab.phaser.swing.util.ExitListener;
import org.jlab.phaser.swing.util.FrostedGlassPane;
import org.jlab.phaser.swing.table.ProgressCavityTableModel;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.jlab.phaser.PhaserServerConsole;
import org.jlab.phaser.model.JobSpecification;
import org.jlab.phaser.NotificationListener;
import org.jlab.phaser.DatabaseConsole;
import org.jlab.phaser.model.PhaserJobState;
import org.jlab.phaser.swing.worker.InitializeResultsDialogWorker;
import org.jlab.phaser.swing.worker.JobsWorker;
import org.jlab.phaser.swing.action.PauseAction;
import org.jlab.phaser.swing.action.ResumeAction;
import org.jlab.phaser.swing.action.SkipAction;
import org.jlab.phaser.swing.worker.ResultsWorker;
import org.jlab.phaser.swing.action.StopAction;
import org.jlab.phaser.swing.util.LedLight;
import org.jlab.phaser.swing.worker.InitializeNewJobDialogWorker;

/**
 * The main Phaser client GUI window, which allows users to control the phasing process and provides
 * process status.
 *
 * Layout was done using Netbeans Matisse Swing GUI builder.
 *
 * @author ryans
 */
public final class PhaserClientFrame extends JFrame implements NotificationListener {

    private static final Logger LOGGER = Logger.getLogger(
            PhaserClientFrame.class.getName());

    private PhaserJobState state = PhaserJobState.UNKNOWN;

    private final FrostedGlassPane frostedPane = new FrostedGlassPane();
    private final LedLight led = new LedLight();
    private final JLabel stateValue = new JLabel("N/A");

    private final WaitDialog waitDialog = new WaitDialog(this);
    private final NewJobDialog newJobDialog = new NewJobDialog(this);
    private final ResultsDialog resultsDialog = new ResultsDialog(this);
    private final HelpDialog helpDialog = new HelpDialog(this);

    private final PhaserServerConsole serverConsole;
    private final DatabaseConsole databaseConsole;
    private final List<ExitListener> exitListeners = new ArrayList<>();

    public static final String TIMESTAMP_FORMAT = "dd-MMM-yyyy HH:mm";    
    
    /**
     * Create a new PhaserClientFrame with supplied PhaserServerConsole and DatabaseConsole.
     *
     * @param serverConsole The server console
     * @param databaseConsole The database console
     */
    public PhaserClientFrame(PhaserServerConsole serverConsole, DatabaseConsole databaseConsole) {
        this.serverConsole = serverConsole;
        this.databaseConsole = databaseConsole;
        initComponents();
        initActions();
        initState();
    }

    /**
     * Initialize frame state.
     */
    private void initState() {
        setGlassPane(frostedPane);
        statusNotification("Waiting for server status notification.", null);
        setPhaserJobState(PhaserJobState.UNKNOWN);
        ledPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        ledPanel.add(led);
        ledPanel.add(stateValue);
        addExitListener(new ExitListener() {

            @Override
            public void exit() {
                led.off();
            }
        });

        loopCountLabel.setVisible(false);
        loopCountValue.setVisible(false);

        correctLabel.setVisible(false);
        correctValue.setVisible(false);
        continuousLabel.setVisible(false);
        continuousValue.setVisible(false);
    }

    /**
     * Initialize frame actions.
     */
    private void initActions() {
        pauseButton.setAction(new PauseAction(this));
        resumeButton.setAction(new ResumeAction(this));
        stopButton.setAction(new StopAction(this));
        skipButton.setAction(new SkipAction(this));
    }

    /**
     * Set the server version string.
     * 
     * @param version The server version string
     */
    public void setServerVersion(String version) {
        helpDialog.setServerVersion(version);
    }
    
    /**
     * Add an ExitListener that will be notified of an exit event.
     *
     * @param listener The ExitListener
     */
    public void addExitListener(ExitListener listener) {
        exitListeners.add(listener);
    }

    /**
     * Notifies all ExitListeners that an exit is about to occur.
     */
    private void notifyExitListeners() {
        for (ExitListener listener : exitListeners) {
            listener.exit();
        }
    }

    /**
     * Return the PhaserServerConsole.
     *
     * @return The PhaserServerConsole
     */
    public PhaserServerConsole getPhaserServerConsole() {
        return serverConsole;
    }

    /**
     * Return the DatabaseConsole.
     *
     * @return The DatabaseConsole
     */
    public DatabaseConsole getDatabaseConsole() {
        return databaseConsole;
    }

    /**
     * Show the wait dialog at the earliest opportunity.
     */
    public void queueShowModalWait() {
        frostedPane.setVisible(true);

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                waitDialog.pack();
                waitDialog.setLocationRelativeTo(PhaserClientFrame.this);
                waitDialog.setVisible(true);
            }
        });
    }

    /**
     * Hide the wait dialog.
     */
    public void hideModalWait() {
        waitDialog.setVisible(false);
        frostedPane.setVisible(false);
    }

    /**
     * Sets the PhaserJobState.
     *
     * @param state The PhaserJobState
     */
    private void setPhaserJobState(PhaserJobState state) {
        this.state = state;

        switch (state) {
            case IDLE:
                newJobButton.setEnabled(true);
                pauseButton.setEnabled(false);
                resumeButton.setEnabled(false);
                stopButton.setEnabled(false);
                skipButton.setEnabled(false);
                stateValue.setText("IDLE");
                //stateValue.setForeground(null);
                cavityProgressBar.setValue(0);
                led.off();
                break;
            case PAUSED:
                newJobButton.setEnabled(false);
                pauseButton.setEnabled(false);
                resumeButton.setEnabled(true);
                stopButton.setEnabled(true);
                skipButton.setEnabled(false);
                stateValue.setText("PAUSED");
                //stateValue.setForeground(null);
                cavityProgressBar.setValue(0);
                led.flash(500, new Color(255, 200, 0, 255)); // Orange-ish Yellow
                break;
            case WORKING:
                newJobButton.setEnabled(false);
                pauseButton.setEnabled(true);
                resumeButton.setEnabled(false);
                stopButton.setEnabled(true);
                skipButton.setEnabled(false);
                stateValue.setText("WORKING");
                //stateValue.setForeground(new Color(41, 94, 6));
                led.steady(Color.GREEN);
                break;
            case ERROR_RETRY_WAIT:
                newJobButton.setEnabled(false);
                pauseButton.setEnabled(true);
                resumeButton.setEnabled(false);
                stopButton.setEnabled(true);
                skipButton.setEnabled(true);
                stateValue.setText("ERROR");
                //stateValue.setForeground(null);
                //cavityProgressBar.setValue(0);
                led.flash(250, Color.RED);
                break;
            case UNKNOWN:
                newJobButton.setEnabled(false);
                pauseButton.setEnabled(false);
                resumeButton.setEnabled(false);
                stopButton.setEnabled(false);
                skipButton.setEnabled(false);
                stateValue.setText("UNKNOWN");
                //stateValue.setForeground(null);
                cavityProgressBar.setValue(0);
                led.flash(250, Color.RED);
                break;
        }
    }

    /**
     * Sets this frame visible or not.
     *
     * @param visible true if visible, false otherwise.
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        String cavity = cavityProgressBar.getString();
        selectCavityInTable(cavity);
    }

    /**
     * Selects a cavity in the cavities table.
     *
     * @param cavity The cavity to select
     */
    private void selectCavityInTable(String cavity) {
        if (cavity != null && !cavity.isEmpty()) {
            ProgressCavityTableModel tableModel
                    = (ProgressCavityTableModel) cavitiesTable.getModel();
            int rowIndex = tableModel.findCavityRowIndex(cavity);
            tableModel.updateHeading(rowIndex);
            cavitiesTable.getSelectionModel().setSelectionInterval(rowIndex,
                    rowIndex);
            cavitiesTable.scrollRectToVisible(
                    cavitiesTable.getCellRect(rowIndex, 0,
                            true));
        }
    }

    @Override
    public void statusNotification(final String message, final Boolean error) {
        LOGGER.log(Level.FINEST, "statusNotification: {0}, {1}", new Object[]{message, error});
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                statusMessage.setText(message);

                // Check if current state is not idle or pause as those don't allow errors
                if (state != PhaserJobState.IDLE && state != PhaserJobState.PAUSED) {
                    if (error != null && error) {
                        setPhaserJobState(PhaserJobState.ERROR_RETRY_WAIT);
                    } else {
                        setPhaserJobState(PhaserJobState.WORKING);
                    }
                }
            }
        });
    }

    @Override
    public void jobNotification(final JobSpecification job, final Long jobId,
            final Calendar jobStart, final Boolean paused, final Integer loop, final String cavity,
            final Calendar cavityStart, final Integer progress, final String label) {
        LOGGER.log(Level.FINEST, "jobNotification: {0}, {1}",
                new Object[]{jobStart == null ? null : jobStart.getTime(), job});
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                String jobStartStr = "N/A";
                String cavityStartStr = "N/A";
                String jobIdStr = "N/A";

                if (jobId != null) {
                    jobIdStr = String.valueOf(jobId);
                }

                jobNumberValue.setText(jobIdStr);

                if (jobStart != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "MMM dd HH:mm");
                    jobStartStr = formatter.format(jobStart.getTime());
                }
                jobStartValue.setText(jobStartStr);

                if (paused == null) {
                    setPhaserJobState(PhaserJobState.IDLE);
                } else if (paused) {
                    setPhaserJobState(PhaserJobState.PAUSED);
                } else {
                    setPhaserJobState(PhaserJobState.WORKING);
                }

                loopCountValue.setText(loop == null ? "N/A" : loop.toString());

                if (label == null) {
                    cavityProgressBar.setStringPainted(false);
                } else {
                    cavityProgressBar.setString(label);
                    cavityProgressBar.setStringPainted(true);
                }

                if (cavityStart != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "MMM dd HH:mm");
                    cavityStartStr = formatter.format(cavityStart.getTime());
                }
                cavityStartValue.setText(cavityStartStr);

                String maxPhaseError = "N/A";
                String maxMomentumError = "N/A";
                String kickSamples = "N/A";
                String continuous = "N/A";
                String correct = "N/A";
                LinkedHashSet<String> cavities = new LinkedHashSet<>();

                loopCountLabel.setVisible(false);
                loopCountValue.setVisible(false);

                if (job != null) {
                    if (progress != null && progress >= 0 && progress <= 100) {
                        cavityProgressBar.setValue(progress);
                    } else {
                        LOGGER.log(Level.WARNING,
                                "Job change notification contains invalid progress: {0}",
                                progress);
                    }

                    DecimalFormat formatter = new DecimalFormat();
                    maxPhaseError = formatter.format(job.getMaxPhaseError()) + " degrees";
                    maxMomentumError = String.valueOf(job.getMaxMomentumError()) + " dp/p"; // Unformatted
                    kickSamples = String.valueOf(job.getKickSamples());
                    continuous = job.isContinuous() ? "Yes" : "No";
                    correct = job.isCorrect() ? "Yes" : "No";
                    cavities = job.getCavities();

                    if (job.isContinuous()) {
                        loopCountLabel.setVisible(true);
                        loopCountValue.setVisible(true);
                    }
                } else {
                    ((ProgressCavityTableModel) cavitiesTable.getModel()).updateHeading(-1);
                }

                maxPhaseErrorValue.setText(maxPhaseError);
                maxMomentumErrorValue.setText(maxMomentumError);
                kickSamplesValue.setText(kickSamples);
                continuousValue.setText(continuous);
                correctValue.setText(correct);
                ((ProgressCavityTableModel) cavitiesTable.getModel()).setCavities(
                        cavities);
                selectCavityInTable(cavity);
            }
        });
    }

    @Override
    public void pausedNotification(final Boolean paused) {
        LOGGER.log(Level.FINEST, "pausedNotification: {0}", paused);
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (paused == null) {
                    setPhaserJobState(PhaserJobState.IDLE);
                } else if (paused) {
                    setPhaserJobState(PhaserJobState.PAUSED);
                } else {
                    setPhaserJobState(PhaserJobState.WORKING);
                }
            }
        });
    }

    @Override
    public void loopNotification(final Integer loop) {
        LOGGER.log(Level.FINEST, "loopNotification: {0}", loop);
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                loopCountValue.setText(loop == null ? "N/A" : loop.toString());
            }
        });
    }

    @Override
    public void cavityNotification(final Calendar start, final String cavity) {
        LOGGER.log(Level.FINEST, "cavityNotification: {0}, {1}",
                new Object[]{start == null ? null : start.getTime(), cavity});
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                String startStr = "N/A";
                if (start != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "MMM dd HH:mm");
                    startStr = formatter.format(start.getTime());
                }
                cavityStartValue.setText(startStr);

                selectCavityInTable(cavity);

                if (resultsDialog.isVisible()) {
                    new ResultsWorker(resultsDialog,
                            resultsDialog.getResultsPaginator(),
                            MinimumExecutionSwingWorker.DEFAULT_MIN_MILLISECONDS,
                            true).execute();
                    new JobsWorker(resultsDialog,
                            resultsDialog.getJobsPaginator(),
                            MinimumExecutionSwingWorker.DEFAULT_MIN_MILLISECONDS,
                            true).execute();
                }
            }
        });
    }

    @Override
    public void progressNotification(final Integer value, final String label) {
        LOGGER.log(Level.FINEST, "progressNotification: {0}", value);
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (value != null && value >= 0 && value <= 100) {
                    cavityProgressBar.setValue(value);
                } else {
                    LOGGER.log(Level.WARNING,
                            "Progress notification contains invalid progress: {0}",
                            value);
                }
                if (label == null) {
                    cavityProgressBar.setStringPainted(false);
                } else {
                    cavityProgressBar.setString(label);
                    cavityProgressBar.setStringPainted(true);
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        commandPanel = new javax.swing.JPanel();
        pauseButton = new javax.swing.JButton();
        resumeButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        resultsButton = new javax.swing.JButton();
        newJobButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();
        skipButton = new javax.swing.JButton();
        statusPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        statusMessage = new javax.swing.JTextPane();
        progressPanel = new javax.swing.JPanel();
        cavityStartValue = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        loopCountValue = new javax.swing.JLabel();
        loopCountLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cavityProgressBar = new javax.swing.JProgressBar();
        ledPanel = new javax.swing.JPanel();
        configPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        cavitiesTable = new javax.swing.JTable();
        settingsPanel = new javax.swing.JPanel();
        correctValue = new javax.swing.JLabel();
        correctLabel = new javax.swing.JLabel();
        continuousLabel = new javax.swing.JLabel();
        continuousValue = new javax.swing.JLabel();
        maxPhaseErrorValue = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jobStartValue = new javax.swing.JLabel();
        jobNumberValue = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        maxMomentumErrorValue = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        kickSamplesValue = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Phaser");
        setMinimumSize(new java.awt.Dimension(700, 700));
        setPreferredSize(new java.awt.Dimension(700, 700));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        commandPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Commands"));

        pauseButton.setText("Pause");
        pauseButton.setEnabled(false);

        resumeButton.setText("Resume");
        resumeButton.setEnabled(false);

        stopButton.setText("Stop");
        stopButton.setEnabled(false);

        resultsButton.setText("Results...");
        resultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultsButtonActionPerformed(evt);
            }
        });

        newJobButton.setText("New Job...");
        newJobButton.setEnabled(false);
        newJobButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newJobButtonActionPerformed(evt);
            }
        });

        helpButton.setText("Help...");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });

        skipButton.setText("Skip");
        skipButton.setEnabled(false);

        javax.swing.GroupLayout commandPanelLayout = new javax.swing.GroupLayout(commandPanel);
        commandPanel.setLayout(commandPanelLayout);
        commandPanelLayout.setHorizontalGroup(
            commandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(commandPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(newJobButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pauseButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resumeButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stopButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(skipButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(helpButton)
                .addContainerGap(121, Short.MAX_VALUE))
        );
        commandPanelLayout.setVerticalGroup(
            commandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(commandPanelLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(commandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newJobButton)
                    .addComponent(pauseButton)
                    .addComponent(resumeButton)
                    .addComponent(stopButton)
                    .addComponent(resultsButton)
                    .addComponent(helpButton)
                    .addComponent(skipButton))
                .addGap(20, 20, 20))
        );

        statusPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Status"));

        jScrollPane3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        statusMessage.setEditable(false);
        statusMessage.setBorder(null);
        statusMessage.setMinimumSize(new java.awt.Dimension(50, 150));
        statusMessage.setName(""); // NOI18N
        statusMessage.setOpaque(false);
        statusMessage.setPreferredSize(new java.awt.Dimension(50, 150));
        jScrollPane3.setViewportView(statusMessage);

        cavityStartValue.setText("N/A");

        jLabel9.setText("Cavity Start:");

        loopCountValue.setText("N/A");

        loopCountLabel.setText("Loop Count:");

        jLabel4.setText("Cavity Progress:");

        javax.swing.GroupLayout progressPanelLayout = new javax.swing.GroupLayout(progressPanel);
        progressPanel.setLayout(progressPanelLayout);
        progressPanelLayout.setHorizontalGroup(
            progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(progressPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(progressPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(loopCountLabel)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cavityStartValue)
                            .addComponent(loopCountValue))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(progressPanelLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cavityProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        progressPanelLayout.setVerticalGroup(
            progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(progressPanelLayout.createSequentialGroup()
                .addGroup(progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(progressPanelLayout.createSequentialGroup()
                        .addComponent(cavityProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cavityStartValue)
                            .addComponent(jLabel9)))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loopCountLabel)
                    .addComponent(loopCountValue))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        ledPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        ledPanel.setAlignmentX(0.0F);
        ledPanel.setAlignmentY(0.0F);

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ledPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ledPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        configPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Configuration"));

        jScrollPane1.setMinimumSize(null);

        cavitiesTable.setModel(new org.jlab.phaser.swing.table.ProgressCavityTableModel());
        cavitiesTable.setEnabled(false);
        cavitiesTable.setFocusable(false);
        cavitiesTable.setMaximumSize(null);
        cavitiesTable.setMinimumSize(null);
        cavitiesTable.setName(""); // NOI18N
        cavitiesTable.setPreferredSize(null);
        cavitiesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        cavitiesTable.getTableHeader().setResizingAllowed(false);
        cavitiesTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(cavitiesTable);

        correctValue.setText("N/A");

        correctLabel.setText("Incremental Correct:");

        continuousLabel.setText("Continuous:");

        continuousValue.setText("N/A");

        maxPhaseErrorValue.setText("N/A");

        jLabel15.setText("Max Phase Error:");

        jLabel3.setText("Job Start:");

        jobStartValue.setText("N/A");

        jobNumberValue.setText("N/A");

        jLabel2.setText("Job #:");

        jLabel1.setText("Max Momentum Error:");

        maxMomentumErrorValue.setText("N/A");

        jLabel7.setText("Kick Samples:");

        kickSamplesValue.setText("N/A");

        javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(continuousLabel)
                    .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(correctLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(maxPhaseErrorValue)
                    .addComponent(continuousValue)
                    .addComponent(correctValue)
                    .addComponent(jobStartValue)
                    .addComponent(jobNumberValue)
                    .addComponent(maxMomentumErrorValue)
                    .addComponent(kickSamplesValue))
                .addGap(76, 76, 76))
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jobNumberValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jobStartValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(maxPhaseErrorValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(maxMomentumErrorValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(kickSamplesValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(continuousLabel)
                    .addComponent(continuousValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(correctLabel)
                    .addComponent(correctValue))
                .addGap(31, 31, 31))
        );

        javax.swing.GroupLayout configPanelLayout = new javax.swing.GroupLayout(configPanel);
        configPanel.setLayout(configPanelLayout);
        configPanelLayout.setHorizontalGroup(
            configPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(configPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(configPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(settingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        configPanelLayout.setVerticalGroup(
            configPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, configPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(settingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(commandPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(configPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(configPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(commandPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        notifyExitListeners();
    }//GEN-LAST:event_formWindowClosed

    private void newJobButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newJobButtonActionPerformed
        queueShowModalWait();
        new InitializeNewJobDialogWorker(this, newJobDialog).execute();
    }//GEN-LAST:event_newJobButtonActionPerformed

    private void resultsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultsButtonActionPerformed
        resultsDialog.setVisible(false);
        resultsDialog.resetForm();
        queueShowModalWait();
        new InitializeResultsDialogWorker(this, resultsDialog).execute();
    }//GEN-LAST:event_resultsButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        helpDialog.pack();
        helpDialog.setLocationRelativeTo(this);
        helpDialog.setVisible(true);
    }//GEN-LAST:event_helpButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable cavitiesTable;
    private javax.swing.JProgressBar cavityProgressBar;
    private javax.swing.JLabel cavityStartValue;
    private javax.swing.JPanel commandPanel;
    private javax.swing.JPanel configPanel;
    private javax.swing.JLabel continuousLabel;
    private javax.swing.JLabel continuousValue;
    private javax.swing.JLabel correctLabel;
    private javax.swing.JLabel correctValue;
    private javax.swing.JButton helpButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel jobNumberValue;
    private javax.swing.JLabel jobStartValue;
    private javax.swing.JLabel kickSamplesValue;
    private javax.swing.JPanel ledPanel;
    private javax.swing.JLabel loopCountLabel;
    private javax.swing.JLabel loopCountValue;
    private javax.swing.JLabel maxMomentumErrorValue;
    private javax.swing.JLabel maxPhaseErrorValue;
    private javax.swing.JButton newJobButton;
    private javax.swing.JButton pauseButton;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JButton resultsButton;
    private javax.swing.JButton resumeButton;
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JButton skipButton;
    private javax.swing.JTextPane statusMessage;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
}
