package org.jlab.phaser.swing.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * A widget for displaying a hyperlink and opening the user's default web browser with the
 * referenced page when the link is clicked.
 *
 * <p>Note: this doesn't work on JLab Red Hat Linux boxes currently due to lack of support for Java
 * Desktop API in this environment.
 *
 * @author ryans
 */
public class HyperLinkEnabledMessage extends JEditorPane {

  private static final Logger LOGGER = Logger.getLogger(HyperLinkEnabledMessage.class.getName());

  /**
   * Create a new HyperLinkEnabledMessage with the specified HTML message.
   *
   * @param htmlMessage The HTML message
   */
  public HyperLinkEnabledMessage(String htmlMessage) {
    setContentType("text/html");
    setText(htmlMessage);
    addHyperlinkListener(
        new HyperlinkListener() {

          @Override
          public void hyperlinkUpdate(HyperlinkEvent e) {
            HyperlinkEvent.EventType type = e.getEventType();
            final URL url = e.getURL();
            if (type == HyperlinkEvent.EventType.ACTIVATED) {
              Desktop desktop = Desktop.getDesktop();
              try {
                desktop.browse(url.toURI());
              } catch (URISyntaxException | IOException | UnsupportedOperationException ex) {
                LOGGER.log(Level.WARNING, "Unable to launch web browser", ex);
                JOptionPane.showInputDialog(
                    null,
                    "Copy and paste URL (copy with CTRL-C): ",
                    "Unable to launch web browser",
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    null,
                    url.toString());
              }
            }
          }
        });
    setEditable(false);
    setOpaque(false);
  }

  @Override
  public final void setText(String text) {
    // We make this method final since we call it from the constructor
    super.setText(text);
  }

  @Override
  public final void setEditable(boolean editable) {
    // We make this method final since we call it from the constructor
    super.setEditable(editable);
  }

  @Override
  public final void setOpaque(boolean opaque) {
    // We make this method final since we call it from the constructor
    super.setOpaque(opaque);
  }

  @Override
  public final void addHyperlinkListener(HyperlinkListener listener) {
    // We make this method final since we call it from the constructor
    super.addHyperlinkListener(listener);
  }
}
