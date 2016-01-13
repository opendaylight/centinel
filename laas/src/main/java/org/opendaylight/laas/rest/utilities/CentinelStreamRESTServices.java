/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.laas.rest.utilities;

import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamRule;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.streamrecord.StreamList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.streamrecord.StreamListBuilder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;

public class CentinelStreamRESTServices extends CentinelCommonRESTServices {

    private static final Logger LOG = LoggerFactory.getLogger(CentinelStreamRESTServices.class);
    private static CentinelStreamRESTServices singleton = null;
    private static final String STREAM_ID = "stream_id";
    private static String outputPluginId = "";
    String graylogStreamRules = properties.getProperty("graylog_streamrules");
    String graylogStreamPause = properties.getProperty("graylog_pause_stream");
    String graylogStreamResume = properties.getProperty("graylog_resume_stream");
    String stream = properties.getProperty("graylog_streams");
    boolean outputExists = false;

    private CentinelStreamRESTServices() {
        super();
    }

    /*
     * Provide single instance of CentinelStreamConditionRESTServices across the
     * application throughout its life cycle
     */
    public static synchronized CentinelStreamRESTServices getInstance() {

        if (singleton == null) {
            singleton = new CentinelStreamRESTServices();
        }
        return singleton;

    }

    /*
     * to update in operational data store. hits greylog rpc to create stream.
     */
    public DataObject createFromConfigToOperationalStream(DataObject dataObjectRuleList) {

        DataObject newDataObjectRuleList;

        JsonObject configJson = objectToJsonMapperStream(dataObjectRuleList);
        ClientResponse streamResponse = graylogRESTPost(configJson, graylogServerIp + stream);

        if (streamResponse.getStatus() != 201) {
            newDataObjectRuleList = null;
            LOG.info("Problem at Graylog while creating an stream condition with Response Code: "
                    + streamResponse.getStatus() + " with message : "
                    + fetchResponse(streamResponse.getEntity(String.class), message));
        } else {

            newDataObjectRuleList = jsonToObjectStream(streamResponse, dataObjectRuleList);
            LOG.info("stream condition created successfully");
        }

        ClientResponse outputResponse = graylogRESTGet(graylogServerIp + "system/outputs");
        LOG.info("graylog outputs received status: " + outputResponse.getStatus());
        JsonReader reader = Json.createReader(new StringReader(outputResponse.getEntity(String.class)));
        JsonObject streamOutputJsonObject = reader.readObject();
        reader.close();
        JsonArray outResponse = streamOutputJsonObject.getJsonArray("outputs");
        LOG.info("outResponse: " + outResponse);

        if (outResponse.isEmpty() || !outputTypeExists(outResponse)) {
            outputPluginId = createSystemOutput(streamResponse);
            LOG.info("Output created with ID: " + outputPluginId);
        } else {
            for (int i = 0; i < outResponse.toArray().length; i++) {
                if ("\"org.opendaylight.centinel.output.CentinelOutput\"".equals(outResponse.getJsonObject(i)
                        .get("type").toString())) {
                    outputPluginId = outResponse.getJsonObject(i).get("id").toString().replace("\"", "");
                    LOG.info("Output already exists OutputId: " + outputPluginId);
                    break;
                }
            }
        }

        return newDataObjectRuleList;

    }

    private boolean outputTypeExists(JsonArray outResponse) {
        boolean isExists = false;
        for (int i = 0; i < outResponse.toArray().length; i++) {
            if ("\"org.opendaylight.centinel.output.CentinelOutput\"".equals(outResponse.getJsonObject(i).get("type")
                    .toString())) {
                isExists = true;
                break;
            }
        }
        return isExists;
    }

    /**
     * @param dataObj
     * @return boolean returns true if successfully deleted the object from
     *         Analysis server
     */
    public boolean deleteFromOperationalStream(DataObject dataObj) {
        boolean deletedSuccessfully = false;
        ClientResponse streamResponse = null;
        if (dataObj instanceof StreamList) {
            StreamList streamList = (StreamList) dataObj;
            streamResponse = graylogRESTDelete(graylogServerIp + graylogStream + streamList.getStreamID());
        }

        if (streamResponse.getStatus() != 204 || streamResponse == null) {
            LOG.info("Problem at Graylog while deleting stream with Response Code: " + streamResponse.getStatus()
                    + " with message : " + fetchResponse(streamResponse.getEntity(String.class), message));
        } else {
            LOG.info("Stream condition deleted successfully ");
            deletedSuccessfully = true;
        }
        return deletedSuccessfully;
    }

    /*
     * @param dataObj DataObject
     * 
     * @return boolean returns true if Analysis object successfully updated
     */
    public boolean updateToOperationalStream(DataObject dataObj) {

        boolean isUpdated = false;
        JsonObject configJson = null;
        ClientResponse streamResponse = null;

        if (dataObj instanceof StreamList) {
            StreamList streamList = (StreamList) dataObj;
            configJson = objectToJsonMapperStream(streamList);
            streamResponse = graylogRESTPut(configJson, graylogServerIp + graylogStream + streamList.getStreamID());

        }

        if (streamResponse.getStatus() != 200 || streamResponse == null) {
            LOG.info("Problem at Graylog while updating stream condition with Response Code: "
                    + streamResponse.getStatus() + " with message : "
                    + fetchResponse(streamResponse.getEntity(String.class), message));
        } else {
            isUpdated = true;
            LOG.info("Stream condition updated successfully ");
        }

        return isUpdated;
    }

    /*
     * @param dataObject DataObject
     * 
     * @return setStreamJsonObject returns json to hit greylog rpc.
     */
    private JsonObject objectToJsonMapperStream(DataObject dataObject) {

        JsonObject setStreamJsonObject = null;

        if (dataObject instanceof StreamList) {

            StreamList streamObject = (StreamList) dataObject;
            if (streamObject.getTitle() != null && streamObject.getDescription() != null) {
                setStreamJsonObject = factory.createObjectBuilder()
                        .add(properties.getProperty("title"), streamObject.getTitle())
                        .add(properties.getProperty("description"), streamObject.getDescription()).build();
            }
        }

        LOG.info("JSON for GrayLog" + setStreamJsonObject);
        return setStreamJsonObject;
    }

    /*
     * @param createStreamResponse ClientResponse
     * 
     * @param dataObjectRuleList DataObject
     * 
     * @return operationalDataObject returns json to hit greylog rpc.
     */
    private DataObject jsonToObjectStream(ClientResponse createStreamResponse, DataObject dataObjectRuleList) {

        DataObject operationalDataObject = null;
        String streamId = fetchResponse(createStreamResponse.getEntity(String.class), STREAM_ID);

        if (dataObjectRuleList instanceof StreamList) {
            StreamList streamDataObject = (StreamList) dataObjectRuleList;
            operationalDataObject = new StreamListBuilder().setTitle(streamDataObject.getTitle())
                    .setDescription(streamDataObject.getDescription()).setStreamID(streamId)
                    .setDisabled(streamDataObject.getDisabled()).setConfigID(streamDataObject.getConfigID())
                    .setStreamRules(streamDataObject.getStreamRules()).setDisabled(streamDataObject.getDisabled())
                    .build();
        }
        LOG.info("DataObject for Operational Data Store : " + operationalDataObject);
        return operationalDataObject;
    }

    /*
     * @param dataObj DataObject
     * 
     * @param StreamId String
     * 
     * @return boolean returns true if Analysis object successfully updated
     */
    public boolean updateToOperationalStreamEnabler(DataObject dataObj, String StreamId) {
        boolean isUpdated = false;
        ClientResponse streamResponse = null;
        if (dataObj instanceof StreamList) {
            StreamList streamList = (StreamList) dataObj;
            if ("true".equalsIgnoreCase(streamList.getDisabled())) {
                streamResponse = graylogRESTPOSTEnabler(graylogServerIp + graylogStream + StreamId + graylogStreamPause);

            } else {
                streamResponse = graylogRESTPOSTEnabler(graylogServerIp + graylogStream + StreamId
                        + graylogStreamResume);
            }
        }

        if (streamResponse.getStatus() != 204 || streamResponse == null) {
            LOG.info("Problem at Graylog while updating stream condition with Response Code: "
                    + streamResponse.getStatus() + fetchResponse(streamResponse.getEntity(String.class), message));
        } else {
            isUpdated = true;
            LOG.info("Stream condition updated successfully ");
            createAlarmCallback(StreamId);
            createStreamOutput(StreamId, outputPluginId);
        }

        return isUpdated;
    }

    private void createAlarmCallback(String StreamId) {
        JsonObject alertCallbackJsonObject = null;
        ClientResponse alarmCallbackResponse = null;

        alertCallbackJsonObject = factory.createObjectBuilder()
                .add(properties.getProperty("type"), "org.opendaylight.centinel.alertcallback.CentinelAlertCallback")
                .add("configuration", factory.createObjectBuilder()).build();
        LOG.info("alertCallback JsonObject: " + alertCallbackJsonObject.toString());
        LOG.info("resource: " + graylogServerIp + graylogStream + StreamId + alarmCallback);
        try {
            alarmCallbackResponse = graylogRESTPost(alertCallbackJsonObject, graylogServerIp + graylogStream + StreamId
                    + alarmCallback);
            LOG.info("alarmCallbackResponse created with status: " + alarmCallbackResponse.getStatus());

            if (alarmCallbackResponse.getStatus() != 201 || alarmCallbackResponse == null) {
                LOG.info("Problem at Graylog while adding alarm callback on stream with Response Code: "
                        + alarmCallbackResponse.getStatus());
            } else {
                LOG.info("Alarm callback updated succesfully");
            }
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
    }

    private void createStreamOutput(String StreamId, String outputPluginId) {
        JsonObject streamOutputJsonObject = null;
        ClientResponse streamOutputResponse = null;

        streamOutputJsonObject = factory.createObjectBuilder()
                .add("outputs", factory.createArrayBuilder().add(outputPluginId)).build();
        LOG.info("streamOutput JsonObject: " + streamOutputJsonObject.toString());
        streamOutputResponse = graylogRESTPost(streamOutputJsonObject, graylogServerIp + graylogStream + StreamId
                + "/outputs");
        LOG.info("streamOutputResponse created with status: " + streamOutputResponse.getStatus());

        if (streamOutputResponse.getStatus() != 202 || streamOutputResponse == null) {
            LOG.info("Problem at Graylog while adding stream Output with Response Code: "
                    + streamOutputResponse.getStatus());
        } else {
            LOG.info("stream Output updated succesfully");
        }
    }

    private String createSystemOutput(ClientResponse streamResponse) {
        JsonObject messageOutputJsonObject = null;
        ClientResponse messageOutputResponse = null;
        String outputId = "";

        messageOutputJsonObject = factory.createObjectBuilder().add("configuration", factory.createObjectBuilder())
                .add("title", "centinel output")
                .add(properties.getProperty("type"), "org.opendaylight.centinel.output.CentinelOutput").build();
        messageOutputResponse = graylogRESTPost(messageOutputJsonObject, graylogServerIp + "system/outputs");
        LOG.info("messageOutputResponse created with status: " + messageOutputResponse.getStatus());

        if (messageOutputResponse.getStatus() != 201 || messageOutputResponse == null) {
            LOG.info("Problem at Graylog while adding output for stream with Response Code: "
                    + messageOutputResponse.getStatus());
        } else {
            LOG.info("Output updated succesfully");
            outputId = fetchResponse(messageOutputResponse.getEntity(String.class), "id");
        }
        return outputId;
    }

    /**
     * @param streamrules
     * @param string
     * @return boolean returns true if Analysis object successfully updated
     */
    public boolean updateToOperationalStreamRule(StreamRule streamrule, String streamId) {
        boolean isUpdated = false;
        JsonObject configJson = null;
        ClientResponse streamResponse = null;
        configJson = objectToJsonMapperStreamRule(streamrule);
        streamResponse = graylogRESTPost(configJson, graylogServerIp + graylogStream + streamId + graylogStreamRules);
        if (streamResponse.getStatus() != 201 || streamResponse == null) {
            LOG.info("Problem at Graylog while updating stream rules condition with Response Code: "
                    + streamResponse.getStatus());
        } else {
            isUpdated = true;
            LOG.info("Stream rule condition updated successfully" + streamResponse.getStatus());
        }

        return isUpdated;
    }

    private JsonObject objectToJsonMapperStreamRule(StreamRule streamRule) {

        JsonObject setStreamRuleJsonObject = null;
        LOG.info("STREAM RULE" + streamRule);

        if (streamRule.getType() != null && streamRule.getField() != null && streamRule.isInverted() != null) {
            int value = streamRule.getType().getIntValue() + 1;
            setStreamRuleJsonObject = factory.createObjectBuilder()
                    .add(properties.getProperty("field"), streamRule.getField())
                    .add(properties.getProperty("value"), streamRule.getValue())
                    .add(properties.getProperty("inverted"), streamRule.isInverted())
                    .add(properties.getProperty("type"), value).build();
        }

        LOG.info("JSON for GrayLog" + setStreamRuleJsonObject.toString());
        return setStreamRuleJsonObject;
    }

}
