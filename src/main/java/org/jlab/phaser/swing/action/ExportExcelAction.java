package org.jlab.phaser.swing.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jlab.phaser.export.ExcelResultsService;
import org.jlab.phaser.exception.CommandException;
import org.jlab.phaser.model.ResultFilter;
import org.jlab.phaser.model.ResultPage;
import org.jlab.phaser.swing.worker.MinimumExecutionSwingWorker;

import org.jlab.phaser.model.Paginator;
import org.jlab.phaser.swing.dialog.ResultsDialog;
import org.jlab.phaser.swing.dialog.chooser.ConfirmOverwriteFileChooser;

/**
 * Handles the "Excel" button click on the results form. Launches a new worker to export results in
 * Microsoft Excel format.
 *
 * @author ryans
 */
public final class ExportExcelAction extends AbstractAction {

    /**
     * Excel specification only allows a maximum of 1,048,576, but we only accept an even 1,000,000
     * so there is room for extra stuff.
     */
    private static final long MAX_ROWS = 1000000;

    private static final Logger LOGGER = Logger.getLogger(
            ExportExcelAction.class.getName());

    /**
     * The dialog.
     */
    private final ResultsDialog dialog;

    /**
     * Create a new ExportExcelAction.
     *
     * @param dialog The ResultsDialog
     */
    public ExportExcelAction(ResultsDialog dialog) {
        this.dialog = dialog;
        putValue(AbstractAction.NAME, "Excel...");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JFileChooser chooser = new ConfirmOverwriteFileChooser();
        chooser.setDialogTitle("Export Results to Excel");
        chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
        chooser.setSelectedFile(new File("results.xlsx"));
        chooser.setFileFilter(new FileNameExtensionFilter("Excel File", "xlsx"));
        int retval = chooser.showDialog(dialog, "Export Excel");
        if (retval == JFileChooser.APPROVE_OPTION) {
            final File file = chooser.getSelectedFile();

            dialog.queueShowModalWait();
            new MinimumExecutionSwingWorker<Void, Void>() {

                @Override
                protected Void doWithMinimumExecution() throws Exception {
                    ResultFilter filter = dialog.getResultFilter();
                    ResultPage page = dialog.getResultsCommandConsole().results(filter,
                            new Paginator(0, 0, MAX_ROWS));

                    ExcelResultsService service = new ExcelResultsService();
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        service.export(page, out);
                    }

                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get(); // See if there were any exceptions
                    } catch (InterruptedException | ExecutionException ex) {
                        String title = "Unable to export to excel";
                        String message = "Unexpected error";
                        LOGGER.log(Level.SEVERE, title, ex);

                        Throwable cause = ex.getCause();
                        if (cause != null && cause instanceof CommandException) {
                            message = cause.getMessage();
                        }

                        JOptionPane.showMessageDialog(dialog, message, title,
                                JOptionPane.ERROR_MESSAGE);
                    } finally {
                        dialog.hideModalWait();
                    }
                }
            }.execute();
        }
    }
}
