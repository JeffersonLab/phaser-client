package org.jlab.phaser.swing.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jlab.phaser.model.Paginator;
import org.jlab.phaser.swing.generated.dialog.ResultsDialog;
import org.jlab.phaser.swing.worker.ResultsWorker;

/**
 * Handles the "Previous" button click on the results form. Launches a new ResultsWorker to query
 * the Phaser database.
 *
 * @author ryans
 */
public final class PreviousResultsAction extends AbstractAction {

  /** The dialog. */
  private final ResultsDialog dialog;

  /**
   * Creates a new PreviousResultsAction.
   *
   * @param dialog The ResultsDialog
   */
  public PreviousResultsAction(ResultsDialog dialog) {
    this.dialog = dialog;
    putValue(AbstractAction.NAME, "Previous");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    dialog.queueShowModalWait();
    Paginator paginator = dialog.getResultsPaginator();
    new ResultsWorker(
            dialog,
            new Paginator(0, paginator.getPreviousOffset(), ResultsDialog.MAX_RECORDS_PER_PAGE))
        .execute();
  }
}
