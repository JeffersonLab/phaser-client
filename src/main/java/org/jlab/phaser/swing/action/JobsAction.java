package org.jlab.phaser.swing.action;

import org.jlab.phaser.swing.worker.JobsWorker;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jlab.phaser.model.Paginator;
import org.jlab.phaser.swing.dialog.ResultsDialog;

/**
 * Handles the "Get Jobs" button click on the jobs form. Launches a new
 * JobsWorker to query the Phaser database.
 *
 * @author ryans
 */
public final class JobsAction extends AbstractAction {

    /**
     * The dialog.
     */
    private final ResultsDialog dialog;

    /**
     * Create a new JobsAction.
     * 
     * @param dialog The ResultsDialog
     */
    public JobsAction(ResultsDialog dialog) {
        this.dialog = dialog;
        putValue(AbstractAction.NAME, "Get Jobs");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.queueShowModalWait();
        new JobsWorker(dialog, new Paginator(0, 0,
                ResultsDialog.MAX_RECORDS_PER_PAGE)).execute();
    }
}
