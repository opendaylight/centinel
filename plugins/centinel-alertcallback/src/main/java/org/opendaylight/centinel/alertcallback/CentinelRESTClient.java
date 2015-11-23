/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.alertcallback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;
import org.apache.commons.codec.binary.Base64;
import org.graylog2.plugin.MessageSummary;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.alarms.AlertCondition.CheckResult;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.graylog2.plugin.streams.Stream;
import org.graylog2.plugin.streams.StreamRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Monika Verma
 * 
 */
public class CentinelRESTClient {
    private static final Logger LOG = LoggerFactory.getLogger(CentinelRESTClient.class);

    private static CentinelRESTClient singleton = null;
    JsonBuilderFactory factory = null;
    private Properties properties;

    private CentinelRESTClient() {
        factory = Json.createBuilderFactory(null);
        loadPropertiesFile();
    }

    /**
     * Provide single instance of CentinelRestClient across the application
     * throughout its life cycle
     */
    public static synchronized CentinelRESTClient getInstance() {

        if (singleton == null) {
            singleton = new CentinelRESTClient();
        }
        return singleton;
    }

    /**
     * Triggers the alert callback to Centinel Rest Api
     */
    public void trigger(AlertCondition.CheckResult checkResult, Stream stream) throws AlarmCallbackException {

        TriggeredConditionPojo triggeredConditionPojo = new TriggeredConditionPojo(checkResult.getTriggeredCondition());
        Schema schemaTriggeredCond = ReflectData.get().getSchema(TriggeredConditionPojo.class);
        GenericRecord recordTriggeredCond = new Record(schemaTriggeredCond);
        recordTriggeredCond.put("alertConditionId", triggeredConditionPojo.getId());
        recordTriggeredCond.put("type", triggeredConditionPojo.getType());
        recordTriggeredCond.put("creatorUserId", triggeredConditionPojo.getCreatorUserId());
        recordTriggeredCond.put("createdAt", triggeredConditionPojo.getCreatedAt());
        recordTriggeredCond.put("grace", triggeredConditionPojo.getGrace());
        recordTriggeredCond.put("parameters", triggeredConditionPojo.getParameters());
        recordTriggeredCond.put("description", triggeredConditionPojo.getDescription());
        recordTriggeredCond.put("backlog", triggeredConditionPojo.getBacklog());

        Schema schemaMatchingMessage = ReflectData.get().getSchema(MatchingMessagePojo.class);
        GenericRecord recordMatchingMessage = new Record(schemaMatchingMessage);
        if (!checkResult.getMatchingMessages().isEmpty()) {
            MessageSummary messageSummary = checkResult.getMatchingMessages().get(0);
            recordMatchingMessage.put("index", messageSummary.getIndex());
            recordMatchingMessage.put("message", messageSummary.getMessage());
            recordMatchingMessage.put("fields", messageSummary.getFields());
            recordMatchingMessage.put("id", messageSummary.getId());
            recordMatchingMessage.put("source", messageSummary.getSource());
            recordMatchingMessage.put("timestamp", messageSummary.getTimestamp());
        }

        CheckResultPojo checkResultPojo = new CheckResultPojo(checkResult);
        Schema schemaCheckResult = ReflectData.get().getSchema(CheckResultPojo.class);
        GenericRecord avroRecordCheckResult = new Record(schemaCheckResult);

        avroRecordCheckResult.put("resultDescription", checkResultPojo.getResultDescription());
        avroRecordCheckResult.put("triggeredCondition", recordTriggeredCond);
        avroRecordCheckResult.put("triggeredAt", checkResultPojo.getTriggeredAt());
        avroRecordCheckResult.put("triggered", checkResultPojo.isTriggered());
        avroRecordCheckResult.put("matchingMessages", recordMatchingMessage);

        Schema schemaRules = ReflectData.get().getSchema(RulesPojo.class);
        GenericRecord recordRules = new Record(schemaRules);

        StreamPojo streamPojo = new StreamPojo(stream);

        StreamRule rule = streamPojo.getRules().get(0);
        recordRules.put("field", rule.getField());
        recordRules.put("streamId", rule.getStreamId());
        recordRules.put("id", rule.getId());
        recordRules.put("type", rule.getType().toString());
        recordRules.put("inverted", rule.getInverted());
        recordRules.put("value", rule.getValue());

        Schema schemaStream = ReflectData.get().getSchema(StreamPojo.class);
        GenericRecord avroRecordStream = new Record(schemaStream);

        avroRecordStream.put("creatoruserid", streamPojo.getCreatoruserid());
        avroRecordStream.put("matchingtype", streamPojo.getMatchingtype());
        avroRecordStream.put("description", streamPojo.getDescription());
        avroRecordStream.put("disabled", streamPojo.getDisabled());
        avroRecordStream.put("rules", recordRules);
        avroRecordStream.put("streamId", streamPojo.getId());
        avroRecordStream.put("title", streamPojo.getTitle());

        String eventBody = "{\"check_result\":" + avroRecordCheckResult + ",\"stream\":" + avroRecordStream + "}";

        JsonObject inputJson = null;
        inputJson = factory
                .createObjectBuilder()
                .add("input",
                        factory.createObjectBuilder()
                                .add("eventType", "alert")
                                .add("eventBodyType", "avro")
                                .add("eventBody", eventBody)
                                .add("eventKeys",
                                        factory.createArrayBuilder().add("triggeredAt")
                                                .add("check_result:resultDescription")
                                                .add("check_result:triggeredCondition:alertConditionId")
                                                .add("check_result:triggeredCondition:type")
                                                .add("check_result:triggeredCondition:description")
                                                .add("stream:streamId"))).build();

        final URL url;
        try {
            url = new URL("http://" + properties.getProperty("centinel_ip") + ":"
                    + properties.getProperty("centinel_port") + "/restconf/operations/eventinput:notify-event");
        } catch (MalformedURLException e) {
            throw new AlarmCallbackException("Error while constructing URL of Centinel API.", e);
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
            throw new AlarmCallbackException("Could not open connection to Rest API - Centinel", e);
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
     * Pojo for CheckResult - to map checkResult object to avro schema
     */
    public static class CheckResultPojo {
        private String resultDescription;
        private AlertCondition triggeredCondition;
        private String triggeredAt;
        private boolean triggered;
        private List<MessageSummary> matchingMessages;

        public CheckResultPojo(CheckResult checkResult) {
            this.resultDescription = checkResult.getResultDescription();
            this.triggeredCondition = checkResult.getTriggeredCondition();
            this.triggeredAt = checkResult.getTriggeredAt().toString();
            this.triggered = checkResult.isTriggered();
            this.matchingMessages = checkResult.getMatchingMessages();
        }

        public String getResultDescription() {
            return resultDescription;
        }

        public void setResultDescription(String resultDescription) {
            this.resultDescription = resultDescription;
        }

        public AlertCondition getTriggeredCondition() {
            return triggeredCondition;
        }

        public void setTriggeredCondition(AlertCondition triggeredCondition) {
            this.triggeredCondition = triggeredCondition;
        }

        public String getTriggeredAt() {
            return triggeredAt;
        }

        public void setTriggeredAt(String triggeredAt) {
            this.triggeredAt = triggeredAt;
        }

        public boolean isTriggered() {
            return triggered;
        }

        public void setTriggered(boolean triggered) {
            this.triggered = triggered;
        }

        public List<MessageSummary> getMatchingMessages() {
            return matchingMessages;
        }

        public void setMatchingMessages(List<MessageSummary> matchingMessages) {
            this.matchingMessages = matchingMessages;
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
     * Pojo for Triggered AlertCondition - to map triggered alertCondition
     * object to avro schema
     */
    public static class TriggeredConditionPojo {

        private String alertConditionId;
        private String type;
        private String creatorUserId;
        private String createdAt;
        private int grace;
        private Map<String, Object> parameters;
        private String description;
        private Integer backlog;

        public TriggeredConditionPojo(AlertCondition alertCondition) {
            this.alertConditionId = alertCondition.getId();
            this.type = alertCondition.getTypeString();
            this.creatorUserId = alertCondition.getCreatorUserId();
            this.createdAt = alertCondition.getCreatedAt().toString();
            this.grace = alertCondition.getGrace();
            this.parameters = alertCondition.getParameters();
            this.description = alertCondition.getDescription();
            this.backlog = alertCondition.getBacklog();
        }

        public String getId() {
            return alertConditionId;
        }

        public void setId(String id) {
            this.alertConditionId = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCreatorUserId() {
            return creatorUserId;
        }

        public void setCreatorUserId(String creatorUserId) {
            this.creatorUserId = creatorUserId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public int getGrace() {
            return grace;
        }

        public void setGrace(int grace) {
            this.grace = grace;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getBacklog() {
            return backlog;
        }

        public void setBacklog(Integer backlog) {
            this.backlog = backlog;
        }
    }

    /**
     * Pojo for StreamRule - to map streamRule object to avro schema
     */
    public static class RulesPojo {

        private String field;
        private String streamId;
        private String id;
        private String type;
        private Boolean inverted;
        private String value;

        public RulesPojo(StreamRule rule) {
            this.field = rule.getField();
            this.streamId = rule.getStreamId();
            this.id = rule.getId();
            this.type = rule.getType().name();
            this.inverted = rule.getInverted();
            this.value = rule.getValue();
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
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

    /**
     * Pojo for MatchingMessage - to map messageSummary object to avro schema
     */
    public static class MatchingMessagePojo {

        private String index;
        private String message;
        private Map<String, Object> fields;
        private String id;
        private String source;
        private String timestamp;

        public MatchingMessagePojo(MessageSummary messageSummary) {
            this.index = messageSummary.getIndex();
            this.message = messageSummary.getMessage();
            this.fields = messageSummary.getFields();
            this.id = messageSummary.getId();
            this.source = messageSummary.getSource();
            this.timestamp = messageSummary.getTimestamp().toString();
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, Object> getFields() {
            return fields;
        }

        public void setFields(Map<String, Object> fields) {
            this.fields = fields;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }

}