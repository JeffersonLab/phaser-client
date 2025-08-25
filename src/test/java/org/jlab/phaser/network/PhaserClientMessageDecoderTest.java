package org.jlab.phaser.network;

import java.io.StringReader;
import java.util.Calendar;
import javax.json.Json;
import javax.json.JsonObject;
import org.jlab.phaser.NotificationListener;
import org.jlab.phaser.model.JobSpecification;
import org.junit.Test;

public class PhaserClientMessageDecoderTest {

  @Test
  public void parseAndDispatchNotificationTest() {

    String serverResponseStr =
        "{\"name\":\"1L22-3\",\"notification\":\"cavity\",\"start\":\"2023-09-12T10:26:21\"}";
    JsonObject serverResponseJson =
        Json.createReader(new StringReader(serverResponseStr)).readObject();

    PhaserClientMessageDecoder decoder = new PhaserClientMessageDecoder();

    decoder.addNotificationListener(
        new NotificationListener() {
          @Override
          public void statusNotification(String message, Boolean error) {}

          @Override
          public void jobNotification(
              JobSpecification job,
              Long jobId,
              Calendar jobStart,
              Boolean paused,
              Integer loop,
              String cavity,
              Calendar cavityStart,
              Integer progress,
              String label) {}

          @Override
          public void pausedNotification(Boolean paused) {}

          @Override
          public void loopNotification(Integer loop) {}

          @Override
          public void cavityNotification(Calendar start, String cavity) {}

          @Override
          public void progressNotification(Integer value, String label) {}
        });

    decoder.parseAndDispatchNotification(serverResponseJson);
  }
}
