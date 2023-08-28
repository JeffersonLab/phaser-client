package org.jlab.phaser.network;

import org.jlab.phaser.model.JobSpecification;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.LinkedHashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.jlab.phaser.exception.CommandException;
import org.jlab.phaser.PhaserServerConsole;

/**
 * An implementation of a PhaserServerConsole which leverages the Netty network library to do the
 * heavy lifting.
 *
 * This class also implements ResponseListener and should be registered as a response listener so
 * that it can check for "ok" responses and returned data.
 *
 * @author ryans
 */
public class NettyJsonConsole implements PhaserServerConsole, ResponseListener {

    private static final Logger LOGGER = Logger.getLogger(
            NettyJsonConsole.class.getName());
    private final static long RESPONSE_POLL_SECONDS = 5;
    private Channel channel = null;
    /**
     * The response queue should have either zero or one response in it at any given time.
     */
    private final BlockingQueue<JsonObject> responseQueue
            = new LinkedBlockingQueue<>(1);

    /**
     * Serializes client command/response pairs and checks for an "ok" response.
     *
     * @param command The command
     * @return The response JsonObject
     * @throws CommandException If something goes wrong
     */
    private synchronized JsonObject sendCommand(String command) throws
            CommandException {
        // As a matter of housekeeping first check response queue.  If something
        // is present then clear it and log it as an issue
        if (responseQueue.size() > 0) {
            for (JsonObject response : responseQueue) {
                LOGGER.log(Level.SEVERE,
                        "Unsolicited response found in response queue: {0}",
                        response.toString());
            }
            responseQueue.clear();
        }

        ChannelFuture future = channel.writeAndFlush(command);

        future.awaitUninterruptibly();

        JsonObject response = null;

        if (future.isSuccess()) {
            try {
                response = responseQueue.poll(RESPONSE_POLL_SECONDS,
                        TimeUnit.SECONDS);

                if (response == null) {
                    throw new CommandException(
                            "Server did not respond within " + RESPONSE_POLL_SECONDS + " seconds");
                }

                if (!"ok".equals(response.getString("response"))) {
                    String message = response.getString("message");
                    throw new CommandException(
                            "Unable to execute command: " + message);
                }
            } catch (InterruptedException e) {
                throw new CommandException(
                        "Interrupted while waiting for server response", e);
            }
        } else {
            throw new CommandException("Unable to send command",
                    future.cause());
        }

        return response;
    }

    /**
     * Parses the server JsonObject response into a list of cavity names.
     *
     * @param response The JsonObject returned from the server
     * @return The cavity name list
     * @throws CommandException If unable to parse the response
     */
    private LinkedHashSet<String> parseCavitiesResponse(JsonObject response) throws
            CommandException {
        LinkedHashSet<String> cavities = new LinkedHashSet<>();

        if (!response.containsKey("cavities")) {
            throw new CommandException(
                    "Server response did not contain cavities");
        }

        JsonArray cavitiesArray = response.getJsonArray("cavities");
        for (JsonValue value : cavitiesArray) {
            cavities.add(((JsonString) value).getString());
        }

        return cavities;
    }

    @Override
    public LinkedHashSet<String> cavities() throws CommandException {
        String command = "{\"command\": \"cavities\"}\n";

        LOGGER.log(Level.FINEST, "Client Command: {0}", command);

        JsonObject response = sendCommand(command);

        return parseCavitiesResponse(response);
    }

    @Override
    public void applyCorrections(BigInteger[] resultIdArray) throws CommandException {
        String command = "{\"command\": \"apply-corrections\", \"results\": [";

        if (resultIdArray != null) {
            if (resultIdArray.length > 0) {
                command = command + resultIdArray[0];
            }

            for (int i = 1; i < resultIdArray.length; i++) {
                command = command + ", " + resultIdArray[i];
            }
        }

        command = command + "]}\n";

        LOGGER.log(Level.FINEST, "Client Command: {0}", command);

        sendCommand(command);
    }

    @Override
    public void start(JobSpecification job) throws CommandException {
        DecimalFormat decimalFormat = new DecimalFormat("0.0#####");
        StringBuilder builder = new StringBuilder();
        builder.append("{\"correct\": ");
        builder.append(job.isCorrect());
        builder.append(", \"continuous\": ");
        builder.append(job.isContinuous());
        builder.append(", \"max-phase-error\": ");
        builder.append(decimalFormat.format(job.getMaxPhaseError()));
        builder.append(", \"max-momentum-error\": ");
        builder.append(decimalFormat.format(job.getMaxMomentumError()));
        builder.append(", \"kick-samples\": ");
        builder.append(job.getKickSamples());
        builder.append(", \"cavities\": [");
        for (String cavity : job.getCavities()) {
            builder.append("\"");
            builder.append(cavity);
            builder.append("\", ");
        }
        int lastCommaIndex = builder.lastIndexOf(",");
        builder.delete(lastCommaIndex, lastCommaIndex + 1);
        builder.append("]}");

        String command = "{\"command\": \"start\", \"job\": " + builder.toString() + "}\n";

        LOGGER.log(Level.FINEST, "Client Command: {0}", command);

        sendCommand(command);
    }

    @Override
    public void pause() throws CommandException {
        String command = "{\"command\": \"pause\"}\n";

        LOGGER.log(Level.FINEST, "Client Command: {0}", command);

        sendCommand(command);
    }

    @Override
    public void resume() throws CommandException {
        String command = "{\"command\": \"resume\"}\n";

        LOGGER.log(Level.FINEST, "Client Command: {0}", command);

        sendCommand(command);
    }

    private String parseServerVersion(JsonObject response) throws
            CommandException {
        if (!response.containsKey("version")) {
            throw new CommandException(
                    "Server response did not contain version");
        }

        JsonString value = response.getJsonString("version");
        String version = value.getString();

        return version;
    }

    @Override
    public String serverVersion() throws CommandException {
        String command = "{\"command\": \"version\"}\n";

        LOGGER.log(Level.FINEST, "Client Command: {0}", command);

        JsonObject response = sendCommand(command);

        return parseServerVersion(response);
    }

    @Override
    public void stop() throws CommandException {
        String command = "{\"command\": \"stop\"}\n";

        LOGGER.log(Level.FINEST, "Client Command: {0}", command);

        sendCommand(command);
    }

    @Override
    public void skip() throws CommandException {
        String command = "{\"command\": \"skip\"}\n";

        LOGGER.log(Level.FINEST, "Client Command: {0}", command);

        sendCommand(command);
    }

    @Override
    public void handleResponse(JsonObject json) {
        responseQueue.add(json);
    }

    /**
     * Set the Netty communication channel to use.
     *
     * @param channel The channel
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
