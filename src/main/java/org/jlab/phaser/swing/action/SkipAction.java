package org.jlab.phaser.swing.action;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.jlab.phaser.exception.CommandException;
import org.jlab.phaser.swing.PhaserClientFrame;
import org.jlab.phaser.swing.worker.MinimumExecutionSwingWorker;

/**
 * Handles the "Skip" button click on the control form. Utilizes the server
 * console to send a command to the server.
 *
 * @author ryans
 */
public final class SkipAction extends AbstractAction {

    private static final Logger LOGGER = Logger.getLogger(SkipAction.class.getName());

    /**
     * The frame.
     */
    private final PhaserClientFrame frame;

    /**
     * Creates a new SkipAction.
     * 
     * @param frame The PhaserClientFrame
     */
    public SkipAction(PhaserClientFrame frame) {
        this.frame = frame;
        putValue(AbstractAction.NAME, "Skip");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.queueShowModalWait();
        new MinimumExecutionSwingWorker<Void, Void>() {

            @Override
            protected Void doWithMinimumExecution() throws Exception {
                frame.getPhaserServerConsole().skip();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // See if there were any exceptions
                } catch (InterruptedException | ExecutionException ex) {
                    String title = "Unable to skip phasing";
                    String message = "Unexpected error";
                    LOGGER.log(Level.SEVERE, title, ex);

                    Throwable cause = ex.getCause();
                    if (cause != null && cause instanceof CommandException) {
                        message = cause.getMessage();
                    }

                    JOptionPane.showMessageDialog(frame, message, title,
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    frame.hideModalWait();
                }
            }
        }.execute();
    }
}
