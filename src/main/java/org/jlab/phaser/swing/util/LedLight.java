package org.jlab.phaser.swing.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;

/**
 * An LED light widget.
 *
 * @author ryans
 */
public class LedLight extends JPanel {

    private static final Color SOCKET_OFF = new Color(0, 0, 0, 255);
    private static final Color SOCKET_ON = new Color(0, 0, 0, 64);

    private Color transparentLightColor = new Color(255, 0, 0, 128);
    private final Color transparentBackgroundColor = new Color(getBackground().getRed(),
            getBackground().getGreen(), getBackground().getBlue(), 128);
    private final Timer timer;
    private boolean on = false;

    /**
     * Create a new LED light.
     */
    public LedLight() {
        setBorder(BorderFactory.createEtchedBorder());

        timer = new Timer(750, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                on = !on;

                repaint();
            }
        });
    }

    @Override
    public final void setBorder(Border b) { // final since we're calling it in the constructor
        super.setBorder(b);
    }

    /**
     * Set the LED to a steady color.
     *
     * @param color The color
     */
    public void steady(Color color) {
        timer.stop();
        transparentLightColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 128);
        on = true;
        repaint();
    }

    /**
     * Flash the LED.
     *
     * @param speed The milliseconds between flashes
     * @param color The color
     */
    public void flash(int speed, Color color) {
        timer.stop();
        transparentLightColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 128);
        on = false;
        timer.setInitialDelay(speed);
        timer.setDelay(speed);
        timer.start();
    }

    /**
     * Turn the LED off.
     */
    public void off() {
        timer.stop();
        on = false;
        repaint();
    }

    /**
     * Return the color of the LED light itself (core piece).
     *
     * @return The light color
     */
    private Color getLightColor() {
        if (on) {
            return transparentLightColor;
        } else {
            return transparentBackgroundColor;
        }
    }

    /**
     * Return the color of the LED socket.
     *
     * @return The socket color
     */
    private Color getSocketColor() {
        if (on) {
            return SOCKET_ON;
        } else {
            return SOCKET_OFF;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(24, 24);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final int radius = 12;

        Color lightColor = getLightColor();
        Color socketColor = getSocketColor();

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Paint original = g2.getPaint();

        Shape circle = new Ellipse2D.Double((getWidth() / 3) - 1,
                (getHeight() / 3) - 1, getWidth() / 3, getHeight() / 3);
        g2.setColor(socketColor);
        g2.draw(circle);

        Point2D center = new Point2D.Float((getWidth() / 2) - 1,
                (getHeight() / 2) - 1);
        float[] dist = {0.0f, 1.0f};

        Color[] colors = {lightColor, getBackground()};

        RadialGradientPaint p = new RadialGradientPaint(center, radius, dist,
                colors);

        g2.setPaint(p);
        g2.fillOval(0, 0, getWidth(), getHeight());

        g2.setPaint(original);

    }
}
