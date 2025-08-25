package org.jlab.phaser;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * @author ryans
 */
public class TestServer {

  private final int MAX_FRAME_LENGTH = 1048576;

  public TestServer() throws InterruptedException {
    int port = 2048;

    MultiThreadIoEventLoopGroup bossGroup =
        new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
    MultiThreadIoEventLoopGroup workerGroup =
        new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

    try {
      ServerBootstrap boot = new ServerBootstrap();
      boot.group(bossGroup, workerGroup);
      boot.channel(NioServerSocketChannel.class);
      boot.option(ChannelOption.SO_BACKLOG, 128);
      boot.option(ChannelOption.SO_KEEPALIVE, true);
      boot.childHandler(
          new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
              channel
                  .pipeline()
                  .addLast(
                      new DelimiterBasedFrameDecoder(
                          MAX_FRAME_LENGTH, true, Delimiters.lineDelimiter()),
                      new StringDecoder(CharsetUtil.UTF_8),
                      new StringEncoder(CharsetUtil.UTF_8),
                      new TestServerHandler());
            }
          });

      System.out.println("Server started");

      ChannelFuture future = boot.bind(port).sync();

      future.channel().closeFuture().sync();

    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }

  public static class TestServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
      try {
        System.out.println("Server Received Message: " + (String) message);

        JsonReader reader = Json.createReader(new StringReader((String) message));
        JsonObject obj = reader.readObject();
        String command = obj.getString("command");

        switch (command) {
          case "version":
            context.write("{\"response\": \"ok\", \"version\": \"Test-Server 0.2.0\"}\n");
            break;
          case "cavities":
            context.write(
                "{\"response\": \"ok\", \"cavities\": [\"3L01-1\", \"2L26-8\", \"2L26-7\", \"2L02-1\", \"1L26-8\", \"1L04-1\", \"0L03-1\", \"0L03-2\", \"0L03-3\", \"0L03-4\", \"1L05-1\"]}\n");
            break;
          case "start":
            context.write("{\"response\": \"ok\"}\n");
            break;
          default:
            System.out.println("Unknown command: " + command);
        }

        context.flush();
      } finally {
        ReferenceCountUtil.release(message);
      }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
      cause.printStackTrace();
      context.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext context) {
      System.out.println("Client Connected");

      // Unavailable scenario
      // context.writeAndFlush("{\"notification\": \"status\", \"message\": \"Energy lock cavity
      // offline\"}\n");
      // Idle scenario
      context.writeAndFlush("{\"notification\": \"status\", \"message\": \"Hello from Server\"}\n");
      context.writeAndFlush("{\"notification\": \"job\", \"job\": null}\n");

      // Paused scenario
      // context.writeAndFlush("{\"notification\": \"status\", \"name\": \"paused\"}\n");
      // context.writeAndFlush("{\"notification\": \"job\", \"job\": {\"correct\": true,
      // \"continuous\": true, \"max-error\": 35.75, \"cavities\": [\"0L03-1\", \"0L03-2\",
      // \"0L03-3\"]}, \"start\": \"2014-06-25T13:30:00\"}\n");
      // context.writeAndFlush("{\"notification\": \"loop\", \"count\": 1}\n");
      // context.writeAndFlush("{\"notification\": \"cavity\", \"name\": \"0L03-1\", \"start\":
      // \"2014-06-25T13:30:00\"}\n");
      // Working scenario
      // context.writeAndFlush("{\"notification\": \"status\", \"name\": \"working\"}\n");
      // context.writeAndFlush("{\"notification\": \"job\", \"job\": {\"correct\": true,
      // \"continuous\": true, \"max-error\": 35.75, \"cavities\": [\"0L03-1\", \"0L03-2\",
      // \"0L03-3\"]}, \"start\": \"2014-06-25T13:30:00\"}\n");
      // context.writeAndFlush("{\"notification\": \"loop\", \"count\": 1}\n");
      // context.writeAndFlush("{\"notification\": \"cavity\", \"name\": \"0L03-1\", \"start\":
      // \"2014-06-25T13:30:00\"}\n");
    }
  }

  public static void main(String[] args) throws InterruptedException {
    new TestServer();
  }
}
