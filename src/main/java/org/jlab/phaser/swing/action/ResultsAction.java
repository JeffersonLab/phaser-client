package org.jlab.phaser.swing.action;

import org.jlab.phaser.swing.worker.ResultsWorker;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jlab.phaser.model.Paginator;
import org.jlab.phaser.swing.dialog.ResultsDialog;

/**
 * Handles the "Get Results" button click on the results form. Launches a new ResultsWorker to query
 * the Phaser database.
 *
 * @author ryans
 */
public final class ResultsAction extends AbstractAction {

    /**
     * The dialog.
     */
    private final ResultsDialog dialog;

    /**
     * Creates a new ResultsAction.
     *
     * @param dialog The ResultsDialog
     */
    public ResultsAction(ResultsDialog dialog) {
        this.dialog = dialog;
        putValue(AbstractAction.NAME, "Get Results");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.queueShowModalWait();
        new ResultsWorker(dialog, new Paginator(0, 0,
                ResultsDialog.MAX_RECORDS_PER_PAGE)).execute();
    }
}
