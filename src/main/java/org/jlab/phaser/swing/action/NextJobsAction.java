package org.jlab.phaser.swing.action;

import org.jlab.phaser.swing.worker.JobsWorker;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.jlab.phaser.model.Paginator;
import org.jlab.phaser.swing.dialog.ResultsDialog;

/**
 * Handles the "Next" button click on the jobs form.  Launches a new JobsWorker
 * to query the Phaser database.
 * 
 * @author ryans
 */
public final class NextJobsAction extends AbstractAction {

    private final ResultsDialog dialog;

    /**
     * Create a new NextJobsAction.
     * 
     * @param dialog The ResutlsDialog
     */
    public NextJobsAction(ResultsDialog dialog) {
        this.dialog = dialog;
        putValue(AbstractAction.NAME, "Next");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.queueShowModalWait();
        Paginator paginator = dialog.getJobsPaginator();
        new JobsWorker(dialog, new Paginator(0, paginator.getNextOffset(), 
                ResultsDialog.MAX_RECORDS_PER_PAGE)).execute();
    }
}
