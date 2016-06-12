/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/*
 * @author Sumit kapoor
 * This class publishes stream messages to given topic
 */

public class KafkaStreamProducer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(KafkaStreamProducer.class);

    private static final int RFC3164_LENGTH = 15;
    private static final Pattern SPACES_TWO_CHECK = Pattern.compile("  ");
    private static final DateTimeFormatter RFC3164_FORMAT_DATETIME = DateTimeFormat.forPattern("MMM d HH:mm:ss")
            .withZoneUTC();
    private static final int RFC5424_LEN_PREFIX = 19;
    private static final String TIMEPAT = "yyyy-MM-dd'T'HH:mm:ss";
    private DateTimeFormatter timeParser = DateTimeFormat.forPattern(TIMEPAT).withZoneUTC();

    private final KafkaProducer<Long, String> producer;
    private final String topic;
    private final Boolean isAsync;
    private final Long key;
    private String message = null;

    public KafkaStreamProducer(String topic, Boolean isAsync, Long key, String message) {
        Properties props = new Properties();
        props.put("bootstrap.servers", KafkaProperties.zkConnect);
        props.put("client.id", "StreamProducer");
        props.put("key.serializer", LongSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        producer = new KafkaProducer<>(props);
        this.topic = topic;
        this.isAsync = isAsync;
        this.key = key;
        this.message = message;
    }

    public KafkaStreamProducer(String topic, Boolean isAsync, String message, String type,
            KafkaProducer<Long, String> producer) {

        synchronized (producer) {
            this.producer = producer;
        }

        JSONObject data = null;
        if (type.equals("syslog")) {
            try {
                data = parseLogMessage(message);
                this.message = data.toString();
            } catch (JSONException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        } else {
            this.message = message;
        }
        this.topic = topic;
        this.isAsync = isAsync;
        this.key = System.currentTimeMillis();
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        if (isAsync) { // Send asynchronously
            producer.send(new ProducerRecord<>(topic, key, message), new StreamCallBack(startTime, key, message));
        } else { // Send synchronously
            try {
                producer.send(new ProducerRecord<>(topic, key, message)).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * @param logMessage
     *            syslog message.
     * @return JSONObject
     *           return json object of syslog.
     * @throws JSONException
     *        throws org.codehaus.jettison.json.JSONException.
     */
    public JSONObject parseLogMessage(String logMessage) throws JSONException {
        JSONObject jsonLogEvent = new JSONObject();
        int cursorPosition = 0;
        Preconditions.checkArgument(logMessage.charAt(cursorPosition) == '<',
                "Bad format: invalid priority: cannot find open bracket '<' (%s)", logMessage);

        int endBracPosition = logMessage.indexOf('>');
        Preconditions.checkArgument(endBracPosition > 0 && endBracPosition <= 6,
                "Bad format: invalid priority: cannot find end bracket '>' (%s)", logMessage);
        String priority = logMessage.substring(1, endBracPosition);
        int priIntValue = Integer.parseInt(priority);
        int facility = priIntValue / 8;
        int severity = priIntValue % 8;
        // saving priority and facility into JSONObject
        jsonLogEvent.put("facility", String.valueOf(facility));
        jsonLogEvent.put("severity", String.valueOf(severity));
        int msgLength = logMessage.length();
        Preconditions.checkArgument(msgLength > endBracPosition + 1, "Bad format: no data except priority (%s)",
                logMessage);
        cursorPosition = endBracPosition + 1;
        if (msgLength > cursorPosition + 2 && "1 ".equals(logMessage.substring(cursorPosition, cursorPosition + 2))) {
            cursorPosition += 2;
        }
        // parsing timestamp and handling different timestamp formats
        long timeStamp;
        char chardateStart = logMessage.charAt(cursorPosition);
        try {
            // when no timestamp is specified relay current time is used
            if (chardateStart == '-') {
                timeStamp = System.currentTimeMillis();
                if (msgLength <= cursorPosition + 2) {
                    log.error("bad syslog format (missing hostname).Log string :- " + logMessage);
                    throw new IllegalArgumentException("bad syslog format (missing hostname)");
                }
                cursorPosition += 2;
            } else if (chardateStart >= 'A' && chardateStart <= 'Z') {
                if (msgLength <= cursorPosition + RFC3164_LENGTH) {
                    log.error("bad timestamp format " + logMessage);
                    throw new IllegalArgumentException("bad timestamp format");
                }
                timeStamp = rfc3164TimeStamp(logMessage.substring(cursorPosition, cursorPosition + RFC3164_LENGTH));
                cursorPosition += RFC3164_LENGTH + 1;
            } else {
                int nextSpaceIndex = logMessage.indexOf(' ', cursorPosition);
                if (nextSpaceIndex == -1) {
                    log.error("bad timestamp format " + logMessage);
                    throw new IllegalArgumentException("bad timestamp format");
                }
                timeStamp = rfc5424DateTime(logMessage.substring(cursorPosition, nextSpaceIndex));
                cursorPosition = nextSpaceIndex + 1;
            }
        } catch (IllegalArgumentException ex) {
            log.error("Unable to parse message: " + logMessage);
            throw new IllegalArgumentException("Unable to parse message: " + logMessage, ex);
        }
        jsonLogEvent.put("log_time", String.valueOf(timeStamp));
        int nextSpaceIndex = logMessage.indexOf(' ', cursorPosition);
        if (nextSpaceIndex == -1) {
            throw new IllegalArgumentException("bad syslog format (missing hostname)");
        }
        String hostname = new String(logMessage.substring(cursorPosition, nextSpaceIndex));
        jsonLogEvent.put("hostname", hostname);
        String messageData = "";
        if (msgLength > nextSpaceIndex + 1) {
            cursorPosition = nextSpaceIndex + 1;
            messageData = logMessage.substring(cursorPosition);
        } else {

            messageData = logMessage;
        }
        jsonLogEvent.put("message", messageData);
        return jsonLogEvent;
    }

    /**
     * @param timeStamp
     *            timestamp.
     * @return long
     *           return rfc3164TimeStamp.
     */
    protected long rfc3164TimeStamp(String timeStamp) {
        DateTime currentDateTime = DateTime.now();
        int yearCurrent = currentDateTime.getYear();
        timeStamp = SPACES_TWO_CHECK.matcher(timeStamp).replaceFirst(" ");
        DateTime dateReturned;
        try {
            dateReturned = RFC3164_FORMAT_DATETIME.parseDateTime(timeStamp);
        } catch (IllegalArgumentException e) {
            log.error("rfc3164 date parse failed on (" + timeStamp + "): invalid format", e);
            return 0;
        }
        if (dateReturned != null) {
            DateTime fixedDate = dateReturned.withYear(yearCurrent);
            if (fixedDate.isAfter(currentDateTime) && fixedDate.minusMonths(1).isAfter(currentDateTime)) {
                fixedDate = dateReturned.withYear(yearCurrent - 1);
            } else if (fixedDate.isBefore(currentDateTime) && fixedDate.plusMonths(1).isBefore(currentDateTime)) {
                fixedDate = dateReturned.withYear(yearCurrent + 1);
            }
            dateReturned = fixedDate;
        }
        if (dateReturned == null) {
            return 0;
        }
        return dateReturned.getMillis();
    }

    /**
     * @param message
     *            timestamp.
     * @return long
     *           return rfc5424TimeStamp.
     */
    protected long rfc5424DateTime(String message) {
        int msgLength = message.length();
        int curPosition = 0;
        Long timeStamp = null;
        Preconditions.checkArgument(msgLength > RFC5424_LEN_PREFIX, "Bad format: Not a valid RFC5424 timestamp: %s",
                message);
        String timeStampPrefix = message.substring(curPosition, RFC5424_LEN_PREFIX);
        timeStamp = timeParser.parseMillis(timeStampPrefix);
        curPosition += RFC5424_LEN_PREFIX;
        Preconditions.checkArgument(timeStamp != null, "Parsing error: timestamp is null");
        if (message.charAt(curPosition) == '.') {
            boolean endFound = false;
            int endMillisPosition = curPosition + 1;
            if (msgLength <= endMillisPosition) {
                throw new IllegalArgumentException("bad timestamp format (no TZ)");
            }
            while (!endFound) {
                char curDigit = message.charAt(endMillisPosition);
                if (curDigit >= '0' && curDigit <= '9') {
                    endMillisPosition++;
                } else {
                    endFound = true;
                }
            }
            if (endMillisPosition - (curPosition + 1) > 0) {
                float frac = Float.parseFloat(message.substring(curPosition, endMillisPosition));
                long milliseconds = (long) (frac * 1000f);
                timeStamp += milliseconds;
            } else {
                throw new IllegalArgumentException("Bad format: Invalid timestamp (fractional portion): " + message);
            }
            curPosition = endMillisPosition;
        }
        char timeZoneFirst = message.charAt(curPosition);
        if (timeZoneFirst != 'Z') {
            if (timeZoneFirst == '+' || timeZoneFirst == '-') {
                Preconditions.checkArgument(msgLength > curPosition + 5, "Bad format: Invalid timezone (%s)", message);
                int polarity;
                if (timeZoneFirst == '+') {
                    polarity = +1;
                } else {
                    polarity = -1;
                }
                char[] charAtPos = new char[5];
                for (int i = 0; i < 5; i++) {
                    charAtPos[i] = message.charAt(curPosition + 1 + i);
                }
                if (charAtPos[0] >= '0' && charAtPos[0] <= '9' && charAtPos[1] >= '0' && charAtPos[1] <= '9'
                        && charAtPos[2] == ':' && charAtPos[3] >= '0' && charAtPos[3] <= '9' && charAtPos[4] >= '0'
                        && charAtPos[4] <= '9') {
                    int hourOffset = Integer.parseInt(message.substring(curPosition + 1, curPosition + 3));
                    int minOffset = Integer.parseInt(message.substring(curPosition + 4, curPosition + 6));
                    timeStamp = timeStamp - polarity * ((hourOffset * 60) + minOffset) * 60000;
                } else {
                    throw new IllegalArgumentException("Bad format: Invalid timezone: " + message);
                }
            }
        }
        return timeStamp;
    }

}

/*
 * @author Sumit kapoor This class will provide access point to the common
 * services for handling KAFKA cluster.
 */
class StreamCallBack implements Callback {
    private static final Logger log = LoggerFactory.getLogger(StreamCallBack.class);
    private final long startTime;
    private final Long key;
    private final String message;

    public StreamCallBack(long startTime, Long key, String message) {
        this.startTime = startTime;
        this.key = key;
        this.message = message;
    }

    /**
     * A callback method the user can implement to provide asynchronous handling
     * of request completion. This method will be called when the record sent to
     * the server has been acknowledged. Exactly one of the arguments will be
     * non-null.
     *
     * @param metadata
     *            The metadata for the record that was sent (i.e. the partition
     *            and offset). Null if an error occurred.
     * @param exception
     *            The exception thrown during processing of this record. Null if
     *            no error occurred.
     */
    @Override
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (metadata != null) {
            log.error(exception.getLocalizedMessage(), exception);

        }
    }
}