/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.streamhandler.impl;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.EventBodyType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.PersistEventInputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class LogCollector {
    private static final Logger LOG = LoggerFactory.getLogger(LogCollector.class);
    CommonServices commonServices = CommonServices.getInstance();

    public void server() {
        ServerSocket s = null;
        Socket conn = null;
        try {
            s = new ServerSocket(Integer.parseInt(commonServices.syslogPort));
            conn = s.accept();
            new ClientHandler(conn).start();
        }

        catch (Exception e) {
            LOG.error("Exception while connecting " + e.getMessage(), e);
        }
        try {
            s.close();
        }

        catch (IOException ioException) {
            LOG.error("Unable to close. IOexception", ioException);
        }
    }

}

class ClientHandler extends Thread {
    private Socket conn;
    private static final int RFC3164_LENGTH = 15;
    private static final Pattern SPACES_TWO_CHECK = Pattern.compile("  ");
    private static final DateTimeFormatter rfc3164_FORMAT_DATETIME = DateTimeFormat.forPattern("MMM d HH:mm:ss")
            .withZoneUTC();
    private static final int RFC5424_LEN_PREFIX = 19;
    private Cache<String, Long> cacheTimestamp;
    JSONObject data = null;
    PersistEventInputBuilder input = null;
    StreamhandlerImpl streamHandlerImpl = new StreamhandlerImpl();
    private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);
    public static final Client client = Client.create();
    CommonServices commonServices = CommonServices.getInstance();

    ClientHandler(Socket conn) {
        this.conn = conn;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        String line;

        try {
            // get socket writing and reading streams
            DataInputStream in = new DataInputStream(conn.getInputStream());

            // Send welcome message to client
            LOG.info("Welcome to the Server");

            // Now start reading input from client
            input = new PersistEventInputBuilder();
            input.setEventBodyType(EventBodyType.Avro);
            List<String> keyList = new ArrayList<String>();
            keyList.add("log_timestamp");
            keyList.add("message");
            input.setEventKeys(keyList);
            input.setEventType("stringdata");
            while (true) {
                line = in.readLine();

                try {
                    try {
                        data = parseLogMessage(line);
                    } catch (JSONException e) {
                        LOG.error("Exception while converting getting JSON from parseLogMessage " + e.getMessage(), e);
                    }
                    // start Persisting Log event into Hbase

                    input.setEventBody(data.toString());
                    // end Persisting Log event into Hbase
                    Future<RpcResult<Void>> persistEvent = streamHandlerImpl.persistEvent(input.build());
                    // end Persisting Log event into Hbase
                    LOG.info("rpc result persisted in database: " + persistEvent.get().isSuccessful());
                    // if persisted successfully send it to graylog
                    if (persistEvent.get().isSuccessful()) {

                        WebResource webResource = client.resource(commonServices.graylogHostname + "gelf");
                        ClientResponse response = webResource.type("application/json").post(ClientResponse.class,
                                data.toString());
                        if (response.getStatus() == 202) {
                            LOG.info("Message sent successfully to graylog");
                        }
                    }
                } catch (Exception e) {
                    LOG.error("Unable to parse message: " + " Error Message > " + e.getMessage(), e);

                }

            }
        }

        catch (IOException e) {
            LOG.error("IOException on socket : " + e);
        }

    }

    public JSONObject parseLogMessage(String logMessage) throws JSONException {
        JSONObject jsonLogEvent = new JSONObject();
        int cursorPosition = 0;
        int msgLength = logMessage.length();
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
        jsonLogEvent.put("Facility", String.valueOf(facility));
        jsonLogEvent.put("Severity", String.valueOf(severity));
        Preconditions.checkArgument(msgLength > endBracPosition + 1, "Bad format: no data except priority (%s)",
                logMessage);
        cursorPosition = endBracPosition + 1;
        if (msgLength > cursorPosition + 2 && "1 ".equals(logMessage.substring(cursorPosition, cursorPosition + 2)))
            cursorPosition += 2;
        // parsing timestamp and handling different timestamp formats
        long timeStamp;
        char chardateStart = logMessage.charAt(cursorPosition);
        try {
            // when no timestamp is specified relay current time is used
            if (chardateStart == '-') {
                timeStamp = System.currentTimeMillis();
                if (msgLength <= cursorPosition + 2) {
                    LOG.error("bad syslog format (missing hostname).Log string :- " + logMessage);
                    throw new IllegalArgumentException("bad syslog format (missing hostname)");
                }
                cursorPosition += 2;
            } else if (chardateStart >= 'A' && chardateStart <= 'Z') {
                if (msgLength <= cursorPosition + RFC3164_LENGTH) {
                    LOG.error("bad timestamp format " + logMessage);
                    throw new IllegalArgumentException("bad timestamp format");
                }
                timeStamp = rfc3164TimeStamp(logMessage.substring(cursorPosition, cursorPosition + RFC3164_LENGTH));
                cursorPosition += RFC3164_LENGTH + 1;
            } else {
                int nextSpaceIndex = logMessage.indexOf(' ', cursorPosition);
                if (nextSpaceIndex == -1) {
                    LOG.error("bad timestamp format " + logMessage);
                    throw new IllegalArgumentException("bad timestamp format");
                }
                timeStamp = rfc5424DateTime(logMessage.substring(cursorPosition, nextSpaceIndex));
                cursorPosition = nextSpaceIndex + 1;
            }
        } catch (IllegalArgumentException ex) {
            LOG.error("Unable to parse message: " + logMessage);
            throw new IllegalArgumentException("Unable to parse message: " + logMessage, ex);
        }
        jsonLogEvent.put("log_timestamp", String.valueOf(timeStamp));
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

    // parsing RFC3164 timestamp
    protected long rfc3164TimeStamp(String timeStamp) {
        DateTime currentDateTime = DateTime.now();
        int yearCurrent = currentDateTime.getYear();
        timeStamp = SPACES_TWO_CHECK.matcher(timeStamp).replaceFirst(" ");
        DateTime dateReturned;
        try {
            dateReturned = rfc3164_FORMAT_DATETIME.parseDateTime(timeStamp);
        } catch (IllegalArgumentException e) {
            LOG.error("rfc3164 date parse failed on (" + timeStamp + "): invalid format", e);
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

    // parsing Rfc5424 timestamp
    protected long rfc5424DateTime(String message) {
        int msgLength = message.length();
        int curPosition = 0;
        Long timeStamp = null;
        Preconditions.checkArgument(msgLength > RFC5424_LEN_PREFIX, "Bad format: Not a valid RFC5424 timestamp: %s",
                message);
        String timeStampPrefix = message.substring(curPosition, RFC5424_LEN_PREFIX);
        try {
            timeStamp = cacheTimestamp.get(timeStampPrefix);
        } catch (ExecutionException ex) {
            throw new IllegalArgumentException("bad timestamp format", ex);
        }
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
