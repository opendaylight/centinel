/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.alertcallback;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.List;

import org.graylog2.plugin.MessageSummary;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackConfigurationException;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.streams.Stream;
import org.graylog2.plugin.streams.StreamRule;
import org.graylog2.plugin.streams.StreamRuleType;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Monika Verma
 * 
 * This class provides unit tests for CentinelAlertCallback.
 * 
 */

public class CentinelAlertCallbackTest {

    CentinelAlertCallback alertCallback;
    boolean exceptionCaught = false;
    
    @Before
    public void init() {
        alertCallback = new CentinelAlertCallback();
    }

    @After
    public void destroy() {
        alertCallback = null;
    }

    @Test
    public void testCall() {
        
        AlertCondition.CheckResult checkResult = mock(AlertCondition.CheckResult.class);
        AlertCondition alertCondition = mock(AlertCondition.class);
        Stream stream = mock(Stream.class);
        
        List<MessageSummary> listMessageSummary = mock(List.class);
        MessageSummary messageSummary = mock(MessageSummary.class);
        doReturn(messageSummary).when(listMessageSummary).get(0);
        doReturn(new DateTime()).when(messageSummary).getTimestamp();
        
        doReturn("alertConditionId").when(alertCondition).getId();
        doReturn("Message-Count").when(alertCondition).getTypeString();
        doReturn("testUserId").when(alertCondition).getCreatorUserId();
        doReturn(new DateTime()).when(alertCondition).getCreatedAt();
        doReturn(0).when(alertCondition).getGrace();
        doReturn(new HashMap<String, Object>()).when(alertCondition).getParameters();
        doReturn("AlertCondition Description").when(alertCondition).getDescription();
        doReturn(0).when(alertCondition).getBacklog();
        
        doReturn("mock result desc").when(checkResult).getResultDescription();
        doReturn(alertCondition).when(checkResult).getTriggeredCondition();
        doReturn(listMessageSummary).when(checkResult).getMatchingMessages();
        doReturn(new DateTime()).when(checkResult).getTriggeredAt();
        doReturn(true).when(checkResult).isTriggered();
        
        List<StreamRule> streamRules  = mock(List.class);
        StreamRule streamRule = mock(StreamRule.class);
        doReturn(streamRule).when(streamRules).get(0);
        doReturn(StreamRuleType.EXACT).when(streamRule).getType();
        
        doReturn(streamRules).when(stream).getStreamRules();
        doReturn("Stream Description").when(stream).getDescription();
        doReturn(true).when(stream).getDisabled();
        doReturn("StreamId").when(stream).getId();
        doReturn("Stream Title").when(stream).getTitle();
        
        try {
            alertCallback.call(stream, checkResult);
        } catch (AlarmCallbackException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }
    
    @Test
    public void testGetAttributes() {
        Configuration configuration = mock(Configuration.class);
        try {
            alertCallback.initialize(configuration);
        } catch (AlarmCallbackConfigurationException e) {
            e.printStackTrace();
        }
        doReturn(new HashMap<String, Object>()).when(configuration).getSource();
        assertNotNull(alertCallback.getAttributes());
    }
    
    @Test
    public void testGetRequestedConfiguration() {
        assertNotNull(alertCallback.getRequestedConfiguration());
    }
    
    @Test
    public void testGetName() {
        assertNotNull(alertCallback.getName());
    }

}
