package org.jlab.phaser.swing.worker;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jlab.phaser.exception.PhaserException;
import org.jlab.phaser.model.JobFilter;
import org.jlab.phaser.model.JobPage;
import org.jlab.phaser.model.Paginator;
import org.jlab.phaser.swing.generated.dialog.ResultsDialog;

/**
 * Searches for jobs in the Phaser database utilizing a filter and paginator.
 *
 * @author ryans
 */
public final class JobsWorker extends MinimumExecutionSwingWorker<JobPage, Void> {

  private static final Logger LOGGER = Logger.getLogger(JobsWorker.class.getName());

  private final ResultsDialog dialog;
  private final Paginator paginator;
  private final boolean silent;

  /**
   * Create a new JobsWorker with the default minimum execution and responsiveness.
   *
   * @param dialog The ResultsDialog
   * @param paginator The Paginator
   */
  public JobsWorker(ResultsDialog dialog, Paginator paginator) {
    this(dialog, paginator, MinimumExecutionSwingWorker.DEFAULT_MIN_MILLISECONDS, false);
  }

  /**
   * Create a new JobsWorker with the specified dialog, paginator, minimum execution time, and
   * responsiveness.
   *
   * @param dialog The ResultsDialog
   * @param paginator The Paginator
   * @param minimumMilliseconds The minimum execution time in milliseconds
   * @param silent true if errors are silent, false otherwise
   */
  public JobsWorker(
      ResultsDialog dialog, Paginator paginator, long minimumMilliseconds, boolean silent) {
    super(minimumMilliseconds);
    this.dialog = dialog;
    this.paginator = paginator;
    this.silent = silent;
  }

  @Override
  protected JobPage doWithMinimumExecution() throws PhaserException {
    JobFilter filter = dialog.getJobFilter();
    return dialog.getResultsCommandConsole().jobs(filter, paginator);
  }

  @Override
  protected void done() {
    try {
      JobPage page = get(); // See if there were any exceptions
      dialog.setJobPage(page);
    } catch (InterruptedException | ExecutionException ex) {
      String title = "Unable to query jobs";
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
