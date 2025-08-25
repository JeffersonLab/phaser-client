package org.jlab.phaser.network;

import javax.json.JsonObject;

/**
 * A contract for classes interested in Phaser Client-Server protocol command responses.
 *
 * @author ryans
 */
public interface ResponseListener {
  /**
   * Notification that a server response to a command has been received.
   *
   * @param json The JsonObject containing the response
   */
  public void handleResponse(JsonObject json);
}
