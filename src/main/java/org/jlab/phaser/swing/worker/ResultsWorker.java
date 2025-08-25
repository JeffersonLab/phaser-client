package org.jlab.phaser.swing.worker;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jlab.phaser.exception.PhaserException;
import org.jlab.phaser.model.ResultFilter;
import org.jlab.phaser.model.ResultPage;
import org.jlab.phaser.model.Paginator;
import org.jlab.phaser.swing.generated.dialog.ResultsDialog;

/**
 * Searches for results in the Phaser database utilizing a filter and paginator.
 *
 * @author ryans
 */
public final class ResultsWorker extends MinimumExecutionSwingWorker<ResultPage, Void> {

    private static final Logger LOGGER = Logger.getLogger(
            ResultsWorker.class.getName());

    private final ResultsDialog dialog;
    private final Paginator paginator;
    private final boolean silent;

    /**
     * Creates a new ResultsWorker with the default minimum execution time and responsiveness.
     *
     * @param dialog The ResultsDialog
     * @param paginator The Paginator
     */
    public ResultsWorker(ResultsDialog dialog, Paginator paginator) {
        this(dialog, paginator, MinimumExecutionSwingWorker.DEFAULT_MIN_MILLISECONDS, false);
    }

    /**
     * Creates a new ResultsWorker with the specified dialog, paginator, minimum execution time, and
     * responsiveness.
     *
     * @param dialog The ResultsDialog
     * @param paginator The Paginator
     * @param minimumMilliseconds The minimum execution time in milliseconds
     * @param silent true if errors are ignored, false to alert the user
     */
    public ResultsWorker(ResultsDialog dialog, Paginator paginator, long minimumMilliseconds,
            boolean silent) {
        super(minimumMilliseconds);
        this.dialog = dialog;
        this.paginator = paginator;
        this.silent = silent;
    }

    @Override
    protected ResultPage doWithMinimumExecution() throws PhaserException {
        ResultFilter filter = dialog.getResultFilter();
        return dialog.getResultsCommandConsole().results(filter, paginator);
    }

    @Override
    protected void done() {
        try {
            ResultPage page = get(); // See if there were any exceptions
            dialog.setResultPage(page);
        } catch (InterruptedException | ExecutionException ex) {
            String title = "Unable to query phaser results";
            String message = "Unexpected error";
            LOGGER.log(Level.SEVERE, title, ex);

            Throwable cause = ex.getCause();
            if (cause != null && cause instanceof PhaserException) {
                message = cause.getMessage();
            }

            if (!silent) {
                JOptionPane.showMessageDialog(dialog, message, title, JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            dialog.hideModalWait();
        }
    }
}
