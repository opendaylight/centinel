/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.laas.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.NotificationService;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.laas.impl.CentinelLaasStreamImpl;
import org.opendaylight.laas.rest.utilities.CentinelStreamRESTServices;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.streamrecord.StreamList;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.Futures;

/**
 * @author Monika Verma
 * 
 *         This class provides unit tests for CentinelLaasStreamImpl.
 * 
 */
public class CentinelLaasStreamImplTest {

    CentinelLaasStreamImpl centinelLaasStreamImpl = new CentinelLaasStreamImpl();
    ChangeEventObjectFactory changeEventObjectFactory = new ChangeEventObjectFactory();

    @Mock
    private NotificationService mockNotificationService;
    @Mock
    private DataBroker mockDataBroker;

    @Before
    public void beforeTest() {
        MockitoAnnotations.initMocks(this);
        centinelLaasStreamImpl.setDataProvider(mockDataBroker);
    }

    @After
    public void AfterTest() {
        try {
            centinelLaasStreamImpl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        centinelLaasStreamImpl = null;
    }
    
    
    @Test
    public void testCreateFromConfigToOperationalStream()
    {
    	boolean exceptionCaughtFromGraylog = false;
    	try
    	{
    	 ChangeEventObjectFactory changeEventObjectFactory = new ChangeEventObjectFactory();
    	CentinelStreamRESTServices.getInstance().createFromConfigToOperationalStream(changeEventObjectFactory.getOriginalSubtreeStreamRecord());
    	}
    	 catch (Exception e) {
             exceptionCaughtFromGraylog = true;
             assertTrue(exceptionCaughtFromGraylog);
         }
    }

    @SuppressWarnings("static-access")
    @Test
    public void testOnDataChangeCreateStreamRecord() {

        boolean exceptionCaughtFromGraylog = false;

        @SuppressWarnings("unchecked")
        final AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> mockChangeEvent = Mockito
                .mock(AsyncDataChangeEvent.class);
        doReturn(changeEventObjectFactory.getUpdatedSubtreeStreamRecord()).when(mockChangeEvent).getUpdatedSubtree();
        doReturn(changeEventObjectFactory.getCreatedDataStreamRecord()).when(mockChangeEvent).getCreatedData();

        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamList> streamList = new ArrayList<StreamList>();
        streamList.add(changeEventObjectFactory.getOriginalStreamList());
        StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(streamList).build();
        Optional<StreamRecord> expected = Optional.of(streamRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                centinelLaasStreamImpl.streamRecordId);

        try {
            centinelLaasStreamImpl.onDataChanged(mockChangeEvent);
        }

        catch (Exception e) {
            exceptionCaughtFromGraylog = true;
            assertTrue(exceptionCaughtFromGraylog);
        }

    }

    @SuppressWarnings("static-access")
    @Test
    public void testOnDataChangeUpdateStreamRecord() {

        boolean exceptionCaughtFromGraylog = false;

        @SuppressWarnings("unchecked")
        final AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> mockChangeEvent = Mockito
                .mock(AsyncDataChangeEvent.class);
        doReturn(changeEventObjectFactory.getUpdatedSubtreeStreamRecord()).when(mockChangeEvent).getUpdatedSubtree();
        doReturn(changeEventObjectFactory.getOriginalSubtreeStreamRecord()).when(mockChangeEvent).getOriginalSubtree();

        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamList> streamList = new ArrayList<StreamList>();
        streamList.add(changeEventObjectFactory.getOriginalStreamList());
        StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(streamList).build();
        Optional<StreamRecord> expected = Optional.of(streamRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                centinelLaasStreamImpl.streamRecordId);

        try {
            centinelLaasStreamImpl.onDataChanged(mockChangeEvent);
        }

        catch (Exception e) {
            exceptionCaughtFromGraylog = true;
            assertTrue(exceptionCaughtFromGraylog);
        }

    }

    @SuppressWarnings({ "unchecked", "static-access" })
    @Test
    public void testOnDataChangeRemoveStreamRecord() {

        boolean exceptionCaughtFromGraylog = false;

        final AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> mockChangeEvent = Mockito
                .mock(AsyncDataChangeEvent.class);
        doReturn(changeEventObjectFactory.getUpdatedSubtreeStreamRecord()).when(mockChangeEvent).getUpdatedSubtree();
        doReturn(changeEventObjectFactory.getRemovedPathsStreamRecord()).when(mockChangeEvent).getRemovedPaths();
        doReturn(changeEventObjectFactory.getOriginalDataStreamList()).when(mockChangeEvent).getOriginalData();

        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        List<StreamList> streamList = new ArrayList<StreamList>();
        streamList.add(changeEventObjectFactory.getOriginalStreamList());
        StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(streamList).build();
        Optional<StreamRecord> expected = Optional.of(streamRecord);
        doReturn(Futures.immediateCheckedFuture(expected)).when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
                centinelLaasStreamImpl.streamRecordId);
        try {
            centinelLaasStreamImpl.onDataChanged(mockChangeEvent);
        }

        catch (Exception e) {
            exceptionCaughtFromGraylog = true;
            assertTrue(exceptionCaughtFromGraylog);
        }

    }

}
