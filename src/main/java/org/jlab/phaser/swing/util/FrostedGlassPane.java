package org.jlab.phaser.swing.util;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 * When visible covers the entire frame/dialog with a 50% transparent shroud preventing users from
 * interacting. This is useful to show users the interface is disabled while background processes
 * run.
 *
 * @author ryans
 */
public class FrostedGlassPane extends JPanel {

    /**
     * Create a new FrostedGlassPane.
     */
    public FrostedGlassPane() {
        setOpaque(false);
    }

    @Override
    public final void setOpaque(boolean opaque) { 
        super.setOpaque(opaque); // We make this method final since we call from constructor
    }

    @Override
    public void paint(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        super.paint(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
        g2.fillRect(0, 0, w, h);

        g2.dispose();
    }
}
