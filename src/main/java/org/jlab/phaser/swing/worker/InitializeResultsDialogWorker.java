package org.jlab.phaser.swing.worker;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jlab.phaser.exception.PhaserException;
import org.jlab.phaser.model.JobPage;
import org.jlab.phaser.swing.generated.PhaserClientFrame;
import org.jlab.phaser.swing.generated.dialog.ResultsDialog;

/**
 * Prepares the Results dialog by performing the initial database queries and populating the form
 * with initial values.
 *
 * <p>First we query for the JobPage and if successful we next query for the ResultPage. We use the
 * ordered list of jobs to find the most recent and use that in the query for the ResultPage. If
 * there wasn't this dependency we could have run the queries in parallel.
 *
 * @author ryans
 */
public final class InitializeResultsDialogWorker
    extends MinimumExecutionSwingWorker<JobPage, Void> {

  private static final Logger LOGGER =
      Logger.getLogger(InitializeResultsDialogWorker.class.getName());

  private final PhaserClientFrame frame;
  private final ResultsDialog dialog;

  /**
   * Create a new InitializeResultsDialogWorker.
   *
   * @param frame The PhaserClientFrame
   * @param dialog The ResultsDialog
   */
  public InitializeResultsDialogWorker(PhaserClientFrame frame, ResultsDialog dialog) {
    super(375L); // Don't force long wait since we may have two workers run
    this.frame = frame;
    this.dialog = dialog;
  }

  @Override
  protected JobPage doWithMinimumExecution() throws Exception {
    return frame.getDatabaseConsole().jobs(dialog.getJobFilter(), dialog.getJobsPaginator());
  }

  @Override
  protected void done() {
    boolean runningSecondWorker = false;
    try {
      JobPage page = get(); // Get execution result and see if there were any exceptions
      dialog.setJobPage(page);
      Long jobId = null;
      if (!page.getRecords().isEmpty()) {
        jobId = page.getRecords().get(0).getId();
      }
      dialog.setMostRecentJobId(jobId);
      ResultsWorker secondWorker =
          new ResultsWorker(dialog, dialog.getResultsPaginator(), 375L, false);
      /**
       * Use callback function to handle second worker finish because the existing "done" method
       * will try to hide the ResultDialog wait, but we need to hide the PhaserClientFrame wait and
       * also to show the ResultDialog. We could have overridden ResultWorker and redefined the done
       * method, but this callback will work and the existing done method won't do any harm and sets
       * the results for us.
       */
      secondWorker.addPropertyChangeListener(
          new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
              if ("state".equals(evt.getPropertyName())) {
                if (evt.getNewValue() instanceof StateValue) {
                  StateValue state = (StateValue) evt.getNewValue();
                  if (state == StateValue.DONE) {
                    frame.hideModalWait();
                    dialog.pack();
                    dialog.setLocationRelativeTo(frame);
                    dialog.setVisible(true);
                  }
                }
              }
            }
          });

      secondWorker.execute();
      runningSecondWorker = true;
    } catch (InterruptedException | ExecutionException ex) {
      String title = "Unable to query jobs";
      String message = "Unexpected error";
      LOGGER.log(Level.SEVERE, title, ex);

      Throwable cause = ex.getCause();
      if (cause != null && cause instanceof PhaserException) {
        message = cause.getMessage();
      }

      JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    } finally {
      if (!runningSecondWorker) { // Second worker won't be executed if exception in first
        frame.hideModalWait();
      }
    }
  }
}
