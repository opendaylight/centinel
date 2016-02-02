/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.dashboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.streamhandler.impl.StreamhandlerImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.streamhandler.impl.rev141210.StreamhandlerModule;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DashboardIncrementTestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DashboardIncrementTestOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DashboardRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DashboardRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DeleteDashboardInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DeleteDashboardOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DeleteWidgetInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DeleteWidgetOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetWidgetHistogramInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetWidgetHistogramOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetWidgetMessageCountInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetWidgetMessageCountOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetDashboardInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetDashboardOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetWidgetInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetWidgetOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetWidgetOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.dashboard.Widgets;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.dashboard.WidgetsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.dashboardrecord.DashboardList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.dashboardrecord.DashboardListBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;

/**
 * @author Sunaina Khanna
 * 
 *         This class consists of junit test cases for dashboardImpl .
 */

public class CentinelDashboardImplTest {
    @Mock
    private DataBroker mockDataBroker;
    private final ExecutorService mockExecutor;

    @Mock
    private StreamhandlerImpl mockStreamhandlerImpl;
    private StreamCounterInfoCache mockStreamCounterInfoCache;
    private CentinelDashboardImpl mockCentinelDashboardImpl;
    private CentinelDashboardImplFactory centinelDashboardImplFactory = new CentinelDashboardImplFactory();
    private MockCentinelDashboardImpl myMock = new MockCentinelDashboardImpl();
    public static final InstanceIdentifier<DashboardRecord> mockDashboardRecordRecordId = InstanceIdentifier.builder(
            DashboardRecord.class).build();

    public CentinelDashboardImplTest() {
        mockExecutor = Executors.newFixedThreadPool(1);
    }

    CentinelDashboardImplTest(StreamhandlerImpl streamhandlerImpl) {
        mockExecutor = Executors.newFixedThreadPool(1);
        this.mockStreamhandlerImpl = streamhandlerImpl;
    }

    /**
     * Test method for WidgetInputRule on valid inputs
     */
    @Before
    public void beforeTest() {
        MockitoAnnotations.initMocks(this);
        mockCentinelDashboardImpl = new CentinelDashboardImpl();
        mockStreamCounterInfoCache = StreamCounterInfoCache.getInstance();
        mockStreamhandlerImpl = new StreamhandlerImpl();
        myMock.setDataProvider(mockDataBroker);
    }

    @After
    public void AfterTest() {
        mockCentinelDashboardImpl = null;
        mockStreamCounterInfoCache = null;
        mockStreamhandlerImpl = null;
    }

    @Test
    public void testWidgetInputRuleDashboardNull() {
        SetWidgetInput input = centinelDashboardImplFactory.setInputForWidgetModeNull();
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        Future<RpcResult<SetWidgetOutput>> futureOutput = myMock.setWidget(input);
        boolean caught = false;
        try {
            if (!futureOutput.get().getErrors().isEmpty()) {
                caught = true;
            }
        } catch (Exception e) {
        }
        assertTrue(caught);
    }

    @Test
    public void testSetWidgetInputRuleDashboardNull() {
        SetWidgetInput inputDashboardNull = centinelDashboardImplFactory.setInputForWidgetDashboardNull();
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        Future<RpcResult<SetWidgetOutput>> futureOutput = myMock.setWidget(inputDashboardNull);
        boolean caught = false;
        try {
            if (!futureOutput.get().getErrors().isEmpty()) {
                caught = true;
            }
        } catch (Exception e) {
        }
        assertTrue(caught);
    }

    @Test
    public void testgetWidgetHistogramTestWhenListNull() {
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        GetWidgetHistogramInput input = centinelDashboardImplFactory.setInputForGetWidgetHistogramInput();
        Future<RpcResult<GetWidgetHistogramOutput>> futureOutput = myMock.getWidgetHistogram(input);
    }

    @Test
    public void testgetWidgetHistogramTestWhenListNotNull() {
        boolean caught = false;
        try {
            ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
            doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
            GetWidgetHistogramInput input = centinelDashboardImplFactory.setInputForGetWidgetHistogramInput();
            WidgetStreamCounterVO mockWidgetStreamCounterVO = new WidgetStreamCounterVO();
            mockWidgetStreamCounterVO.setWidgetID(input.getWidgetID());
            List<WidgetStreamCounterVO> listofwidgets = mockStreamCounterInfoCache.getListofcounter();
            listofwidgets.add(mockWidgetStreamCounterVO);
            Future<RpcResult<GetWidgetHistogramOutput>> futureOutput = myMock.getWidgetHistogram(input);
        } catch (Exception e) {
            caught = true;
        }
        assertTrue(caught);
    }

    @Test
    public void testDashboardIncrementTest() {
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        DashboardIncrementTestInput input = centinelDashboardImplFactory.setInputForDashboardIncrementTest();
        Future<RpcResult<DashboardIncrementTestOutput>> futureOutput = myMock.dashboardIncrementTest(input);
        assertNotNull(futureOutput);
    }

    @Test
    public void testsetDashboard() {
        try {
            ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
            doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
            SetDashboardInput input = centinelDashboardImplFactory.setInputForDashboard();
            List<DashboardList> Listofmerge = new ArrayList<DashboardList>();
            DashboardList dashboard;
            dashboard = new DashboardListBuilder().setDescription(input.getDescription()).setTitle(input.getTitle())
                    .build();
            Listofmerge.add(dashboard);
            mockReadWriteTx.merge(LogicalDatastoreType.OPERATIONAL, MockCentinelDashboardImpl.dashboardRecordRecordId,
                    new DashboardRecordBuilder().setDashboardList(Listofmerge).build(), true);
            doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx).submit();
            Future<RpcResult<SetDashboardOutput>> futureOutput = myMock.setDashboard(input);
            SetDashboardOutput out = futureOutput.get().getResult();
            assertEquals(input.getTitle(), out.getTitle());
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getCause().toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail(e.getCause().toString());
        }
    }

    @Test
    public void testWidgetInputRuleWidgetNull() {
        SetWidgetInput inputWidgetNull = centinelDashboardImplFactory.setInputForWidgetNull();
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        Future<RpcResult<SetWidgetOutput>> futureOutput = myMock.setWidget(inputWidgetNull);
        boolean caught = false;
        try {
            if (!futureOutput.get().getErrors().isEmpty()) {
                caught = true;
            }
        } catch (Exception e) {
        }
        assertTrue(caught);
    }

    @Test
    public void testWidgetInputRuleWidgetIdNullDashboardIsNull() {
        SetWidgetInput inputWidgetIdNull = centinelDashboardImplFactory.setInputForWidgetIdNullDashboardIsNull();
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        Future<RpcResult<SetWidgetOutput>> futureOutput = myMock.setWidget(inputWidgetIdNull);
        boolean caught = false;
        try {
            if (!futureOutput.get().getErrors().isEmpty()) {
                caught = true;
            }
        } catch (Exception e) {
        }
        assertTrue(caught);
    }

    @Test
    public void testWidgetInputRuleModeIsNull() {
        SetWidgetInput inputWidgetIdNull = centinelDashboardImplFactory.setInputForModeIsNull();
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        Future<RpcResult<SetWidgetOutput>> futureOutput = myMock.setWidget(inputWidgetIdNull);
        boolean caught = false;
        try {
            if (!futureOutput.get().getErrors().isEmpty()) {
                caught = true;
            }
        } catch (Exception e) {
        }
        assertTrue(caught);
    }

    @Test
    public void testWidgetInputRuleWidgetIdNull() {
        SetWidgetInput inputWidgetIdNull = centinelDashboardImplFactory.setInputForWidgetIdNull();
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        Future<RpcResult<SetWidgetOutput>> futureOutput = myMock.setWidget(inputWidgetIdNull);
        assertNotNull(futureOutput);
    }

    @Test
    public void testWidgetInputRule() {
        SetWidgetInput input = centinelDashboardImplFactory.setInputForWidget();
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        Future<RpcResult<SetWidgetOutput>> futureOutput = myMock.setWidget(input);
        final SetWidgetOutputBuilder mockSetWidgetOutputBuilder = new SetWidgetOutputBuilder();
        List<Widgets> listofwidgets = new ArrayList<Widgets>();

        Widgets widget;
        widget = new WidgetsBuilder().setDashboardID(input.getDashboardID()).setDescription(input.getDescription())
                .setStreamID(input.getStreamID()).setType(input.getType()).setAlertID(input.getAlertID())
                .setMode(input.getMode()).setTimeRange(input.getTimeRange()).setWidgetID(input.getWidgetID()).build();
        listofwidgets.add(widget);
        DashboardList newitem = new DashboardListBuilder().setWidgets(listofwidgets)
                .setDashboardID(input.getDashboardID()).build();

        List<DashboardList> Listofmerge = new ArrayList<DashboardList>();
        Listofmerge.add(newitem);
        mockReadWriteTx.merge(LogicalDatastoreType.OPERATIONAL, mockDashboardRecordRecordId,
                new DashboardRecordBuilder().setDashboardList(Listofmerge)

                .build(), false);
        mockReadWriteTx.submit();
        mockSetWidgetOutputBuilder.setDashboardID(newitem.getDashboardID());
        mockSetWidgetOutputBuilder.setStreamID(newitem.getWidgets().get(0).getStreamID());
        mockSetWidgetOutputBuilder.setWidgetID(newitem.getWidgets().get(0).getWidgetID());
        try {
            boolean caught = false;
            SetWidgetOutput out = futureOutput.get().getResult();
            String expectedMsg = centinelDashboardImplFactory.setInputForWidget().toString();
            if (futureOutput.get().getResult().getDashboardID().equals("1001")) {
                caught = true;
            }
            assertTrue(caught);

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getCause().toString());
        } catch (ExecutionException e) {
            e.printStackTrace();

        }

    }

    @Test
    public void deleteDashboardTestDashboardListIsNull() {
        DeleteDashboardInput input = centinelDashboardImplFactory.mockDeleteWidgetInputDashboardListIsNull();
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        Future<RpcResult<DeleteDashboardOutput>> futureOutput = myMock.deleteDashboard(input);
        boolean caught = false;
        try {
            if (!futureOutput.get().getErrors().isEmpty()) {
                caught = true;
            }
        } catch (Exception e) {
        }
        assertTrue(caught);
    }

    @Test
    public void deleteDashboardTestDashboardListValues() {
        DeleteDashboardInput input = centinelDashboardImplFactory.mockDeleteWidgetInputDashboardListValues();
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        DashboardList dashboardtodelete = new DashboardListBuilder().setDashboardID(input.getDashboardID()).build();
        mockReadWriteTx.read(LogicalDatastoreType.OPERATIONAL,
                mockDashboardRecordRecordId.child(DashboardList.class, dashboardtodelete.getKey()));
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        mockReadWriteTx.submit();
        Future<RpcResult<DeleteDashboardOutput>> futureOutput = myMock.deleteDashboard(input);
        boolean caught = false;
        try {
            if (!futureOutput.get().getErrors().isEmpty()) {
                caught = true;
            }
        } catch (Exception e) {
        }
        assertTrue(caught);
    }

    @Test
    public void deleteWidgetTest() {

        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        DeleteWidgetInput input = centinelDashboardImplFactory.mockDeleteWidgetInput();
        DashboardList dashboardtodelete = new DashboardListBuilder().setDashboardID(input.getDashboardID()).build();
        Widgets widgettodelete = new WidgetsBuilder().setWidgetID(input.getWidgetID()).build();
        mockReadWriteTx.delete(LogicalDatastoreType.OPERATIONAL, MockCentinelDashboardImpl.dashboardRecordRecordId
                .child(DashboardList.class, dashboardtodelete.getKey()).child(Widgets.class, widgettodelete.getKey()));
        mockReadWriteTx.submit();
        doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx).submit();
        Future<RpcResult<DeleteWidgetOutput>> futureOutput = myMock.deleteWidget(input);
        assertNotNull(futureOutput);
    }

    @Test
    public void getWidgetMessageCountOutputTest() {
        Integer value = null;
        StreamCounterInfoCache streamCounterInfoCache = StreamCounterInfoCache.getInstance();
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        GetWidgetMessageCountInput input = centinelDashboardImplFactory.mockGetWidgetMessageCountInput();
        value = streamCounterInfoCache.getCounterValue(input.getWidgetID());
        Future<RpcResult<GetWidgetMessageCountOutput>> futureOutput = myMock.getWidgetMessageCount(input);
        assertNotNull(futureOutput);
    }

    @Test
    public void close() {
        try {
            myMock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MockCentinelDashboardImpl extends CentinelDashboardImpl {

    }

}
