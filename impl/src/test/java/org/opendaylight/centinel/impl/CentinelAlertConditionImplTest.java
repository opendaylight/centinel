/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldContentRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldContentRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldValueRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldValueRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertMessageCountRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertMessageCountRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertFieldContentRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertFieldContentRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertFieldValueRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertFieldValueRuleInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertFieldValueRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertMessageCountRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertMessageCountRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.GetAllAlertRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.GetAllAlertRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldContentRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldContentRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldValueRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldValueRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertMessageCountRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertMessageCountRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertFieldContentRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertFieldContentRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertFieldValueRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertFieldValueRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertMessageCountRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertMessageCountRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldcontentrulerecord.StreamAlertFieldContentRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldvaluerulerecord.StreamAlertFieldValueRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertmessagecountrulerecord.StreamAlertMessageCountRuleList;
import org.opendaylight.yangtools.yang.common.RpcResult;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.Futures;

/**
 * @author Amandeep Singh Rattenpal This class provides the test case handling
 *         for different scenarios
 */

public class CentinelAlertConditionImplTest {

    CentinelAlertConditionImpl centinelAlertConditionImpl;
    @Mock
    private DataBroker mockDataBroker;

    @Mock
    private CentinelAlertConditionImpl mockCentinelAlertConditionImpl;
    private MockCentinelAlertConditionImpl myMock = new MockCentinelAlertConditionImpl();
    private CentinelAlertConditionImplFactory centinelAlertConditionImplFactory = new CentinelAlertConditionImplFactory();

    @Before
    public void beforeTest() {
        MockitoAnnotations.initMocks(this);
        centinelAlertConditionImpl = new CentinelAlertConditionImpl();
        myMock.setDataProvider(mockDataBroker);

    }

    @After
    public void AfterTest() {
        centinelAlertConditionImpl = null;

    }

    public CentinelAlertConditionImplFactory getCentinelImplFactory() {
        return centinelAlertConditionImplFactory;
    }

    public void setCentinelImplFactory(CentinelAlertConditionImplFactory centinelImplFactory) {
        this.centinelAlertConditionImplFactory = centinelImplFactory;
    }

    private class MockCentinelAlertConditionImpl extends CentinelAlertConditionImpl {
        public String generateConfigId() {
            return "1000";
        }
    }

    /**
     * Test method for AlertMessageCountRule on valid inputs
     */

    @Test
    public void testSetAlertMessageCountRule() {
        // build dummy input
        SetAlertMessageCountRuleInput input = centinelAlertConditionImplFactory.setInputForAlertMessageCount();

        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertMessageCountRuleList> streamAlertMessageCountRuleList = new ArrayList<StreamAlertMessageCountRuleList>();
        streamAlertMessageCountRuleList.add(myMock.buildAlertMessageCountRuleRecord(input, myMock.generateConfigId()));

        AlertMessageCountRuleRecord alertMessageCountRuleRecord = new AlertMessageCountRuleRecordBuilder()
                .setStreamAlertMessageCountRuleList(streamAlertMessageCountRuleList).build();

        Optional<AlertMessageCountRuleRecord> expected = Optional.of(alertMessageCountRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertMessageCountRuleRecordId);

        Future<RpcResult<SetAlertMessageCountRuleOutput>> futureOutput = myMock.setAlertMessageCountRule(input);

        try {
            SetAlertMessageCountRuleOutput out = futureOutput.get().getResult();
            String expectedMsg = centinelAlertConditionImplFactory.expectedAlertMessageCountRuleObject().toString();
            assertEquals(expectedMsg, out.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getCause().toString());
        } catch (ExecutionException e) {
            e.printStackTrace();

        }

    }

    /**
     * Test method for AlertMessageCountRule on invalid inputs
     */

    @Test
    public void testSetAlertMessageCountRuleFailure() {
        // build dummy input
        SetAlertMessageCountRuleInput input = centinelAlertConditionImplFactory.setInputBuilderWithMissingInput();

        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertMessageCountRuleList> streamAlertMessageCountRuleList = new ArrayList<StreamAlertMessageCountRuleList>();
        streamAlertMessageCountRuleList.add(myMock.buildAlertMessageCountRuleRecord(input, myMock.generateConfigId()));

        AlertMessageCountRuleRecord alertMessageCountRuleRecord = new AlertMessageCountRuleRecordBuilder()
                .setStreamAlertMessageCountRuleList(streamAlertMessageCountRuleList).build();

        Optional<AlertMessageCountRuleRecord> expected = Optional.of(alertMessageCountRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertMessageCountRuleRecordId);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        Future<RpcResult<SetAlertMessageCountRuleOutput>> futureOutput = myMock.setAlertMessageCountRule(input);

        boolean caught = false;
        try {
            futureOutput.get().getErrors();
        } catch (ExecutionException | InterruptedException e) {
            caught = true;
        }
        assertTrue(caught);
    }

    /**
     * Test method for AlertFieldContentRule on valid inputs
     */

    @Test
    public void testSetAlertFieldContentRule() {

        // build dummy input
        SetAlertFieldContentRuleInput input = centinelAlertConditionImplFactory.setInputForAlertFieldContentRule();

        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertFieldContentRuleList> streamAlertFieldContentRuleList = new ArrayList<StreamAlertFieldContentRuleList>();
        streamAlertFieldContentRuleList.add(myMock.buildAlertFieldContentRuleRecord(input, myMock.generateConfigId()));

        AlertFieldContentRuleRecord alertFieldContentRuleRecord = new AlertFieldContentRuleRecordBuilder()
                .setStreamAlertFieldContentRuleList(streamAlertFieldContentRuleList).build();

        Optional<AlertFieldContentRuleRecord> expected = Optional.of(alertFieldContentRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertFeildContentRuleRecordId);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        Future<RpcResult<SetAlertFieldContentRuleOutput>> futureOutput = myMock.setAlertFieldContentRule(input);

        try {
            SetAlertFieldContentRuleOutput out = futureOutput.get().getResult();
            String expectedMsg = centinelAlertConditionImplFactory.expectedAlertFieldContentRuleObject().toString();
            assertEquals(expectedMsg, out.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getCause().toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail(e.getCause().toString());
        }

    }

    /**
     * Test method for AlertFieldContentRule on invalid inputs
     */

    @Test
    public void testSetAlertFieldContentRuleFailure() {

        // build dummy input
        SetAlertFieldContentRuleInput input = centinelAlertConditionImplFactory
                .setInputForAlertFieldContentWithInvalidInput();

        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertFieldContentRuleList> streamAlertFieldContentRuleList = new ArrayList<StreamAlertFieldContentRuleList>();
        streamAlertFieldContentRuleList.add(myMock.buildAlertFieldContentRuleRecord(input, myMock.generateConfigId()));

        AlertFieldContentRuleRecord alertFieldContentRuleRecord = new AlertFieldContentRuleRecordBuilder()
                .setStreamAlertFieldContentRuleList(streamAlertFieldContentRuleList).build();

        Optional<AlertFieldContentRuleRecord> expected = Optional.of(alertFieldContentRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertFeildContentRuleRecordId);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        Future<RpcResult<SetAlertFieldContentRuleOutput>> futureOutput = myMock.setAlertFieldContentRule(input);

        try {
            boolean caught = false;
            try {
                futureOutput.get().getErrors();
            } catch (ExecutionException e) {
                caught = true;
            }
            assertTrue(caught);
        } catch (InterruptedException e) {
            fail(e.getCause().toString());
        }

    }

    /**
     * Test method for AlertFieldValueRule on valid inputs
     */

    @Test
    public void testSetAlertFieldValueRule() {

        // build dummy input
        SetAlertFieldValueRuleInput input = centinelAlertConditionImplFactory.setInputForAlertFieldValueRule();

        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertFieldValueRuleList> streamAlertFieldValueRuleList = new ArrayList<StreamAlertFieldValueRuleList>();
        streamAlertFieldValueRuleList.add(myMock.buildAlertFieldValueRuleRecord(input, myMock.generateConfigId()));

        AlertFieldValueRuleRecord alertFieldValueRuleRecord = new AlertFieldValueRuleRecordBuilder()
                .setStreamAlertFieldValueRuleList(streamAlertFieldValueRuleList).build();

        Optional<AlertFieldValueRuleRecord> expected = Optional.of(alertFieldValueRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertFieldValueRuleRecordId);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        Future<RpcResult<SetAlertFieldValueRuleOutput>> futureOutput = myMock.setAlertFieldValueRule(input);

        try {
            SetAlertFieldValueRuleOutput out = futureOutput.get().getResult();
            String expectedMsg = centinelAlertConditionImplFactory.expectedAlertFieldValueRuleObject().toString();
            assertEquals(expectedMsg, out.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getCause().toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail(e.getCause().toString());
        }

    }

    /**
     * Test method for AlertFieldValueRule on invalid inputs
     */

    @Test
    public void testSetAlertFieldValueRuleFailure() {

        // build dummy input
        SetAlertFieldValueRuleInput input = centinelAlertConditionImplFactory
                .setInputForAlertFieldValueWithInvalidInput();

        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertFieldValueRuleList> streamAlertFieldValueRuleList = new ArrayList<StreamAlertFieldValueRuleList>();
        streamAlertFieldValueRuleList.add(myMock.buildAlertFieldValueRuleRecord(input, myMock.generateConfigId()));

        AlertFieldValueRuleRecord alertFieldValueRuleRecord = new AlertFieldValueRuleRecordBuilder()
                .setStreamAlertFieldValueRuleList(streamAlertFieldValueRuleList).build();

        Optional<AlertFieldValueRuleRecord> expected = Optional.of(alertFieldValueRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertFieldValueRuleRecordId);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        Future<RpcResult<SetAlertFieldValueRuleOutput>> futureOutput = myMock.setAlertFieldValueRule(input);

        try {
            boolean caught = false;
            try {
                futureOutput.get().getErrors();
            } catch (ExecutionException e) {
                caught = true;
            }
            assertTrue(caught);
        } catch (InterruptedException e) {
            fail(e.getCause().toString());
        }

    }

    /**
     * Test method to update AlertMessageCountRule on valid inputs
     */

    @Test
    public void testUpdateAlertMessageCountRuleOnValidInput() {
        // build dummy input
        UpdateAlertMessageCountRuleInput input = centinelAlertConditionImplFactory.updateInputForAlertMessageCount();
        StreamAlertMessageCountRuleList streamAlertMessageCountRuleListObj = centinelAlertConditionImplFactory
                .mockStreamAlertMessageCountRuleObjectBuilder();

        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertMessageCountRuleList> streamAlertMessageCountRuleList = new ArrayList<StreamAlertMessageCountRuleList>();
        streamAlertMessageCountRuleList.add(myMock.buildUpdateAlertMessageCountRuleRecord(input,
                streamAlertMessageCountRuleListObj));

        AlertMessageCountRuleRecord alertMessageCountRuleRecord = new AlertMessageCountRuleRecordBuilder()
                .setStreamAlertMessageCountRuleList(streamAlertMessageCountRuleList).build();

        Optional<AlertMessageCountRuleRecord> expected = Optional.of(alertMessageCountRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertMessageCountRuleRecordId);

        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                MockCentinelAlertConditionImpl.alertMessageCountRuleRecordId);

        doNothing().when(mockReadWriteTx).merge(
                LogicalDatastoreType.CONFIGURATION,
                MockCentinelAlertConditionImpl.alertMessageCountRuleRecordId,
                new AlertMessageCountRuleRecordBuilder().setStreamAlertMessageCountRuleList(
                        streamAlertMessageCountRuleList).build(), false);
        doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx).submit();
        Future<RpcResult<UpdateAlertMessageCountRuleOutput>> futureOutput = myMock.updateAlertMessageCountRule(input);

        assertNotNull(futureOutput);
    }

    /**
     * Test method to update AlertMessageCountRule on invalid inputs
     */

    @Test
    public void testUpdateAlertMessageCountRuleFailureDueToInvalidInput() {
        // build dummy input
        UpdateAlertMessageCountRuleInput input = centinelAlertConditionImplFactory
                .updateInputForAlertMessageCountWithInvalidValues();
        StreamAlertMessageCountRuleList streamAlertMessageCountRuleListObj = centinelAlertConditionImplFactory
                .mockStreamAlertMessageCountRuleObjectBuilder();

        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertMessageCountRuleList> streamAlertMessageCountRuleList = new ArrayList<StreamAlertMessageCountRuleList>();
        streamAlertMessageCountRuleList.add(myMock.buildUpdateAlertMessageCountRuleRecord(input,
                streamAlertMessageCountRuleListObj));

        AlertMessageCountRuleRecord alertMessageCountRuleRecord = new AlertMessageCountRuleRecordBuilder()
                .setStreamAlertMessageCountRuleList(streamAlertMessageCountRuleList).build();

        Optional<AlertMessageCountRuleRecord> expected = Optional.of(alertMessageCountRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                MockCentinelAlertConditionImpl.alertMessageCountRuleRecordId);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        Future<RpcResult<UpdateAlertMessageCountRuleOutput>> futureOutput = myMock.updateAlertMessageCountRule(input);

        boolean caught = false;
        try {
            futureOutput.get().getErrors();

        } catch (ExecutionException | InterruptedException e) {
            caught = true;
        }
        assertTrue(caught);
    }

    /**
     * Test method to update AlertFieldValueRule on valid inputs
     */

    @Test
    public void testUpdateAlertFieldValueRuleOnValidInput() {
        // build dummy input
        UpdateAlertFieldValueRuleInput input = centinelAlertConditionImplFactory
                .updateInputWithValidValuesForAlertFieldValueRule();
        StreamAlertFieldValueRuleList streamAlertFieldValueRuleListObj = centinelAlertConditionImplFactory
                .mockStreamFieldValueRuleObjectBuilder();

        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertFieldValueRuleList> streamAlertFieldValueRuleList = new ArrayList<StreamAlertFieldValueRuleList>();
        streamAlertFieldValueRuleList.add(myMock.buildUpdateAlertFieldValueRuleRecord(input,
                streamAlertFieldValueRuleListObj));

        AlertFieldValueRuleRecord alertFieldValueRuleRecord = new AlertFieldValueRuleRecordBuilder()
                .setStreamAlertFieldValueRuleList(streamAlertFieldValueRuleList).build();

        Optional<AlertFieldValueRuleRecord> expected = Optional.of(alertFieldValueRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertFieldValueRuleRecordId);

        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                MockCentinelAlertConditionImpl.alertFieldValueRuleRecordId);

        doNothing().when(mockReadWriteTx).merge(
                LogicalDatastoreType.CONFIGURATION,
                MockCentinelAlertConditionImpl.alertFieldValueRuleRecordId,
                new AlertFieldValueRuleRecordBuilder().setStreamAlertFieldValueRuleList(streamAlertFieldValueRuleList)
                        .build(), false);
        doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx).submit();
        Future<RpcResult<UpdateAlertFieldValueRuleOutput>> futureOutput = myMock.updateAlertFieldValueRule(input);

        assertNotNull(futureOutput);
    }

    /**
     * Test method to update AlertFieldValueRule on invalid inputs
     */

    @Test
    public void testUpdateAlertFieldValueRuleFailureDueToInvalidInput() {
        // build dummy input
        UpdateAlertFieldValueRuleInput input = centinelAlertConditionImplFactory
                .updateInputWithInvalidValuesForAlertFieldValueRule();
        StreamAlertFieldValueRuleList streamAlertFieldValueRuleListObj = centinelAlertConditionImplFactory
                .mockStreamFieldValueRuleObjectBuilder();

        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertFieldValueRuleList> streamAlertFieldValueRuleList = new ArrayList<StreamAlertFieldValueRuleList>();
        streamAlertFieldValueRuleList.add(myMock.buildUpdateAlertFieldValueRuleRecord(input,
                streamAlertFieldValueRuleListObj));

        AlertFieldValueRuleRecord alertFieldValueRuleRecord = new AlertFieldValueRuleRecordBuilder()
                .setStreamAlertFieldValueRuleList(streamAlertFieldValueRuleList).build();

        Optional<AlertFieldValueRuleRecord> expected = Optional.of(alertFieldValueRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertMessageCountRuleRecordId);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        Future<RpcResult<UpdateAlertFieldValueRuleOutput>> futureOutput = myMock.updateAlertFieldValueRule(input);

        boolean caught = false;
        try {
            futureOutput.get().getErrors();

        } catch (ExecutionException | InterruptedException e) {
            caught = true;
        }
        assertTrue(caught);
    }

    /**
     * Test method to update AlertFieldContentRule on valid inputs
     */

    @Test
    public void testUpdateAlertFieldContentRuleOnValidInput() {
        // build dummy input
        UpdateAlertFieldContentRuleInput input = centinelAlertConditionImplFactory
                .updateInputWithValidValuesForAlertFieldContentRule();
        StreamAlertFieldContentRuleList streamAlertFieldContentRuleListObj = centinelAlertConditionImplFactory
                .mockStreamAlertFieldContentRuleObjectBuilder();
        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertFieldContentRuleList> streamAlertFieldContentRuleList = new ArrayList<StreamAlertFieldContentRuleList>();
        streamAlertFieldContentRuleList.add(myMock.buildUpdateAlertFieldContentRuleRecord(input,
                streamAlertFieldContentRuleListObj));

        AlertFieldContentRuleRecord alertFieldContentRuleRecord = new AlertFieldContentRuleRecordBuilder()
                .setStreamAlertFieldContentRuleList(streamAlertFieldContentRuleList).build();

        Optional<AlertFieldContentRuleRecord> expected = Optional.of(alertFieldContentRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertFeildContentRuleRecordId);

        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                MockCentinelAlertConditionImpl.alertFeildContentRuleRecordId);

        doNothing().when(mockReadWriteTx).merge(
                LogicalDatastoreType.CONFIGURATION,
                MockCentinelAlertConditionImpl.alertFeildContentRuleRecordId,
                new AlertFieldContentRuleRecordBuilder().setStreamAlertFieldContentRuleList(
                        streamAlertFieldContentRuleList).build(), false);
        doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx).submit();
        Future<RpcResult<UpdateAlertFieldContentRuleOutput>> futureOutput = myMock.updateAlertFieldContentRule(input);

        assertNotNull(futureOutput);
    }

    /**
     * Test method to update AlertFieldContentRule on invalid inputs
     */

    @Test
    public void testUpdateAlertFieldContentRuleFailureDueToInvalidInput() {
        // build dummy input
        UpdateAlertFieldContentRuleInput input = centinelAlertConditionImplFactory
                .updateInputWithInvalidValuesForAlertFieldContentRule();
        StreamAlertFieldContentRuleList streamAlertFieldContentRuleListObj = centinelAlertConditionImplFactory
                .mockStreamAlertFieldContentRuleObjectBuilder();

        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertFieldContentRuleList> streamAlertFieldContentRuleList = new ArrayList<StreamAlertFieldContentRuleList>();
        streamAlertFieldContentRuleList.add(myMock.buildUpdateAlertFieldContentRuleRecord(input,
                streamAlertFieldContentRuleListObj));

        AlertFieldContentRuleRecord alertFieldContentRuleRecord = new AlertFieldContentRuleRecordBuilder()
                .setStreamAlertFieldContentRuleList(streamAlertFieldContentRuleList).build();

        Optional<AlertFieldContentRuleRecord> expected = Optional.of(alertFieldContentRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertMessageCountRuleRecordId);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        Future<RpcResult<UpdateAlertFieldContentRuleOutput>> futureOutput = myMock.updateAlertFieldContentRule(input);

        boolean caught = false;
        try {
            futureOutput.get().getErrors();

        } catch (ExecutionException | InterruptedException e) {
            caught = true;
        }
        assertTrue(caught);

    }

    /**
     * Test method to GetAllAlertRule on invalid inputs
     */

    @Test
    public void testGetAllAlertRuleFailureDueToInvalidInput() {
        // build dummy input
        GetAllAlertRuleInput input = centinelAlertConditionImplFactory.getAllRuleInputWithInvalidValues();

        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertMessageCountRuleList> streamAlertMessageCountRuleList = new ArrayList<StreamAlertMessageCountRuleList>();

        AlertMessageCountRuleRecord alertMessageCountRuleRecord = new AlertMessageCountRuleRecordBuilder()
                .setStreamAlertMessageCountRuleList(streamAlertMessageCountRuleList).build();

        Optional<AlertMessageCountRuleRecord> expected = Optional.of(alertMessageCountRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                MockCentinelAlertConditionImpl.alertMessageCountRuleRecordId);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        Future<RpcResult<GetAllAlertRuleOutput>> futureOutput = myMock.getAllAlertRule(input);

        boolean caught = false;
        try {
            futureOutput.get().getErrors();

        } catch (ExecutionException | InterruptedException e) {
            caught = true;
        }
        assertTrue(caught);
    }

    /**
     * Test method to deleteAlertMessageCountRule on valid inputs
     */

    @Test
    public void testDeleteAlertMessageCountRuleOnValidInput() {
        // build dummy input
        DeleteAlertMessageCountRuleInput input = centinelAlertConditionImplFactory
                .deleteInputValidValuesForAlertMessageCount();
        StreamAlertMessageCountRuleList streamAlertMessageCountRuleListObj = centinelAlertConditionImplFactory
                .mockStreamAlertMessageCountRuleObjectBuilder();
        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertMessageCountRuleList> streamAlertMessageCountRuleList = new ArrayList<StreamAlertMessageCountRuleList>();

        AlertMessageCountRuleRecord alertMessageCountRuleRecord = new AlertMessageCountRuleRecordBuilder()
                .setStreamAlertMessageCountRuleList(streamAlertMessageCountRuleList).build();

        Optional<AlertMessageCountRuleRecord> expected = Optional.of(alertMessageCountRuleRecord);

        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertMessageCountRuleRecordId);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                MockCentinelAlertConditionImpl.alertMessageCountRuleRecordId);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        doNothing().when(mockReadWriteTx).delete(
                LogicalDatastoreType.CONFIGURATION,
                MockCentinelAlertConditionImpl.alertMessageCountRuleRecordId.child(
                        StreamAlertMessageCountRuleList.class, streamAlertMessageCountRuleListObj.getKey()));
        doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx).submit();

        Future<RpcResult<DeleteAlertMessageCountRuleOutput>> futureOutput = myMock.deleteAlertMessageCountRule(input);
        assertNotNull(futureOutput);
    }

    /**
     * Test method to deleteAlertMessageCountRule on invalid inputs
     */

    @Test
    public void testDeleteAlertMessageCountRuleFailureDueToInvalidInput() {
        // build dummy input
        DeleteAlertMessageCountRuleInput input = centinelAlertConditionImplFactory
                .deleteInputWithInvalidValuesForAlertMessageCount();
        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        List<StreamAlertMessageCountRuleList> streamAlertMessageCountRuleList = new ArrayList<StreamAlertMessageCountRuleList>();
        AlertMessageCountRuleRecord alertMessageCountRuleRecord = new AlertMessageCountRuleRecordBuilder()
                .setStreamAlertMessageCountRuleList(streamAlertMessageCountRuleList).build();
        Optional<AlertMessageCountRuleRecord> expected = Optional.of(alertMessageCountRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertMessageCountRuleRecordId);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        Future<RpcResult<DeleteAlertMessageCountRuleOutput>> futureOutput = myMock.deleteAlertMessageCountRule(input);
        boolean caught = false;
        try {
            futureOutput.get().getErrors();

        } catch (ExecutionException | InterruptedException e) {
            caught = true;
        }
        assertTrue(caught);
    }

    /**
     * Test method to deleteAlertFieldValueRule on valid inputs
     */

    @Test
    public void testDeleteAlertFieldValueRuleOnValidInput() {
        // build dummy input
        DeleteAlertFieldValueRuleInput input = centinelAlertConditionImplFactory
                .deleteInputValidValuesForAlertFieldValue();
        StreamAlertFieldValueRuleList streamAlertFieldValueRuleListObj = centinelAlertConditionImplFactory
                .mockStreamFieldValueRuleObjectBuilder();
        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertFieldValueRuleList> streamAlertFieldValueRuleList = new ArrayList<StreamAlertFieldValueRuleList>();

        AlertFieldValueRuleRecord alertFieldValueRuleRecord = new AlertFieldValueRuleRecordBuilder()
                .setStreamAlertFieldValueRuleList(streamAlertFieldValueRuleList).build();

        Optional<AlertFieldValueRuleRecord> expected = Optional.of(alertFieldValueRuleRecord);

        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertFieldValueRuleRecordId);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                MockCentinelAlertConditionImpl.alertFieldValueRuleRecordId);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        doNothing().when(mockReadWriteTx).delete(
                LogicalDatastoreType.CONFIGURATION,
                MockCentinelAlertConditionImpl.alertFieldValueRuleRecordId.child(StreamAlertFieldValueRuleList.class,
                        streamAlertFieldValueRuleListObj.getKey()));
        doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx).submit();

        Future<RpcResult<DeleteAlertFieldValueRuleOutput>> futureOutput = myMock.deleteAlertFieldValueRule(input);
        assertNotNull(futureOutput);
    }

    /**
     * Test method to deleteAlertFieldValueRule on invalid inputs
     */

    @Test
    public void testDeleteAlertFieldValueRuleFailureDueToInvalidInput() {
        // build dummy input
        DeleteAlertFieldValueRuleInput input = centinelAlertConditionImplFactory
                .deleteInputWithInvalidValuesForAlertFieldValue();
        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        List<StreamAlertFieldValueRuleList> streamAlertFieldValueRuleList = new ArrayList<StreamAlertFieldValueRuleList>();
        AlertFieldValueRuleRecord alertFieldValueRuleRecord = new AlertFieldValueRuleRecordBuilder()
                .setStreamAlertFieldValueRuleList(streamAlertFieldValueRuleList).build();
        Optional<AlertFieldValueRuleRecord> expected = Optional.of(alertFieldValueRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertFieldValueRuleRecordId);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        Future<RpcResult<DeleteAlertFieldValueRuleOutput>> futureOutput = myMock.deleteAlertFieldValueRule(input);
        boolean caught = false;
        try {
            futureOutput.get().getErrors();

        } catch (ExecutionException | InterruptedException e) {
            caught = true;
        }
        assertTrue(caught);
    }

    /**
     * Test method to deleteAlertFieldContentRule on valid inputs
     */

    @Test
    public void testDeleteAlertFieldContentRuleOnValidInput() {
        // build dummy input
        DeleteAlertFieldContentRuleInput input = centinelAlertConditionImplFactory
                .deleteInputValidValuesForAlertFieldContent();
        StreamAlertFieldContentRuleList streamAlertFieldContentRuleListObj = centinelAlertConditionImplFactory
                .mockStreamAlertFieldContentRuleObjectBuilder();
        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamAlertFieldContentRuleList> streamAlertFieldContentRuleList = new ArrayList<StreamAlertFieldContentRuleList>();

        AlertFieldContentRuleRecord alertFieldContentRuleRecord = new AlertFieldContentRuleRecordBuilder()
                .setStreamAlertFieldContentRuleList(streamAlertFieldContentRuleList).build();

        Optional<AlertFieldContentRuleRecord> expected = Optional.of(alertFieldContentRuleRecord);

        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertFeildContentRuleRecordId);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                MockCentinelAlertConditionImpl.alertFeildContentRuleRecordId);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        doNothing().when(mockReadWriteTx).delete(
                LogicalDatastoreType.CONFIGURATION,
                MockCentinelAlertConditionImpl.alertFeildContentRuleRecordId.child(
                        StreamAlertFieldContentRuleList.class, streamAlertFieldContentRuleListObj.getKey()));
        doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx).submit();

        Future<RpcResult<DeleteAlertFieldContentRuleOutput>> futureOutput = myMock.deleteAlertFieldContentRule(input);
        assertNotNull(futureOutput);
    }

    /**
     * Test method to deleteAlertFieldContentRule on invalid inputs
     */

    @Test
    public void testDeleteAlertFieldContentRuleFailureDueToInvalidInput() {
        // build dummy input
        DeleteAlertFieldContentRuleInput input = centinelAlertConditionImplFactory
                .deleteInputWithInvalidValuesForAlertFieldContent();
        // to mock the tx object
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        List<StreamAlertFieldContentRuleList> streamAlertFieldContentRuleList = new ArrayList<StreamAlertFieldContentRuleList>();
        AlertFieldContentRuleRecord alertFieldContentRuleRecord = new AlertFieldContentRuleRecordBuilder()
                .setStreamAlertFieldContentRuleList(streamAlertFieldContentRuleList).build();
        Optional<AlertFieldContentRuleRecord> expected = Optional.of(alertFieldContentRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(
                LogicalDatastoreType.CONFIGURATION, MockCentinelAlertConditionImpl.alertFeildContentRuleRecordId);

        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        Future<RpcResult<DeleteAlertFieldContentRuleOutput>> futureOutput = myMock.deleteAlertFieldContentRule(input);
        boolean caught = false;
        try {
            futureOutput.get().getErrors();

        } catch (ExecutionException | InterruptedException e) {
            caught = true;
        }
        assertTrue(caught);
    }

}

