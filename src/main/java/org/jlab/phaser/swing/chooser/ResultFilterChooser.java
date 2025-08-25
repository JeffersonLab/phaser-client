package org.jlab.phaser.swing.chooser;

import java.awt.Dialog;
import org.jlab.phaser.model.ResultFilter;
import org.jlab.phaser.swing.generated.dialog.chooser.ResultFilterChooserDialog;

/**
 * A "chooser" widget for choosing a ResultFilter.
 *
 * A "chooser" is a concept borrowed from Swing classes like JColorChooser and JFileChooser.
 *
 * @author ryans
 */
public class ResultFilterChooser extends Chooser<ResultFilter> {

    /**
     * Create a new ResultFilterChooser.
     *
     * @param parent The parent Dialog
     */
    public ResultFilterChooser(Dialog parent) {
        super(new ResultFilterChooserDialog(parent, true));
    }
}
