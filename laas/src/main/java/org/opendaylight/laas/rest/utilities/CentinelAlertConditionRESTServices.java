/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.laas.rest.utilities;

import javax.json.JsonObject;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldcontentrulerecord.StreamAlertFieldContentRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldcontentrulerecord.StreamAlertFieldContentRuleListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldvaluerulerecord.StreamAlertFieldValueRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldvaluerulerecord.StreamAlertFieldValueRuleListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertmessagecountrulerecord.StreamAlertMessageCountRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertmessagecountrulerecord.StreamAlertMessageCountRuleListBuilder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Abhishek Sharma This class will provide AlertCondition specific
 *         services of GrayLog erver to the Centinel
 */
public final class CentinelAlertConditionRESTServices extends CentinelCommonRESTServices {

    private static CentinelAlertConditionRESTServices singleton = null;

    private static final Logger LOG = LoggerFactory.getLogger(CentinelAlertConditionRESTServices.class);
    String graylogAlertConditions = properties.getProperty("graylog_alertconditions");
    String graylogAlertCondition = properties.getProperty("graylog_alertcondition");
    String graylogAlertConditionId = properties.getProperty("graylog_alertconditionId");
    private static final String PARAMTERS = "parameters";
    private static final String GRACE = "grace";
    private static final String BACKLOG = "backlog";

    private CentinelAlertConditionRESTServices() {
        super();
    }

    public static synchronized CentinelAlertConditionRESTServices getInstance() {

        if (singleton == null) {
            singleton = new CentinelAlertConditionRESTServices();
        }
        return singleton;

    }

    public DataObject createFromConfigToOperational(DataObject dataObjectRuleList) {

        String streamId = "";
        DataObject newDataObjectRuleList;

        if (dataObjectRuleList instanceof StreamAlertMessageCountRuleList) {
            streamId = ((StreamAlertMessageCountRuleList) dataObjectRuleList).getStreamID();
        } else if (dataObjectRuleList instanceof StreamAlertFieldContentRuleList) {
            streamId = ((StreamAlertFieldContentRuleList) dataObjectRuleList).getStreamID();
        } else if (dataObjectRuleList instanceof StreamAlertFieldValueRuleList) {
            streamId = ((StreamAlertFieldValueRuleList) dataObjectRuleList).getStreamID();
        }

        JsonObject configJson = objectToJsonMapper(dataObjectRuleList);
        ClientResponse alertResponse = graylogRESTPost(configJson, graylogServerIp + graylogStream + streamId
                + graylogAlertCondition);

        if (alertResponse.getStatus() != 201) {
            newDataObjectRuleList = null;
            LOG.info("Problem at Graylog while creating an alert condition with Response Code: "
                    + alertResponse.getStatus() + " with message : "
                    + fetchResponse(alertResponse.getEntity(String.class), message));
        } else {
            newDataObjectRuleList = jsonToObject(alertResponse, dataObjectRuleList, streamId);
            LOG.info("Alert condition created successfully");
        }
        return newDataObjectRuleList;
    }

    public boolean deleteFromOperational(DataObject dataObj) {
        boolean deletedSuccessfully = false;
        ClientResponse alertResponse = null;
        if (dataObj instanceof StreamAlertMessageCountRuleList) {
            StreamAlertMessageCountRuleList alertMessageCountRuleList = (StreamAlertMessageCountRuleList) dataObj;
            alertResponse = graylogRESTDelete(graylogServerIp + graylogStream + alertMessageCountRuleList.getStreamID()
                    + graylogAlertConditions + alertMessageCountRuleList.getRuleID());
        } else if (dataObj instanceof StreamAlertFieldContentRuleList) {
            StreamAlertFieldContentRuleList alertFieldContentRuleList = (StreamAlertFieldContentRuleList) dataObj;
            alertResponse = graylogRESTDelete(graylogServerIp + graylogStream + alertFieldContentRuleList.getStreamID()
                    + graylogAlertConditions + alertFieldContentRuleList.getRuleID());
        } else if (dataObj instanceof StreamAlertFieldValueRuleList) {
            StreamAlertFieldValueRuleList alertFieldValueRuleList = (StreamAlertFieldValueRuleList) dataObj;
            alertResponse = graylogRESTDelete(graylogServerIp + graylogStream + alertFieldValueRuleList.getStreamID()
                    + graylogAlertConditions + alertFieldValueRuleList.getRuleID());
        }

        if (alertResponse.getStatus() != 204 || alertResponse == null) {
            LOG.info("Problem at Graylog while deleting an alert condition with Response Code: "
                    + alertResponse.getStatus() + " with message : "
                    + fetchResponse(alertResponse.getEntity(String.class), message));
        } else {
            LOG.info("Alert condition deleted successfully ");
            deletedSuccessfully = true;
        }
        return deletedSuccessfully;
    }

    public boolean updateToOperational(DataObject dataObj) {

        boolean isUpdated = false;
        JsonObject configJson = null;
        ClientResponse alertResponse = null;

        if (dataObj instanceof StreamAlertMessageCountRuleList) {
            StreamAlertMessageCountRuleList alertMessageCountRuleList = (StreamAlertMessageCountRuleList) dataObj;
            configJson = objectToJsonMapper(alertMessageCountRuleList);
            alertResponse = graylogRESTPut(configJson,
                    graylogServerIp + graylogStream + alertMessageCountRuleList.getStreamID() + graylogAlertConditions
                            + alertMessageCountRuleList.getRuleID());

        } else if (dataObj instanceof StreamAlertFieldContentRuleList) {
            StreamAlertFieldContentRuleList alertFieldContentRuleList = (StreamAlertFieldContentRuleList) dataObj;
            configJson = objectToJsonMapper(alertFieldContentRuleList);
            alertResponse = graylogRESTPut(configJson,
                    graylogServerIp + graylogStream + alertFieldContentRuleList.getStreamID() + graylogAlertConditions
                            + alertFieldContentRuleList.getRuleID());
        } else if (dataObj instanceof StreamAlertFieldValueRuleList) {
            StreamAlertFieldValueRuleList alertFieldValueRuleList = (StreamAlertFieldValueRuleList) dataObj;
            configJson = objectToJsonMapper(alertFieldValueRuleList);
            alertResponse = graylogRESTPut(configJson,
                    graylogServerIp + graylogStream + alertFieldValueRuleList.getStreamID() + graylogAlertConditions
                            + alertFieldValueRuleList.getRuleID());
        }

        if (alertResponse.getStatus() != 204 || alertResponse == null) {
            LOG.info("Problem at Graylog while updating an alert condition with Response Code: "
                    + alertResponse.getStatus() + " with message : "
                    + fetchResponse(alertResponse.getEntity(String.class), message));
        } else {
            isUpdated = true;
            LOG.info("Alert condition updated successfully ");
        }

        return isUpdated;
    }

    private JsonObject objectToJsonMapper(DataObject dataObject) {

        JsonObject setAlertJsonObject = null;

        if (dataObject instanceof StreamAlertMessageCountRuleList) {

            StreamAlertMessageCountRuleList messageCountDataObject = (StreamAlertMessageCountRuleList) dataObject;
            if (messageCountDataObject.getMessageCountGrace() != null
                    && messageCountDataObject.getMessageCountCount() != null
                    && messageCountDataObject.getMessageCountOperator() != null
                    && messageCountDataObject.getMessageCountBacklog() != null
                    && messageCountDataObject.getTimeStamp() != null) {
                setAlertJsonObject = factory
                        .createObjectBuilder()
                        .add(properties.getProperty("type"), "message_count")
                        .add(properties.getProperty(PARAMTERS),
                                factory.createObjectBuilder()
                                        .add(properties.getProperty(GRACE),
                                                messageCountDataObject.getMessageCountGrace())
                                        .add(properties.getProperty("threshold"),
                                                messageCountDataObject.getMessageCountCount())
                                        .add(properties.getProperty("thresholdType"),
                                                messageCountDataObject.getMessageCountOperator())
                                        .add(properties.getProperty(BACKLOG),
                                                messageCountDataObject.getMessageCountBacklog())
                                        .add(properties.getProperty("time"), messageCountDataObject.getTimeStamp()))
                        .build();
            }
        }

        else if (dataObject instanceof StreamAlertFieldContentRuleList) {

            StreamAlertFieldContentRuleList fieldContentDataObject = (StreamAlertFieldContentRuleList) dataObject;
            if (fieldContentDataObject.getFieldContentGrace() != null
                    && fieldContentDataObject.getFieldContentBacklog() != null
                    && fieldContentDataObject.getFieldContentField() != null
                    && fieldContentDataObject.getFieldContentCompareToValue() != null) {
                setAlertJsonObject = factory
                        .createObjectBuilder()
                        .add(properties.getProperty("type"), "field_content_value")
                        .add(properties.getProperty(PARAMTERS),
                                factory.createObjectBuilder()
                                        .add(properties.getProperty(GRACE),
                                                fieldContentDataObject.getFieldContentGrace())
                                        .add(properties.getProperty(BACKLOG),
                                                fieldContentDataObject.getFieldContentBacklog())
                                        .add(properties.getProperty("field"),
                                                fieldContentDataObject.getFieldContentField())
                                        .add(properties.getProperty("value"),
                                                fieldContentDataObject.getFieldContentCompareToValue())).build();
            }

        } else if (dataObject instanceof StreamAlertFieldValueRuleList) {
            StreamAlertFieldValueRuleList fieldValueDataObject = (StreamAlertFieldValueRuleList) dataObject;
            if (fieldValueDataObject.getFieldValueBacklog() != null
                    && fieldValueDataObject.getFieldValueGrace() != null
                    && fieldValueDataObject.getFieldValueField() != null
                    && fieldValueDataObject.getFieldValueThreshhold() != null
                    && fieldValueDataObject.getFieldValueThreshholdType() != null
                    && fieldValueDataObject.getTimeStamp() != null && fieldValueDataObject.getFieldValueType() != null) {
                setAlertJsonObject = factory
                        .createObjectBuilder()
                        .add(properties.getProperty("type"), "field_value")
                        .add(properties.getProperty(PARAMTERS),
                                factory.createObjectBuilder()
                                        .add(properties.getProperty(BACKLOG),
                                                fieldValueDataObject.getFieldValueBacklog())
                                        .add(properties.getProperty("field"), fieldValueDataObject.getFieldValueField())
                                        .add(properties.getProperty(GRACE), fieldValueDataObject.getFieldValueGrace())
                                        .add(properties.getProperty("threshold"),
                                                fieldValueDataObject.getFieldValueThreshhold())
                                        .add(properties.getProperty("thresholdType"),
                                                fieldValueDataObject.getFieldValueThreshholdType())
                                        .add(properties.getProperty("time"), fieldValueDataObject.getTimeStamp())
                                        .add(properties.getProperty("type"),
                                                properties.getProperty(fieldValueDataObject.getFieldValueType()
                                                        .replaceAll("\\s", "")))).build();

            }
        }
        return setAlertJsonObject;
    }

    private DataObject jsonToObject(ClientResponse createStreamResponse, DataObject dataObjectRuleList, String streamId) {

        DataObject operationalDataObject = null;
        String alertId = fetchResponse(createStreamResponse.getEntity(String.class), graylogAlertConditionId);

        if (dataObjectRuleList instanceof StreamAlertMessageCountRuleList) {
            StreamAlertMessageCountRuleList messageCountDataObject = (StreamAlertMessageCountRuleList) dataObjectRuleList;
            operationalDataObject = new StreamAlertMessageCountRuleListBuilder()
                    .setConfigID(messageCountDataObject.getConfigID())
                    .setAlertTypeClassifier(messageCountDataObject.getAlertTypeClassifier())
                    .setMessageCountBacklog(messageCountDataObject.getMessageCountBacklog())
                    .setMessageCountGrace(messageCountDataObject.getMessageCountGrace())
                    .setMessageCountOperator(messageCountDataObject.getMessageCountOperator()).setRuleID(alertId)
                    .setAlertName(messageCountDataObject.getAlertName())
                    .setRuleTypeClassifier(messageCountDataObject.getRuleTypeClassifier())
                    .setNodeType(messageCountDataObject.getNodeType())
                    .setMessageCountCount(messageCountDataObject.getMessageCountCount())
                    .setMessageCountOperator(messageCountDataObject.getMessageCountOperator())
                    .setTimeStamp(messageCountDataObject.getTimeStamp()).setStreamID(streamId).build();

        } else if (dataObjectRuleList instanceof StreamAlertFieldContentRuleList) {

            StreamAlertFieldContentRuleList fieldContentDataObject = (StreamAlertFieldContentRuleList) dataObjectRuleList;
            operationalDataObject = new StreamAlertFieldContentRuleListBuilder()
                    .setConfigID(fieldContentDataObject.getConfigID())
                    .setRuleTypeClassifier(fieldContentDataObject.getRuleTypeClassifier())
                    .setNodeType(fieldContentDataObject.getNodeType())
                    .setAlertTypeClassifier(fieldContentDataObject.getAlertTypeClassifier())
                    .setFieldContentBacklog(fieldContentDataObject.getFieldContentBacklog())
                    .setFieldContentGrace(fieldContentDataObject.getFieldContentGrace())
                    .setFieldContentCompareToValue(fieldContentDataObject.getFieldContentCompareToValue())
                    .setRuleID(alertId).setFieldContentField(fieldContentDataObject.getFieldContentField())
                    .setAlertName(fieldContentDataObject.getAlertName())
                    .setTimeStamp(fieldContentDataObject.getTimeStamp()).setStreamID(streamId).build();

        } else if (dataObjectRuleList instanceof StreamAlertFieldValueRuleList) {

            StreamAlertFieldValueRuleList fieldValueDataObject = (StreamAlertFieldValueRuleList) dataObjectRuleList;
            operationalDataObject = new StreamAlertFieldValueRuleListBuilder()
                    .setConfigID(fieldValueDataObject.getConfigID())
                    .setAlertTypeClassifier(fieldValueDataObject.getAlertTypeClassifier())
                    .setFieldValueGrace(fieldValueDataObject.getFieldValueGrace())
                    .setTimeStamp(fieldValueDataObject.getTimeStamp())
                    .setFieldValueBacklog(fieldValueDataObject.getFieldValueBacklog())
                    .setNodeType(fieldValueDataObject.getNodeType())
                    .setRuleTypeClassifier(fieldValueDataObject.getRuleTypeClassifier())
                    .setFieldValueField(fieldValueDataObject.getFieldValueField())
                    .setFieldValueThreshhold(fieldValueDataObject.getFieldValueThreshhold())
                    .setFieldValueThreshholdType(fieldValueDataObject.getFieldValueThreshholdType())
                    .setFieldValueType(fieldValueDataObject.getFieldValueType()).setRuleID(alertId)
                    .setAlertName(fieldValueDataObject.getAlertName())
                    .setTimeStamp(fieldValueDataObject.getTimeStamp()).setStreamID(streamId).build();

        }
        return operationalDataObject;
    }

}