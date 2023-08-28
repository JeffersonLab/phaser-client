package org.jlab.phaser.swing.dialog.chooser;

import java.awt.Dialog;

/**
 * A "chooser" widget for choosing a timestamp.
 * 
 * @author ryans
 */
public class TimestampChooser extends Chooser<String> {

    public TimestampChooser(Dialog parent, String format) {
        super(new TimestampChooserDialog(parent, format));
    }
}
