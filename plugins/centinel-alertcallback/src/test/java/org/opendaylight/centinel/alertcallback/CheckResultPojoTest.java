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

import java.util.List;

import org.graylog2.plugin.MessageSummary;
import org.graylog2.plugin.alarms.AlertCondition;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.centinel.alertcallback.CentinelRESTClient.CheckResultPojo;

public class CheckResultPojoTest {

    AlertCondition.CheckResult checkResult = null;
    CheckResultPojo mockCheckResultPojo = null;
    CheckResultPojoFactory checkResultPojoFactory = new CheckResultPojoFactory();
    AlertCondition mockAlertCondition = null;
    List<MessageSummary> mockListMessageSummary = null;

    @Before
    public void init() {
        checkResult = mock(AlertCondition.CheckResult.class);
        doReturn(new DateTime()).when(checkResult).getTriggeredAt();
        mockAlertCondition = mock(AlertCondition.class);
        mockListMessageSummary = mock(List.class);
        doReturn("mock result desc").when(checkResult).getResultDescription();
        doReturn(mockAlertCondition).when(checkResult).getTriggeredCondition();
        doReturn(mockListMessageSummary).when(checkResult).getMatchingMessages();
        doReturn(true).when(checkResult).isTriggered();
        mockCheckResultPojo = new CheckResultPojo(checkResult);

    }

    @After
    public void destroy() {
        checkResult = null;
        mockCheckResultPojo = null;
        checkResultPojoFactory = null;
        mockAlertCondition = null;
        mockListMessageSummary = null;
    }

    @Test
    public void testSetGetResultDescription() {
        mockCheckResultPojo.setResultDescription(checkResultPojoFactory.inputSetResultDescription());
        mockCheckResultPojo.getResultDescription();
    }

    @Test
    public void testSetGetTriggeredAt() {
        mockCheckResultPojo.getTriggeredAt();
        mockCheckResultPojo.setTriggeredAt(checkResultPojoFactory.inputTriggeredAt());
    }

    @Test
    public void testSetGetIsTriggered() {
        mockCheckResultPojo.isTriggered();
        mockCheckResultPojo.setTriggered(checkResultPojoFactory.inputIsTriggered());
    }

    @Test
    public void testSetGetTriggeredCondition() {
        mockCheckResultPojo.setTriggeredCondition(mockAlertCondition);
        mockCheckResultPojo.getTriggeredCondition();
    }

    @Test
    public void testSetGetMatchingMessages() {
        mockCheckResultPojo.setMatchingMessages(mockListMessageSummary);
        mockCheckResultPojo.getMatchingMessages();
    }

}
