package org.jlab.phaser.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * Responsible for decoding JSON messages from a Netty Channel.
 *
 * @author ryans
 */
public class JsonDecoder extends ChannelInboundHandlerAdapter {

  private static final Logger LOGGER = Logger.getLogger(JsonDecoder.class.getName());

  @Override
  public void channelRead(ChannelHandlerContext context, Object message) {
    LOGGER.log(Level.FINEST, "JsonDecoder Message: {0}", message);
    if (message instanceof String) {
      StringReader stringReader = new StringReader((String) message);
      try (JsonReader jsonReader = Json.createReader(stringReader)) {
        JsonObject json = jsonReader.readObject();
        context.fireChannelRead(json);
      } catch (JsonException | IllegalStateException e) {
        context.fireExceptionCaught(e);
      }
    } else {
      context.fireExceptionCaught(
          new DecoderException("JsonDecoder expects a String, found: " + message));
    }
  }
}
