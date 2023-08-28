package org.jlab.phaser.network;

import org.jlab.phaser.model.JobSpecification;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.xml.bind.DatatypeConverter;
import org.jlab.phaser.NotificationListener;

/**
 * Responsible for decoding JSON messages per the Phaser Client-Server protocol.
 *
 * @author ryans
 */
public class PhaserClientMessageDecoder extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = Logger.getLogger(
            PhaserClientMessageDecoder.class.getName());

    private final List<NotificationListener> notificationListeners
            = new ArrayList<>();
    private final List<ResponseListener> responseListeners = new ArrayList<>();

    /**
     * Adds a NotificationListener to the collection of listeners to be notified of server
     * notifications.
     *
     * @param listener The NotificationListener
     */
    public void addNotificationListener(NotificationListener listener) {
        synchronized (notificationListeners) {
            notificationListeners.add(listener);
        }
    }

    /**
     * Adds a ResponseListener to the collection of listeners to be notified of server responses.
     *
     * @param listener The ResponseListener
     */
    public void addResponseListener(ResponseListener listener) {
        synchronized (responseListeners) {
            responseListeners.add(listener);
        }
    }

    /**
     * Notifies NotificationListeners of a status notification.
     *
     * @param message The notification message
     * @param error true if an error, false otherwise
     */
    private void notifyStatus(String message, Boolean error) {
        synchronized (notificationListeners) {
            for (NotificationListener listener : notificationListeners) {
                listener.statusNotification(message, error);
            }
        }
    }

    /**
     * Notifies NotificationListeners of a job notification. If there is no job then all fields are
     * null.
     *
     * @param job The Job or null
     * @param jobId The Job ID or null
     * @param jobStart The Job Start or null
     * @param paused true if pause, false if not, null if no job
     * @param loop The loop number or null
     * @param cavity The current cavity name or null
     * @param cavityStart The current cavity start date or null
     * @param progress The current cavity progress percent or null
     * @param label The current cavity progress label or null
     */
    private void notifyJob(JobSpecification job, Long jobId, Calendar jobStart, Boolean paused,
            Integer loop, String cavity, Calendar cavityStart, Integer progress, String label) {
        synchronized (notificationListeners) {
            for (NotificationListener listener : notificationListeners) {
                listener.jobNotification(job, jobId, jobStart, paused, loop,
                        cavity, cavityStart, progress, label);
            }
        }
    }

    /**
     * Notifies NotificationListeners of a pause notification.
     *
     * @param paused true if paused, false if not, otherwise null
     */
    private void notifyPaused(Boolean paused) {
        synchronized (notificationListeners) {
            for (NotificationListener listener : notificationListeners) {
                listener.pausedNotification(paused);
            }
        }
    }

    /**
     * Notifies NotificationListeners of a loop notification.
     *
     * @param loop The loop number or null
     */
    private void notifyLoop(Integer loop) {
        synchronized (notificationListeners) {
            for (NotificationListener listener : notificationListeners) {
                listener.loopNotification(loop);
            }
        }
    }

    /**
     * Notifies NotificationListeners of a cavity notification.
     *
     * @param start The cavity start date or null
     * @param cavity The cavity name or null
     */
    private void notifyCavity(Calendar start, String cavity) {
        synchronized (notificationListeners) {
            for (NotificationListener listener : notificationListeners) {
                listener.cavityNotification(start, cavity);
            }
        }
    }

    /**
     * Notifies NotificationListeners of a progress notification.
     *
     * @param value The progress percent (0 - 100) or null
     * @param label The progress label or null
     */
    private void notifyProgress(Integer value, String label) {
        synchronized (notificationListeners) {
            for (NotificationListener listener : notificationListeners) {
                listener.progressNotification(value, label);
            }
        }
    }

    /**
     * Notifies NotificationListeners of a response.
     *
     * @param json The JsonObject response
     */
    private void notifyResponseListeners(JsonObject json) {
        synchronized (responseListeners) {
            for (ResponseListener listener : responseListeners) {
                listener.handleResponse(json);
            }
        }
    }

    /**
     * Parses the JSON obtained from the Netty channel read and dispatches notifications.
     *
     * @param json The JsonObject read on the channel
     */
    private void parseAndDispatchNotification(JsonObject json) {
        String notificationType = json.getString("notification");
        if (notificationType != null) {
            switch (notificationType) {
                case "status":
                    String message = null;
                    if (json.containsKey("message") && !json.isNull("message")) {
                        message = json.getString("message");
                    }
                    Boolean error = null;
                    if (json.containsKey("error") && !json.isNull("error")) {
                        error = json.getBoolean("error");
                    }
                    notifyStatus(message, error);
                    break;
                case "job":
                    JobSpecification job = null;
                    Long jobId = null;
                    Calendar jobStart = null;
                    Boolean initPaused = null;
                    Integer initLoop = null;
                    String initCavity = null;
                    Calendar initCavityStart = null;
                    Integer progress = null;
                    String label = null;
                    if (json.containsKey("job") && !json.isNull("job")) {
                        JsonObject jobJson = json.getJsonObject("job");
                        boolean correct = jobJson.getBoolean("correct");
                        boolean continuous = jobJson.getBoolean("continuous");
                        JsonNumber maxPhaseErrorNumber = jobJson.getJsonNumber(
                                "max-phase-error");
                        float maxPhaseError = (float) maxPhaseErrorNumber.doubleValue();
                        JsonNumber maxMomentumErrorNumber = jobJson.getJsonNumber(
                                "max-momentum-error");
                        float maxMomentumError = (float) maxMomentumErrorNumber.doubleValue();
                        JsonNumber kickSamplesNumber = jobJson.getJsonNumber("kick-samples");
                        int kickSamples = (int) kickSamplesNumber.intValue();
                        JsonArray cavitiesArray = jobJson.getJsonArray(
                                "cavities");
                        LinkedHashSet<String> cavities = new LinkedHashSet<>();
                        for (JsonValue value : cavitiesArray) {
                            cavities.add(((JsonString) value).getString());
                        }
                        job
                                = new JobSpecification(correct, continuous,
                                        maxPhaseError, maxMomentumError, kickSamples, cavities);

                        if (json.containsKey("job-id") && !json.isNull("job-id")) {
                            jobId
                                    = json.getJsonNumber("job-id").longValueExact();
                        }

                        if (json.containsKey("start") && !json.isNull("start")) {
                            String startStr = json.getString("start");
                            jobStart = DatatypeConverter.parseDateTime(startStr);
                        }

                        if (json.containsKey("paused") && !json.isNull("paused")) {
                            initPaused = json.getBoolean("paused");
                        }

                        if (json.containsKey("loop") && !json.isNull("loop")) {
                            initLoop = json.getInt("loop");
                        }

                        if (json.containsKey("cavity") && !json.isNull("cavity")) {
                            initCavity = json.getString("cavity");
                        }

                        if (json.containsKey("cavity-start") && !json.isNull(
                                "cavity-start")) {
                            String startStr = json.getString("cavity-start");
                            initCavityStart = DatatypeConverter.parseDateTime(
                                    startStr);
                        }

                        if (json.containsKey("progress") && !json.isNull(
                                "progress")) {
                            progress = json.getInt("progress");
                        }

                        if (json.containsKey("label") && !json.isNull("label")) {
                            label = json.getString("label");
                        }
                    }
                    notifyJob(job, jobId, jobStart, initPaused, initLoop,
                            initCavity, initCavityStart, progress, label);
                    break;
                case "paused":
                    Boolean paused = null;
                    if (json.containsKey("paused") && !json.isNull("paused")) {
                        paused = json.getBoolean("paused");
                    }
                    notifyPaused(paused);
                    break;
                case "loop":
                    Integer loop = null;
                    if (json.containsKey("count") && !json.isNull("count")) {
                        loop = json.getInt("count");
                    }
                    notifyLoop(loop);
                    break;
                case "cavity":
                    Calendar cavityStart = null;
                    if (json.containsKey("start") && !json.isNull("start")) {
                        String startStr = json.getString("start");
                        cavityStart = DatatypeConverter.parseDateTime(startStr);
                    }
                    String cavity = null;
                    if (json.containsKey("name") && !json.isNull("name")) {
                        cavity = json.getString("name");
                    }
                    notifyCavity(cavityStart, cavity);
                    break;
                case "progress":
                    Integer value = null;
                    if (json.containsKey("value") && !json.isNull("value")) {
                        value = json.getInt("value");
                    }
                    String text = null;
                    if (json.containsKey("label") && !json.isNull("label")) {
                        text = json.getString("label");
                    }
                    notifyProgress(value, text);
                    break;
                default:
                    LOGGER.log(Level.SEVERE, "Unrecognized notification: {0}",
                            notificationType);
                    break;
            }
        } else {
            LOGGER.log(Level.SEVERE, "Notification type is empty");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        if (message instanceof JsonObject) {
            JsonObject json = (JsonObject) message;
            try {
                if (json.containsKey("response")) {
                    notifyResponseListeners(json);
                } else if (json.containsKey("notification")) {
                    parseAndDispatchNotification(json);
                } else {
                    LOGGER.log(Level.SEVERE, "Unrecognized message: {0}",
                            message);
                    notifyStatus("Server protocol not understood (unrecognized message)", true);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unable to parse server's message", e);
                notifyStatus("Server protocol not understood (unable to parse server message)",
                        true);
            }
        } else {
            LOGGER.log(Level.SEVERE, "Expected JsonObject, found: {0}", message);
            notifyStatus("Server protocol not understood (unrecognized format)", true);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        LOGGER.log(Level.SEVERE, "Problem communicating with server", cause);
        context.close();
    }
}
