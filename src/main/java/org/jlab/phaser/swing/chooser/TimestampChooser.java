package org.jlab.phaser.swing.chooser;

import java.awt.Dialog;
import org.jlab.phaser.swing.generated.dialog.chooser.TimestampChooserDialog;

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
