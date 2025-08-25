package org.jlab.phaser.swing.action;

import org.jlab.phaser.swing.worker.ResultsWorker;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.jlab.phaser.model.Paginator;
import org.jlab.phaser.swing.generated.dialog.ResultsDialog;

/**
 * Handles the "Next" button click on the results form. Launches a new
 * ResultsWorker to query the Phaser database.
 *
 * @author ryans
 */
public final class NextResultsAction extends AbstractAction {

    /**
     * The dialog.
     */
    private final ResultsDialog dialog;

    /**
     * Create a new NextResultsAction.
     * 
     * @param dialog The ResultsDialog
     */
    public NextResultsAction(ResultsDialog dialog) {
        this.dialog = dialog;
        putValue(AbstractAction.NAME, "Next");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.queueShowModalWait();
        Paginator paginator = dialog.getResultsPaginator();
        new ResultsWorker(dialog, new Paginator(0, paginator.getNextOffset(),
                ResultsDialog.MAX_RECORDS_PER_PAGE)).execute();
    }
}
