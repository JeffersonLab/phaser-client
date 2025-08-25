package org.jlab.phaser.swing.chooser;

import java.awt.Dialog;
import java.awt.Frame;
import javax.swing.JDialog;

/**
 * A template for chooser dialogs.
 *
 * @author ryans
 * @param <E> The Element to choose
 */
public abstract class ChooserDialog<E> extends JDialog {

    /**
     * Create a new modal ChooserDialog.
     * 
     * @param parent The parent Frame
     */
    public ChooserDialog(Frame parent) {
        super(parent, true);
    }
    
    /**
     * Create a new modal ChooserDialog.
     * 
     * @param parent The parent Dialog
     */
    public ChooserDialog(Dialog parent) {
        super(parent, true);
    }    
    
    /**
     * Returns true if the "OK" button had been pressed.
     * 
     * @return true if "OK" pressed, false otherwise
     */
    public abstract boolean isOkPressed();

    /**
     * Returns the selected element.
     * 
     * @return The selected element or null if none
     */
    public abstract E getSelected();

    /**
     * Sets the initially selected element.
     * 
     * @param selected The initially selected element
     */
    public abstract void setInitiallySelected(E selected);
    
    /**
     * Centers the chooser dialog over the parent container.
     */
    public void centerDialogOverParent() {
        pack();
        setLocationRelativeTo(this.getParent());        
    }    
}
