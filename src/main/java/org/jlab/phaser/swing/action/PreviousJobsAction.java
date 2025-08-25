package org.jlab.phaser.swing.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jlab.phaser.model.Paginator;
import org.jlab.phaser.swing.generated.dialog.ResultsDialog;
import org.jlab.phaser.swing.worker.JobsWorker;

/**
 * Handles the "Previous" button click on the jobs form. Launches a new JobsWorker to query the
 * Phaser database.
 *
 * @author ryans
 */
public final class PreviousJobsAction extends AbstractAction {

  /** The dialog. */
  private final ResultsDialog dialog;

  /**
   * Creates a new PreviousJobsAction.
   *
   * @param dialog The ResultsDialog
   */
  public PreviousJobsAction(ResultsDialog dialog) {
    this.dialog = dialog;
    putValue(AbstractAction.NAME, "Previous");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    dialog.queueShowModalWait();
    Paginator paginator = dialog.getJobsPaginator();
    new JobsWorker(
            dialog,
            new Paginator(0, paginator.getPreviousOffset(), ResultsDialog.MAX_RECORDS_PER_PAGE))
        .execute();
  }
}
