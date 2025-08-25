package org.jlab.phaser.swing.worker;

import java.util.LinkedHashSet;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jlab.phaser.exception.PhaserException;
import org.jlab.phaser.model.CavityWithLastCorrection;
import org.jlab.phaser.swing.generated.PhaserClientFrame;
import org.jlab.phaser.swing.generated.dialog.NewJobDialog;

/**
 * Prepares the New Job dialog by performing the initial database query to
 * obtain the last phase correction date of cavities.
 *
 * @author ryans
 */
public final class InitializeNewJobDialogWorker extends
        MinimumExecutionSwingWorker<LinkedHashSet<CavityWithLastCorrection>, Void> {

    private static final Logger LOGGER = Logger.getLogger(InitializeNewJobDialogWorker.class.getName());

    private final PhaserClientFrame frame;
    private final NewJobDialog dialog;

    /**
     * Create a new InitializeResultsDialogWorker.
     *
     * @param frame The PhaserClientFrame
     * @param dialog The NewJobDialog
     */
    public InitializeNewJobDialogWorker(PhaserClientFrame frame, NewJobDialog dialog) {
        super();
        this.frame = frame;
        this.dialog = dialog;
    }

    @Override
    protected LinkedHashSet<CavityWithLastCorrection> doWithMinimumExecution() throws Exception {
        return frame.getDatabaseConsole().cavitiesWithLastCorrection();
    }

    @Override
    protected void done() {
        boolean loaded = false;
        try {
            LinkedHashSet<CavityWithLastCorrection> cavityCollection = get(); // Get execution result and see if there were any exceptions
            dialog.setCavityWithLastCorrectionCollection(cavityCollection);

            loaded = true;
        } catch (InterruptedException | ExecutionException ex) {
            String title = "Unable to query results";
            String message = "Unexpected error";
            LOGGER.log(Level.SEVERE, title, ex);

            Throwable cause = ex.getCause();
            if (cause != null && cause instanceof PhaserException) {
                message = cause.getMessage();
            }

            JOptionPane.showMessageDialog(frame, message, title,
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            frame.hideModalWait();

            if (loaded) {
                dialog.resetForm();
                dialog.pack();
                dialog.setLocationRelativeTo(frame);
                dialog.setVisible(true);
            }
        }
    }

}
