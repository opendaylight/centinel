/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.las.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opendaylight.centinel.impl.CentinelAlertConditionImpl;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.laas.impl.CentinelLaasAlertConditionImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldContentRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldContentRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldValueRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldValueRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertMessageCountRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertMessageCountRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldcontentrulerecord.StreamAlertFieldContentRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldvaluerulerecord.StreamAlertFieldValueRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertmessagecountrulerecord.StreamAlertMessageCountRuleList;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.Futures;

/**
 * @author Abhishek Sharma 
 * 
 * This class tests CentinelLaasAlertConditionImpl.
 */
public class CentinelLaasAlertConditionImplTest {
	
    CentinelLaasAlertConditionImpl centinelLaasAlertConditionImpl;
    
    @Mock
    private DataBroker mockDataBroker;

    @Mock
    private CentinelLaasAlertConditionImpl mockCentinelLaasAlertConditionImpl;
    CentinelAlertConditionImplObjectFactory centinelAlertConditionImplObjectFactory = new CentinelAlertConditionImplObjectFactory();

    @Before
    public void beforeTest() {
        MockitoAnnotations.initMocks(this);
        centinelLaasAlertConditionImpl = new CentinelLaasAlertConditionImpl();
        centinelLaasAlertConditionImpl.setDataProvider(mockDataBroker);

    }

    @Test
    public void testCreateAlertMessageCountRule() {

        boolean exceptionCaughtFromGraylog = false;
        
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        @SuppressWarnings("unchecked")
        final AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> mockChangeEvent = Mockito
                .mock(AsyncDataChangeEvent.class);

        when(mockChangeEvent.getUpdatedSubtree()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertMessageCountRuleRecord());

        when(mockChangeEvent.getCreatedData()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertMessageCountRuleRecordMap());
        
        try {
            centinelLaasAlertConditionImpl.onDataChanged(mockChangeEvent);
        }

        catch (Exception e) {
            exceptionCaughtFromGraylog = true;
            assertTrue(exceptionCaughtFromGraylog);
        }
    }

    @Test
    public void testCreateAlertFieldContentRule() {

        boolean exceptionCaughtFromGraylog = false;

        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        @SuppressWarnings("unchecked")
        final AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> mockChangeEvent = Mockito
                .mock(AsyncDataChangeEvent.class);

        when(mockChangeEvent.getUpdatedSubtree()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertFieldContentRuleRecord());

        when(mockChangeEvent.getCreatedData()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertFieldContentRuleRecordMap());

        try {
            centinelLaasAlertConditionImpl.onDataChanged(mockChangeEvent);
        }

        catch (Exception e) {
            exceptionCaughtFromGraylog = true;
            assertTrue(exceptionCaughtFromGraylog);
        }

    }

    @Test
    public void testCreateAlertFieldValueRule() {

        boolean exceptionCaughtFromGraylog = false;

        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        @SuppressWarnings("unchecked")
        final AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> mockChangeEvent = Mockito
                .mock(AsyncDataChangeEvent.class);

        when(mockChangeEvent.getUpdatedSubtree()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertFieldValueRuleRecord());

        when(mockChangeEvent.getCreatedData()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertFieldValueRuleRecordMap());

        try {
            centinelLaasAlertConditionImpl.onDataChanged(mockChangeEvent);
        }

        catch (Exception e) {
            exceptionCaughtFromGraylog = true;
            assertTrue(exceptionCaughtFromGraylog);
        }

    }

    @Test
    public void testDeleteAlertMessageCountRule() {

        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        @SuppressWarnings("unchecked")
        final AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> mockChangeEvent = Mockito
                .mock(AsyncDataChangeEvent.class);

        when(mockChangeEvent.getRemovedPaths()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertMessageCountRuleRecordSet());
        when(mockChangeEvent.getOriginalData()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertMessageCountRuleListMap());

        List<StreamAlertMessageCountRuleList> streamAlertMessageCountRuleList = new ArrayList<StreamAlertMessageCountRuleList>();
        streamAlertMessageCountRuleList.add(centinelAlertConditionImplObjectFactory.buildAlertMessageCountRuleList());

        AlertMessageCountRuleRecord alertMessageCountRuleRecord = new AlertMessageCountRuleRecordBuilder()
                .setStreamAlertMessageCountRuleList(streamAlertMessageCountRuleList).build();

        Optional<AlertMessageCountRuleRecord> expected = Optional.of(alertMessageCountRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                CentinelAlertConditionImpl.alertMessageCountRuleRecordId);

            centinelLaasAlertConditionImpl.onDataChanged(mockChangeEvent);
            assertTrue(expected.get().getStreamAlertMessageCountRuleList().get(0)
                    .equals(centinelAlertConditionImplObjectFactory.buildAlertMessageCountRuleList()));

    }
    
    @Test
    public void testDeleteAlertFieldValueRule() {

        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        @SuppressWarnings("unchecked")
        final AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> mockChangeEvent = Mockito
                .mock(AsyncDataChangeEvent.class);

        when(mockChangeEvent.getRemovedPaths()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertFieldValueRuleRecordSet());
        when(mockChangeEvent.getOriginalData()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertFieldValueRuleListMap());

        List<StreamAlertFieldValueRuleList> streamAlertFieldValueRuleList = new ArrayList<StreamAlertFieldValueRuleList>();
        streamAlertFieldValueRuleList.add(centinelAlertConditionImplObjectFactory.buildAlertFieldValueRuleList());

        AlertFieldValueRuleRecord alertFieldValueRuleRecord = new AlertFieldValueRuleRecordBuilder()
                .setStreamAlertFieldValueRuleList(streamAlertFieldValueRuleList).build();

        Optional<AlertFieldValueRuleRecord> expected = Optional.of(alertFieldValueRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                CentinelAlertConditionImpl.alertFieldValueRuleRecordId);

            centinelLaasAlertConditionImpl.onDataChanged(mockChangeEvent);
            assertTrue(expected.get().getStreamAlertFieldValueRuleList().get(0)
                    .equals(centinelAlertConditionImplObjectFactory.buildAlertFieldValueRuleList()));

    }
    
    @Test
    public void testDeleteAlertFieldContentRule() {

        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        @SuppressWarnings("unchecked")
        final AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> mockChangeEvent = Mockito
                .mock(AsyncDataChangeEvent.class);

        when(mockChangeEvent.getRemovedPaths()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertFieldContentRuleRecordSet());
        when(mockChangeEvent.getOriginalData()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertFieldContentRuleListMap());

        List<StreamAlertFieldContentRuleList> streamAlertFieldContentRuleList = new ArrayList<StreamAlertFieldContentRuleList>();
        streamAlertFieldContentRuleList.add(centinelAlertConditionImplObjectFactory.buildAlertFieldContentRuleList());

        AlertFieldContentRuleRecord alertFieldContentRuleRecord = new AlertFieldContentRuleRecordBuilder()
                .setStreamAlertFieldContentRuleList(streamAlertFieldContentRuleList).build();

        Optional<AlertFieldContentRuleRecord> expected = Optional.of(alertFieldContentRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                CentinelAlertConditionImpl.alertFeildContentRuleRecordId);

            centinelLaasAlertConditionImpl.onDataChanged(mockChangeEvent);
            assertTrue(expected.get().getStreamAlertFieldContentRuleList().get(0)
                    .equals(centinelAlertConditionImplObjectFactory.buildAlertFieldContentRuleList()));

    }
    
    @Test
    public void testUpdateAlertMessageCountRule() {

        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        @SuppressWarnings("unchecked")
        final AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> mockChangeEvent = Mockito
                .mock(AsyncDataChangeEvent.class);

        when(mockChangeEvent.getUpdatedSubtree()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertMessageCountRuleRecordForUpdate());
        when(mockChangeEvent.getOriginalSubtree()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertMessageCountRuleRecord());

        List<StreamAlertMessageCountRuleList> streamAlertMessageCountRuleList = new ArrayList<StreamAlertMessageCountRuleList>();
        streamAlertMessageCountRuleList.add(centinelAlertConditionImplObjectFactory.buildAlertMessageCountRuleList());

        AlertMessageCountRuleRecord alertMessageCountRuleRecord = new AlertMessageCountRuleRecordBuilder()
                .setStreamAlertMessageCountRuleList(streamAlertMessageCountRuleList).build();

        Optional<AlertMessageCountRuleRecord> expected = Optional.of(alertMessageCountRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                CentinelAlertConditionImpl.alertMessageCountRuleRecordId);

            centinelLaasAlertConditionImpl.onDataChanged(mockChangeEvent);
            assertTrue(expected.get().getStreamAlertMessageCountRuleList().get(0)
                    .equals(centinelAlertConditionImplObjectFactory.buildAlertMessageCountRuleList()));

    }
    
    @Test
    public void testUpdateAlertFieldValueRule() {

        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        @SuppressWarnings("unchecked")
        final AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> mockChangeEvent = Mockito
                .mock(AsyncDataChangeEvent.class);

        when(mockChangeEvent.getUpdatedSubtree()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertFieldValueRuleRecordUpdate());
        when(mockChangeEvent.getOriginalSubtree()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertFieldValueRuleRecord());

        List<StreamAlertFieldValueRuleList> streamAlertFieldValueRuleList = new ArrayList<StreamAlertFieldValueRuleList>();
        streamAlertFieldValueRuleList.add(centinelAlertConditionImplObjectFactory.buildAlertFieldValueRuleList());

        AlertFieldValueRuleRecord alertFieldValueRuleRecord = new AlertFieldValueRuleRecordBuilder()
                .setStreamAlertFieldValueRuleList(streamAlertFieldValueRuleList).build();

        Optional<AlertFieldValueRuleRecord> expected = Optional.of(alertFieldValueRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                CentinelAlertConditionImpl.alertFieldValueRuleRecordId);

            centinelLaasAlertConditionImpl.onDataChanged(mockChangeEvent);
            assertTrue(expected.get().getStreamAlertFieldValueRuleList().get(0)
                    .equals(centinelAlertConditionImplObjectFactory.buildAlertFieldValueRuleList()));

    }
    
    @Test
    public void testUpdateAlertFieldContentRule() {

        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        @SuppressWarnings("unchecked")
        final AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> mockChangeEvent = Mockito
                .mock(AsyncDataChangeEvent.class);

        when(mockChangeEvent.getUpdatedSubtree()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertFieldContentRuleRecordForUpdate());
        when(mockChangeEvent.getOriginalSubtree()).thenReturn(
                centinelAlertConditionImplObjectFactory.buildAlertFieldContentRuleRecord());

        List<StreamAlertFieldContentRuleList> streamAlertFieldContentRuleList = new ArrayList<StreamAlertFieldContentRuleList>();
        streamAlertFieldContentRuleList.add(centinelAlertConditionImplObjectFactory.buildAlertFieldContentRuleList());

        AlertFieldContentRuleRecord alertFieldContentRuleRecord = new AlertFieldContentRuleRecordBuilder()
                .setStreamAlertFieldContentRuleList(streamAlertFieldContentRuleList).build();

        Optional<AlertFieldContentRuleRecord> expected = Optional.of(alertFieldContentRuleRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                CentinelAlertConditionImpl.alertFeildContentRuleRecordId);

            centinelLaasAlertConditionImpl.onDataChanged(mockChangeEvent);
            assertTrue(expected.get().getStreamAlertFieldContentRuleList().get(0)
                    .equals(centinelAlertConditionImplObjectFactory.buildAlertFieldContentRuleList()));

    }
    
}
