package org.jlab.phaser.swing.dialog.chooser;

import java.awt.Component;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.jlab.phaser.swing.util.GenericButtonAreaLayout;

/**
 * A file chooser which prompts the user before allowing file overwrite.
 *
 * This file chooser also lays out buttons with their preferred size as opposed
 * to making the width equal to the width of the widest button on the form.
 *
 * @author ryans
 */
public class ConfirmOverwriteFileChooser extends JFileChooser {

    @Override
    public void approveSelection() {
        File selected = getSelectedFile();
        if ((selected != null) && selected.exists()) {
            // Note: if you use two strings then JOptionPane will make them
            // buttons of the same size; we want size to differ.  We have to
            // hack the close action though, so the return value is not
            // number 1 when cancel is clicked, it is -1 just as if the top
            // X in the window was clicked.  Oh well we can live with that.
            final JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.getWindowAncestor(cancelButton).dispose();
                }
            });
            Object[] options = new Object[]{"Overwrite File", cancelButton};
            int response = JOptionPane.showOptionDialog(this,
                    "The file " + selected.getName()
                    + " already exists. Do you want to overwrite the existing file?",
                    "File Exists", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (response != 0) {
                return;
            }
        }

        super.approveSelection();
    }

    @Override
    protected JDialog createDialog(Component parent) throws HeadlessException {
        JDialog dialog = super.createDialog(parent);
        dialog.setResizable(false);
        // Now we want to allow cancel button to keep preferred size
        overrideButtonAreaLayout();
        return dialog;
    }

    /**
     * Set the button panel layout manager to our custom one that allows button size to vary 
     */
    private void overrideButtonAreaLayout() {
        JButton cancelButton = getFirstButtonByText(this, "Cancel");
        if (cancelButton != null) {
            Container buttonPanel = cancelButton.getParent();
            LayoutManager buttonLayout = new GenericButtonAreaLayout(false, 6,
                    SwingConstants.RIGHT, false);
            buttonPanel.setLayout(buttonLayout);
        }
    }

    /**
     * Find the first button in a container that has the specified text label.
     * 
     * @param c The container
     * @param text The text label
     * @return The first button to match or null if none found
     */
    private JButton getFirstButtonByText(Container c, String text) {
        JButton temp = null;
        for (Component comp : c.getComponents()) {
            if (comp == null) {
                continue;
            }
            if (comp instanceof JButton && (temp = (JButton) comp).getText() != null && temp.getText().equals(
                    text)) {
                return temp;
            } else if (comp instanceof Container) {
                if ((temp = getFirstButtonByText((Container) comp, text)) != null) {
                    return temp;
                }
            }
        }
        return temp;
    }
}
