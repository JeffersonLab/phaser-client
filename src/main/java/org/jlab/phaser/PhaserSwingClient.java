package org.jlab.phaser;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.JdkLoggerFactory;
import java.awt.EventQueue;
import java.awt.Frame;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.jlab.phaser.db.OracleJdbcConsole;
import org.jlab.phaser.exception.CommandException;
import org.jlab.phaser.exception.InitializationException;
import org.jlab.phaser.exception.ShutdownException;
import org.jlab.phaser.network.JsonDecoder;
import org.jlab.phaser.network.NettyJsonConsole;
import org.jlab.phaser.network.PhaserClientMessageDecoder;
import org.jlab.phaser.swing.CavityCache;
import org.jlab.phaser.swing.generated.MaydayFrame;
import org.jlab.phaser.swing.generated.PhaserClientFrame;
import org.jlab.phaser.swing.util.ExitListener;

/**
 * The entry point of the application.
 *
 * <p>The PhaserSwingClient class loads the client properties, initializes a network connection to
 * the server, wires up callbacks, and initializes the Swing GUI.
 *
 * @author ryans
 */
public final class PhaserSwingClient {

  /** Stores the client properties (configuration parameters from file). */
  public static final Properties CLIENT_PROPERTIES = new Properties();

  /** Contain the application release data and version. */
  public static final Properties RELEASE_PROPERTIES = new Properties();

  private static final Logger LOGGER = Logger.getLogger(PhaserSwingClient.class.getName());

  private final int MAX_FRAME_LENGTH = 1048576;

  private volatile boolean userRequestedExit = false;

  /**
   * Instantiates a new PhaserSwingClient which is connected to the Phaser server at the specified
   * host and port.
   *
   * @param host The server host name
   * @param port The server port
   * @throws InterruptedException If an unexpected interrupt is signaled
   * @throws InitializationException If unable to initialize
   * @throws ShutdownException If unable to gracefully shutdown
   */
  public PhaserSwingClient(String host, int port)
      throws InterruptedException, InitializationException, ShutdownException {
    final PhaserClientMessageDecoder phaserDecoder = new PhaserClientMessageDecoder();
    NettyJsonConsole jobConsole = new NettyJsonConsole();
    OracleJdbcConsole dbConsole = new OracleJdbcConsole();
    PhaserClientFrame frame = new PhaserClientFrame(jobConsole, dbConsole);
    MultiThreadIoEventLoopGroup workerGroup =
        new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
    Channel channel = null;
    try {
      Bootstrap boot = new Bootstrap();
      boot.group(workerGroup);
      boot.channel(NioSocketChannel.class);
      boot.option(ChannelOption.SO_KEEPALIVE, true);
      boot.handler(
          new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
              channel
                  .pipeline()
                  .addLast(
                      new DelimiterBasedFrameDecoder(
                          MAX_FRAME_LENGTH, true, Delimiters.lineDelimiter()),
                      new StringDecoder(CharsetUtil.UTF_8),
                      new JsonDecoder(),
                      new StringEncoder(CharsetUtil.UTF_8),
                      phaserDecoder);
            }
          });

      phaserDecoder.addNotificationListener(frame);
      phaserDecoder.addResponseListener(jobConsole);

      final ChannelFuture future = boot.connect(host, port).await();

      if (!future.isSuccess()) {
        throw new InitializationException("Unable to connect to server", future.cause());
      }

      channel = future.channel();

      jobConsole.setChannel(channel);

      frame.addExitListener(
          new ExitListener() {

            @Override
            public void exit() {
              userRequestedExit = true;
              future.channel().close();
            }
          });

      try {
        String serverVersion = jobConsole.serverVersion();
        LOGGER.log(Level.FINEST, "Server version: {0}", serverVersion);
        frame.setServerVersion(serverVersion);
      } catch (CommandException e) {
        throw new InitializationException("Unable to query the server version", e);
      }

      LOGGER.log(Level.FINEST, "Fetching cavities...");
      try {
        CavityCache.setCavities(jobConsole.cavities());
      } catch (CommandException e) {
        throw new InitializationException("Unable to fetch cavity list from server", e);
      }
      LOGGER.log(Level.FINEST, "Done Fetching cavities");

      show(frame);

      // Main thread waits here until connection is closed
      channel.closeFuture().await();

      if (!userRequestedExit) {
        LOGGER.log(Level.SEVERE, "Connection to server was closed without user consent");
        MaydayFrame mayday = new MaydayFrame("Connection to the server was closed unexpectedly");
        show(mayday);
      }

      if (!future.isSuccess()) {
        throw new ShutdownException(
            "Unable to gracefully close connection to server", future.cause());
      }
    } finally {
      // Should already be closed in all but a few odd cases (exceptions)
      if (channel != null && channel.isOpen()) {
        channel.close().awaitUninterruptibly();
      }
      workerGroup.shutdownGracefully();
      frame.dispose();
    }
  }

  private static void show(final Frame frame) {
    EventQueue.invokeLater(
        new Runnable() {

          @Override
          public void run() {
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
          }
        });
  }

  /**
   * The entry point of the application.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    try (InputStream propStream =
            PhaserSwingClient.class.getClassLoader().getResourceAsStream("client.properties");
        InputStream releaseStream =
            PhaserSwingClient.class.getClassLoader().getResourceAsStream("release.properties");
        InputStream loggingStream =
            PhaserSwingClient.class.getClassLoader().getResourceAsStream("logging.properties")) {
      if (propStream == null) {
        throw new InitializationException("Did not find client.properties");
      }

      if (releaseStream == null) {
        throw new InitializationException("File Not Found; Configuration File: release.properties");
      }

      if (loggingStream == null) {
        throw new InitializationException("File Not Found; Configuration File: logging.properties");
      }

      CLIENT_PROPERTIES.load(propStream);

      RELEASE_PROPERTIES.load(releaseStream);

      // java.util.logging configuration defaults to $JAVA_HOME/lib/logging.properties and is
      // overridden by the
      // system property -Djava.util.logging.config.file, but this does NOT search the classpath.
      // So we leverage
      // the classpath search ourselves and manually configure here
      LogManager.getLogManager().readConfiguration(loggingStream);

      // Configure Netty to use JUL
      InternalLoggerFactory.setDefaultFactory(JdkLoggerFactory.INSTANCE);

      String host = CLIENT_PROPERTIES.getProperty("server.host");
      int port = Integer.parseInt(CLIENT_PROPERTIES.getProperty("server.port"));

      new PhaserSwingClient(host, port);
      LOGGER.log(Level.FINEST, "Shutdown completed successfully");
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Unable to load client properties", e);
      MaydayFrame mayday = new MaydayFrame("Unable to load configuration file: " + e.getMessage());
      show(mayday);
    } catch (InterruptedException e) {
      LOGGER.log(Level.SEVERE, "Interrupted", e);
    } catch (InitializationException e) {
      LOGGER.log(Level.SEVERE, "Unable to initialize", e);
      MaydayFrame mayday = new MaydayFrame("Unable to initialize: " + e.getMessage());
      show(mayday);
    } catch (ShutdownException e) {
      LOGGER.log(Level.SEVERE, "Unable to shutdown gracefully", e);
    } catch (RuntimeException e) {
      LOGGER.log(Level.SEVERE, "Something unexpected happened", e);
      System.exit(1); // Swing GUI may be locked up so let's kill it
    }
  }
}
