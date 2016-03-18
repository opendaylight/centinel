/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.alertcallback;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graylog2.plugin.MessageSummary;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.streams.Stream;
import org.graylog2.plugin.streams.StreamRule;
import org.graylog2.plugin.streams.StreamRuleType;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.centinel.alertcallback.CentinelRESTClient.MatchingMessagePojo;
import org.opendaylight.centinel.alertcallback.CentinelRESTClient.RulesPojo;
import org.opendaylight.centinel.alertcallback.CentinelRESTClient.StreamPojo;
import org.opendaylight.centinel.alertcallback.CentinelRESTClient.TriggeredConditionPojo;

public class CentinelRESTClientTest {

    StreamPojo mockStreamPojo = null;
    CentinelRESTClientFactory centinelRESTClientFactory = new CentinelRESTClientFactory();
    RulesPojo mockRulePojo = null;
    MatchingMessagePojo mockMatchingMessagePojo = null;
    TriggeredConditionPojo mockTriggeredConditionPojo = null;

    @Before
    public void init() {

    }

    @After
    public void destroy() {
        mockStreamPojo = null;
        centinelRESTClientFactory = null;
        mockRulePojo = null;
        mockMatchingMessagePojo = null;
        mockTriggeredConditionPojo = null;
    }

    @Test
    public void testStreamPojo() {
        Stream stream = mock(Stream.class);
        List<StreamRule> streamRules = mock(List.class);
        StreamRule streamRule = mock(StreamRule.class);
        doReturn(streamRule).when(streamRules).get(0);
        doReturn(StreamRuleType.EXACT).when(streamRule).getType();

        doReturn(streamRules).when(stream).getStreamRules();
        doReturn("Stream Description").when(stream).getDescription();
        doReturn(true).when(stream).getDisabled();
        doReturn("StreamId").when(stream).getId();
        doReturn("Stream Title").when(stream).getTitle();

        mockStreamPojo = new StreamPojo(stream);
        mockStreamPojo.setCreatoruserid(centinelRESTClientFactory.inputCreatoruserid());
        mockStreamPojo.getCreatoruserid();
        mockStreamPojo.setMatchingtype(centinelRESTClientFactory.inputMatchingtype());
        mockStreamPojo.getMatchingtype();
        mockStreamPojo.setDescription(centinelRESTClientFactory.inputDescription());
        mockStreamPojo.getDescription();
        mockStreamPojo.setDisabled(centinelRESTClientFactory.inputDisabled());
        mockStreamPojo.getDisabled();
        mockStreamPojo.setRules(streamRules);
        mockStreamPojo.getRules();
        mockStreamPojo.setId(centinelRESTClientFactory.inputId());
        mockStreamPojo.getId();
        mockStreamPojo.setTitle(centinelRESTClientFactory.inputTitle());
        mockStreamPojo.getTitle();

    }

    @Test
    public void testRulesPojo() {
        StreamRule streamRule = mock(StreamRule.class);
        doReturn("Stream").when(streamRule).getField();
        doReturn("1001").when(streamRule).getId();
        doReturn(true).when(streamRule).getInverted();
        doReturn("1001").when(streamRule).getStreamId();
        doReturn(StreamRuleType.EXACT).when(streamRule).getType();
        doReturn("Stream").when(streamRule).getValue();
        mockRulePojo = new RulesPojo(streamRule);
        mockRulePojo.setField(centinelRESTClientFactory.inputFields());
        mockRulePojo.setId(centinelRESTClientFactory.inputId());
        mockRulePojo.setInverted(centinelRESTClientFactory.inputInvertied());
        mockRulePojo.setStreamId(centinelRESTClientFactory.inputStreamId());
        mockRulePojo.setType(centinelRESTClientFactory.inputType());
        mockRulePojo.setValue(centinelRESTClientFactory.inputValue());
    }

    @Test
    public void testMatchingMessagePojo() {
        MessageSummary messageSummary = mock(MessageSummary.class);
        doReturn("1").when(messageSummary).getIndex();
        doReturn("Stream saved successfully").when(messageSummary).getMessage();
        doReturn("1001").when(messageSummary).getId();
        doReturn("network").when(messageSummary).getSource();
        doReturn(new DateTime()).when(messageSummary).getTimestamp();
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("1", "stream");
        doReturn(fields).when(messageSummary).getFields();
        mockMatchingMessagePojo = new MatchingMessagePojo(messageSummary);
        mockMatchingMessagePojo.setIndex(centinelRESTClientFactory.inputIndex());
        mockMatchingMessagePojo.setMessage(centinelRESTClientFactory.inputMessage());
        mockMatchingMessagePojo.setFields(centinelRESTClientFactory.inputFieldsMap());
        mockMatchingMessagePojo.setId(centinelRESTClientFactory.inputId());
        mockMatchingMessagePojo.setSource(centinelRESTClientFactory.inputSource());
        mockMatchingMessagePojo.setTimestamp(centinelRESTClientFactory.inputTimestamp());
    }

    @Test
    public void testTriggeredConditionPojo() {
        AlertCondition mockAlertCondition = mock(AlertCondition.class);
        doReturn("alertConditionId").when(mockAlertCondition).getId();
        doReturn("Message-Count").when(mockAlertCondition).getTypeString();
        doReturn("testUserId").when(mockAlertCondition).getCreatorUserId();
        doReturn(new DateTime()).when(mockAlertCondition).getCreatedAt();
        doReturn(0).when(mockAlertCondition).getGrace();
        doReturn(new HashMap<String, Object>()).when(mockAlertCondition).getParameters();
        doReturn("AlertCondition Description").when(mockAlertCondition).getDescription();
        doReturn(0).when(mockAlertCondition).getBacklog();
        mockTriggeredConditionPojo = new TriggeredConditionPojo(mockAlertCondition);
        mockTriggeredConditionPojo.setId(centinelRESTClientFactory.inputId());
        mockTriggeredConditionPojo.setType(centinelRESTClientFactory.inputType());
        mockTriggeredConditionPojo.setCreatorUserId(centinelRESTClientFactory.inputCreatoruserid());
        mockTriggeredConditionPojo.setCreatedAt(centinelRESTClientFactory.inputCreatedAt());
        mockTriggeredConditionPojo.setGrace(centinelRESTClientFactory.inputGrace());
        mockTriggeredConditionPojo.setDescription(centinelRESTClientFactory.inputDescription());
        mockTriggeredConditionPojo.setBacklog(centinelRESTClientFactory.inputBacklog());
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("1", "streamId");
        mockTriggeredConditionPojo.setParameters(centinelRESTClientFactory.inputParameter());

    }
}
