package org.jlab.phaser.swing.action;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.jlab.phaser.exception.PhaserException;
import org.jlab.phaser.model.JobSpecification;
import org.jlab.phaser.swing.generated.dialog.NewJobDialog;
import org.jlab.phaser.swing.worker.MinimumExecutionSwingWorker;

/**
 * Handles the "Start" button click on the new job form. Utilizes the server console to send a
 * command to the server.
 *
 * @author ryans
 */
public final class StartAction extends AbstractAction {

  private static final Logger LOGGER = Logger.getLogger(StartAction.class.getName());

  /** The dialog */
  private final NewJobDialog dialog;

  /**
   * Creates a new StartAction.
   *
   * @param dialog The NewJobDialog
   */
  public StartAction(NewJobDialog dialog) {
    this.dialog = dialog;
    putValue(AbstractAction.NAME, "Start");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    dialog.queueShowModalWait();
    new MinimumExecutionSwingWorker<Void, Void>() {

      @Override
      protected Void doWithMinimumExecution() throws PhaserException {
        JobSpecification job = dialog.getJob();
        dialog.getCommandConsole().start(job);
        return null;
      }

      @Override
      protected void done() {
        try {
          get(); // See if there were any exceptions
          dialog.dispose();
        } catch (InterruptedException | ExecutionException ex) {
          String title = "Unable to start phasing";
          String message = "Unexpected error";
          LOGGER.log(Level.SEVERE, title, ex);

          Throwable cause = ex.getCause();
          if (cause != null && cause instanceof PhaserException) {
            message = cause.getMessage();
          }

          JOptionPane.showMessageDialog(dialog, message, title, JOptionPane.ERROR_MESSAGE);
        } finally {
          dialog.hideModalWait();
        }
      }
    }.execute();
  }
}
