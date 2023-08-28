package org.jlab.phaser.swing.dialog.chooser;

import java.awt.Dialog;
import org.jlab.phaser.model.JobFilter;

/**
 * A "chooser" widget for choosing a JobFilter.
 *
 * A "chooser" is a concept borrowed from Swing classes like JColorChooser and
 * JFileChooser.
 * 
 * @author ryans
 */
public class JobFilterChooser extends Chooser<JobFilter> {
    
    /**
     * Create a new JobFilterChooser.
     * 
     * @param parent The parent Dialog
     */
    public JobFilterChooser(Dialog parent) {
        super(new JobFilterChooserDialog(parent));
    }       
}
