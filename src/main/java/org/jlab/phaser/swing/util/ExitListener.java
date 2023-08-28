package org.jlab.phaser.swing.util;

/**
 * A contract for classes interested in user request to exit.
 *
 * This is necessary for various Swing components which must explicitly be
 * shutdown before the JVM may exit.
 *
 * @author ryans
 */
public interface ExitListener {

    /**
     * The Swing GUI is being shutdown.
     */
    public void exit();
}
