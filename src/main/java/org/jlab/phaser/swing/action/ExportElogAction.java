package org.jlab.phaser.swing.action;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.jlab.jlog.exception.LogException;
import org.jlab.jlog.exception.LogRuntimeException;
import org.jlab.phaser.PhaserSwingClient;
import org.jlab.phaser.export.ElogResultsService;
import org.jlab.phaser.model.Paginator;
import org.jlab.phaser.model.ResultFilter;
import org.jlab.phaser.model.ResultPage;
import org.jlab.phaser.swing.generated.dialog.ResultsDialog;
import org.jlab.phaser.swing.util.HyperLinkEnabledMessage;
import org.jlab.phaser.swing.worker.MinimumExecutionSwingWorker;

/**
 * Handles the "eLog" button click on the results form. Launches a new worker to export results to
 * the Jefferson Lab electronic logbook.
 *
 * @author ryans
 */
public final class ExportElogAction extends AbstractAction {

  private static final Logger LOGGER = Logger.getLogger(ExportElogAction.class.getName());

  /** The dialog. */
  private final ResultsDialog dialog;

  /** The logbooks. */
  private final String LOGBOOKS = PhaserSwingClient.CLIENT_PROPERTIES.getProperty("elog.logbooks");

  /**
   * Create a new ExportExcelAction.
   *
   * @param dialog The ResultsDialog
   */
  public ExportElogAction(ResultsDialog dialog) {
    this.dialog = dialog;
    putValue(AbstractAction.NAME, "eLog");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    dialog.queueShowModalWait();
    new MinimumExecutionSwingWorker<Long, Void>() {

      @Override
      protected Long doWithMinimumExecution() throws Exception {
        ResultFilter filter = dialog.getResultFilter();
        ResultPage page =
            dialog.getResultsCommandConsole().results(filter, new Paginator(0, 0, Long.MAX_VALUE));

        ElogResultsService service = new ElogResultsService();
        Long logId = service.export(page, LOGBOOKS);

        return logId;
      }

      @Override
      protected void done() {
        try {
          Long lognumber = get(); // See if there were any exceptions

          String url = "https://logbooks.jlab.org/entry/" + lognumber;
          String html = "<html>Log number: <a href=\"" + url + "\">" + lognumber + "</a></html>";

          JOptionPane.showMessageDialog(
              dialog,
              new HyperLinkEnabledMessage(html),
              "Successfully created eLog",
              JOptionPane.INFORMATION_MESSAGE);
        } catch (InterruptedException | ExecutionException ex) {
          String title = "Unable to export to eLog";
          String message = "Unexpected error";
          LOGGER.log(Level.SEVERE, title, ex);

          Throwable cause = ex.getCause();
          if (cause != null
              && (cause instanceof LogException || cause instanceof LogRuntimeException)) {
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
