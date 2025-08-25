package org.jlab.phaser.swing.chooser;

import org.jlab.phaser.swing.generated.dialog.chooser.CavityChooserDialog;

import java.awt.Dialog;
import java.util.LinkedHashSet;

/**
 * A "chooser" widget for choosing a cavity.
 * 
 * @author ryans
 */
public class CavityChooser extends Chooser<String> {

    /**
     * Creates a new CavityChooser.
     * 
     * @param parent The parent dialog
     * @param cavities The collection of cavity names
     */
    public CavityChooser(Dialog parent, LinkedHashSet<String> cavities) {
        super(new CavityChooserDialog(parent, cavities));
    }
}
