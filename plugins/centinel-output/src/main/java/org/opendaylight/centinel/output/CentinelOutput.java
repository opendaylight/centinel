/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.output;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;
import org.apache.commons.codec.binary.Base64;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.outputs.MessageOutput;
import org.graylog2.plugin.outputs.MessageOutputConfigurationException;
import org.graylog2.plugin.streams.Stream;
import org.graylog2.plugin.streams.StreamRule;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;

/**
 * @author Monika Verma
 * 
 *         This is the centinel-output plugin. It forwards the graylog stream
 *         events to centinel api
 * 
 */

public class CentinelOutput implements MessageOutput {

    private static final Logger LOG = LoggerFactory.getLogger(CentinelOutput.class);
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private AtomicBoolean disconnecting = new AtomicBoolean(false);
    private AtomicBoolean needReconnect = new AtomicBoolean(false);
    private Properties properties;

    private static String outputname = "Centinel Message Output";

    Runnable reconnectHandler = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    if (needReconnect.get()) {
                        reconnect();
                    }
                    Thread.sleep(5000);
                } catch (Exception e) {
                    LOG.error("centinel-new reconnect handler crashed! " + e.getMessage(), e);
                }
            }
        }
    };

    @Inject
    public CentinelOutput(@Assisted Stream stream, @Assisted Configuration configuration)
            throws MessageOutputConfigurationException {
        loadPropertiesFile();

        disconnecting.set(false);
        needReconnect.set(false);

        new Thread(reconnectHandler).start();
        isRunning.set(true);
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public void write(Message message) throws Exception {

        JsonObject inputJson = null;
        String body;

        try {
            Schema schemaStream = ReflectData.get().getSchema(StreamPojo.class);
            GenericRecord avroRecordStream = new Record(schemaStream);
            Schema schemaRules = ReflectData.get().getSchema(RulesPojo.class);
            GenericRecord recordRules = new Record(schemaRules);
            Stream stream = message.getStreams().get(0);
            StreamPojo streamPojo = new StreamPojo(stream);
            avroRecordStream.put("creatoruserid", streamPojo.getCreatoruserid());
            avroRecordStream.put("matchingtype", streamPojo.getMatchingtype());
            avroRecordStream.put("description", streamPojo.getDescription());
            avroRecordStream.put("disabled", streamPojo.getDisabled());

            StreamRule streamRule = stream.getStreamRules().get(0);
            RulesPojo rule = new RulesPojo(streamRule);
            recordRules.put("field", rule.getField());
            recordRules.put("streamId", rule.getStreamId());
            recordRules.put("ruleId", rule.getId());
            recordRules.put("type", rule.getType());
            recordRules.put("inverted", rule.getInverted());
            recordRules.put("value", rule.getValue());

            avroRecordStream.put("rules", recordRules);
            avroRecordStream.put("streamId", streamPojo.getId());
            avroRecordStream.put("title", streamPojo.getTitle());

            MessagePojo messagePojo = new MessagePojo(message);
            Schema schemaMatchingMessage = ReflectData.get().getSchema(MessagePojo.class);
            GenericRecord recordMatchingMessage = new Record(schemaMatchingMessage);

            recordMatchingMessage.put("fields", messagePojo.getFields());
            recordMatchingMessage.put("id", messagePojo.getId());
            recordMatchingMessage.put("hostName", messagePojo.getHostName());
            recordMatchingMessage.put("sourceInetAddress", messagePojo.isSourceInetAddress());
            recordMatchingMessage.put("journalOffset", messagePojo.getJournalOffset());
            recordMatchingMessage.put("message", messagePojo.getMessage());
            recordMatchingMessage.put("source", messagePojo.getSource());
            recordMatchingMessage.put("sourceInput", messagePojo.getSourceInput());
            recordMatchingMessage.put("streams", avroRecordStream);
            recordMatchingMessage.put("timestamp", messagePojo.getTimestamp());
            recordMatchingMessage.put("validErrors", messagePojo.getValidErrors());

            body = recordMatchingMessage.toString();
            body = body.replace("timestamp", "event_timestamp");

            String regex = "([0-9]{4})-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9.]{6})Z";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                String str = "\"" + matcher.group() + "\"";
                body = body.replaceAll(regex, str);
            }

        } catch (Exception e) {
            throw e;
        }

        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        inputJson = factory
                .createObjectBuilder()
                .add("input",
                        factory.createObjectBuilder()
                                .add("eventType", "stream")
                                .add("eventBodyType", "avro")
                                .add("eventBody", body)
                                .add("eventKeys",
                                        factory.createArrayBuilder().add("event_timestamp").add("streams:streamId")
                                                .add("fields:message").add("fields:bundle").add("fields:feature")
                                                .add("fields:source_node_type").add("fields:severity"))).build();

        final URL url;
        try {
            url = new URL("http://" + properties.getProperty("centinel_ip") + ":"
                    + properties.getProperty("centinel_port") + "/restconf/operations/eventinput:notify-event");
        } catch (MalformedURLException e) {
            throw new AlarmCallbackException("Error while constructing URL of Slack API.", e);
        }

        // Create authentication string and encode it to Base64
        final String admin = "admin";
        String authStr = admin + ":" + admin;
        String encodedAuthStr = Base64.encodeBase64String(authStr.getBytes());

        try {
            // Create Http connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set connection properties
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Basic " + encodedAuthStr);
            connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
            connection.setRequestProperty("content-type", MediaType.APPLICATION_JSON);

            // Send post request
            connection.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(inputJson.toString());
            out.flush();
            out.close();

            // Get the response from connection's inputStream
            connection.getInputStream();

        } catch (IOException e) {
            throw new AlarmCallbackException("Could not open connection to Centinel Rest API", e);
        }
    }

    @Override
    public void write(List<Message> messages) throws Exception {
        for (Message message : messages) {
            write(message);
        }
    }

    @Override
    public void stop() {
        LOG.info("Stopping Centinel output");

        disconnecting.set(true);
        isRunning.set(false);
    }

    private void reconnect() {
        try {
            LOG.info("Trying to reconnect to Centinel server...");
        } catch (Exception e) {
            LOG.error("Reconnect to Centinel server failed. " + e.getMessage(), e);
            return;
        }
        needReconnect.set(false);
    }

    public interface Factory extends MessageOutput.Factory<CentinelOutput> {
        @Override
        CentinelOutput create(Stream stream, Configuration configuration);

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    public static class Config extends MessageOutput.Config {
        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            return new ConfigurationRequest();
        }
    }

    public static class Descriptor extends MessageOutput.Descriptor {
        public Descriptor() {
            super(outputname, false, "", "An output plugin sending graylog events over to Centinel API");
        }
    }

    /**
     * To load property file
     */
    private void loadPropertiesFile() {
        properties = new Properties();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
            properties.load(inputStream);
            LOG.info("Properties file for Graylog loaded successfully");
        } catch (IOException e) {
            LOG.error("Problem while loading Properties file for Graylog " + e.getMessage(), e);
        }
    }

    /**
     * Pojo for Message - to map message object to avro schema
     */
    public static class MessagePojo {

        private Map<String, Object> fields;
        private String source;
        private String id;
        private String hostName;
        private boolean sourceInetAddress;
        private long journalOffset;
        private String message;
        private String sourceInput;
        private List<Stream> streams;
        private DateTime timestamp;
        private String validErrors;

        public MessagePojo(Message message) {
            this.fields = message.getFields();
            this.id = message.getId();
            this.hostName = message.getInetAddress().getHostName();
            this.sourceInetAddress = message.getIsSourceInetAddress();
            this.journalOffset = message.getJournalOffset();
            this.message = message.getMessage();
            this.source = message.getSource();
            this.sourceInput = message.getSourceInputId();
            this.streams = message.getStreams();
            this.timestamp = message.getTimestamp();
            this.validErrors = message.getValidationErrors();
        }

        public Map<String, Object> getFields() {
            return fields;
        }

        public void setFields(Map<String, Object> fields) {
            this.fields = fields;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getHostName() {
            return hostName;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

        public boolean isSourceInetAddress() {
            return sourceInetAddress;
        }

        public void setSourceInetAddress(boolean sourceInetAddress) {
            this.sourceInetAddress = sourceInetAddress;
        }

        public long getJournalOffset() {
            return journalOffset;
        }

        public void setJournalOffset(long journalOffset) {
            this.journalOffset = journalOffset;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSourceInput() {
            return sourceInput;
        }

        public void setSourceInput(String sourceInput) {
            this.sourceInput = sourceInput;
        }

        public List<Stream> getStreams() {
            return streams;
        }

        public void setStreams(List<Stream> streams) {
            this.streams = streams;
        }

        public DateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(DateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getValidErrors() {
            return validErrors;
        }

        public void setValidErrors(String validErrors) {
            this.validErrors = validErrors;
        }
    }

    /**
     * Pojo for Stream - to map stream object to avro schema
     */
    public static class StreamPojo {

        private String creatoruserid;
        private String matchingtype;
        private String description;
        private Boolean disabled;
        private List<StreamRule> rules;
        private String streamId;
        private String title;

        public StreamPojo(Stream stream) {
            this.creatoruserid = "admin";
            this.matchingtype = "AND";
            this.description = stream.getDescription();
            this.disabled = stream.getDisabled();
            this.rules = stream.getStreamRules();
            this.streamId = stream.getId();
            this.title = stream.getTitle();
        }

        public String getCreatoruserid() {
            return creatoruserid;
        }

        public void setCreatoruserid(String creatoruserid) {
            this.creatoruserid = creatoruserid;
        }

        public String getMatchingtype() {
            return matchingtype;
        }

        public void setMatchingtype(String matchingtype) {
            this.matchingtype = matchingtype;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getDisabled() {
            return disabled;
        }

        public void setDisabled(Boolean disabled) {
            this.disabled = disabled;
        }

        public List<StreamRule> getRules() {
            return rules;
        }

        public void setRules(List<StreamRule> rules) {
            this.rules = rules;
        }

        public String getId() {
            return streamId;
        }

        public void setId(String id) {
            this.streamId = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    /**
     * Pojo for StreamRule - to map streamRule object to avro schema
     */
    public static class RulesPojo {

        private String field;
        private String streamId;
        private String ruleId;
        private String type;
        private Boolean inverted;
        private String value;

        public RulesPojo(StreamRule streamRule) {
            this.field = streamRule.getField();
            this.streamId = streamRule.getStreamId();
            this.ruleId = streamRule.getId();
            this.type = streamRule.getType().toString();
            this.inverted = streamRule.getInverted();
            this.value = streamRule.getValue();
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getStreamId() {
            return streamId;
        }

        public void setStreamId(String streamId) {
            this.streamId = streamId;
        }

        public String getId() {
            return ruleId;
        }

        public void setId(String id) {
            this.ruleId = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Boolean getInverted() {
            return inverted;
        }

        public void setInverted(Boolean inverted) {
            this.inverted = inverted;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
