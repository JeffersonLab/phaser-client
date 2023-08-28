package org.jlab.phaser.swing.action;

import java.awt.event.ActionEvent;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.jlab.phaser.exception.CommandException;
import org.jlab.phaser.model.ResultRecord;
import org.jlab.phaser.swing.dialog.ResultsDialog;
import org.jlab.phaser.swing.worker.MinimumExecutionSwingWorker;
import org.jlab.phaser.swing.worker.ResultsWorker;

/**
 * Handles the "Apply Corrections" button click on the results form. Utilizes the server console to
 * send a command to the server.
 *
 * @author ryans
 */
public final class ApplyCorrectionsAction extends AbstractAction {

    private static final Logger LOGGER = Logger.getLogger(ApplyCorrectionsAction.class.getName());

    /**
     * The dialog.
     */
    private final ResultsDialog dialog;

    /**
     * Create a new ApplyCorrectionsAction.
     *
     * @param dialog The ResultsDialog
     */
    public ApplyCorrectionsAction(ResultsDialog dialog) {
        this.dialog = dialog;
        putValue(AbstractAction.NAME, "Apply Corrections");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.queueShowModalWait();
        new MinimumExecutionSwingWorker<Void, Void>() {

            @Override
            protected Void doWithMinimumExecution() throws Exception {
                List<ResultRecord> results = dialog.getCheckedResults();

                List<BigInteger> idList = new ArrayList<>();

                if (results != null) {
                    for (ResultRecord result : results) {
                        idList.add(result.getResultId());
                    }
                }

                dialog.getCommandConsole().applyCorrections(idList.toArray(new BigInteger[0]));
                return null;
            }

            @Override
            protected void done() {
                boolean runningSecondWorker = false;

                try {
                    get(); // See if there were any exceptions

                    ResultsWorker secondWorker = new ResultsWorker(dialog,
                            dialog.getResultsPaginator(), 375L, false);
                    secondWorker.execute();
                    runningSecondWorker = true;
                } catch (InterruptedException | ExecutionException ex) {
                    String title = "Unable to apply phase error corrections";
                    String message = "Unexpected error";
                    LOGGER.log(Level.SEVERE, title, ex);

                    Throwable cause = ex.getCause();
                    if (cause != null && cause instanceof CommandException) {
                        message = cause.getMessage();
                    }

                    JOptionPane.showMessageDialog(dialog, message, title,
                            JOptionPane.ERROR_MESSAGE);
                } finally { // Second worker won't be executed if exception in first
                    if (!runningSecondWorker) {
                        dialog.hideModalWait();
                    }
                }
            }
        }.execute();
    }
}
