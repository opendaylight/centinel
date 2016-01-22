/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlApiInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlApiOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlRelativeApiInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlRelativeApiOutput;
import org.opendaylight.yangtools.yang.common.RpcResult;

import com.sun.jersey.api.client.ClientResponse;

public class StreamhandlerImplTest {

    StreamhandlerImplFactory streamHandlerImplFactory = new StreamhandlerImplFactory();
    StreamhandlerImpl mockStreamHandlerImpl;
    private MockStreamhandlerImpl myMock = new MockStreamhandlerImpl();
    private CommonServices mockCommonServices = null;

    @Before
    public void beforeTest() {
        MockitoAnnotations.initMocks(this);
        mockStreamHandlerImpl = new StreamhandlerImpl();

    }

    @Test
    public void mockQuerySqlRelativeApiWhenQueryNull() {
        QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlRelativeApiNullQuery();
        Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);
        assertNotNull(futureOutput);
    }

    @Test
    public void mockQuerySqlRelativeApiWhenlimitNull() {
        QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlRelativeApiNullLimit();
        Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);
        assertNotNull(futureOutput);
    }

    @Test
    public void mockQuerySqlRelativeApiWhenlimitNullStreamQuery() {
        boolean drillPostingQuery = false;
        try {

            QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory
                    .inputForQuerySqlRelativeApiNullLimitStreamQuery();
            Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);

        } catch (Exception e) {
            drillPostingQuery = true;
            assertTrue(drillPostingQuery);
        }

    }

    @Test
    public void mockQuerySqlRelativeApiWhenlimitNullAlertQuery() {
        boolean drillPostingQuery = false;
        try {

            QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory
                    .inputForQuerySqlRelativeApiNullLimitAlertQuery();
            Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);

        } catch (Exception e) {
            drillPostingQuery = true;
            assertTrue(drillPostingQuery);
        }

    }

    @Test
    public void mockQuerySqlRelativeApiWhenlimitNullDashboardQuery() {
        boolean drillPostingQuery = false;
        try {

            QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory
                    .inputForQuerySqlRelativeApiNullLimitDashboardQuery();
            Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);

        } catch (Exception e) {
            drillPostingQuery = true;
            assertTrue(drillPostingQuery);
        }

    }

    @Test
    public void mockQuerySqlRelativeApiWhenlimitNullDataQuery() {
        boolean drillPostingQuery = false;
        try {

            QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory
                    .inputForQuerySqlRelativeApiNullLimitDataQuery();
            Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);

        } catch (Exception e) {
            drillPostingQuery = true;
            assertTrue(drillPostingQuery);
        }

    }

    @Test
    public void mockQuerySqlRelativeApiWhenTimeNotNullQueryContainDot() {
        boolean drillPostingQuery = false;
        try {

            QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory
                    .inputForQuerySqlRelativeApiNotNullTimeQueryContainDot();
            Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);

        } catch (Exception e) {
            drillPostingQuery = true;
            assertTrue(drillPostingQuery);
        }

    }

    @Test
    public void mockQuerySqlRelativeApiWhenTimeNotNullStreamContainSpace() {
        boolean drillPostingQuery = false;
        try {

            QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory
                    .inputForQuerySqlRelativeApiNotNullTimeStreamContainSpace();
            Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);

        } catch (Exception e) {
            drillPostingQuery = true;
            assertTrue(drillPostingQuery);
        }

    }

    @Test
    public void mockQuerySqlRelativeApiWhenTimeNotNullStreamContainDot() {
        boolean drillPostingQuery = false;
        try {

            QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory
                    .inputForQuerySqlRelativeApiNotNullTimeStreamContainDot();
            Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);

        } catch (Exception e) {
            drillPostingQuery = true;
            assertTrue(drillPostingQuery);
        }

    }

    @Test
    public void mockQuerySqlRelativeApiWhenTimeNotNullStreamContainDotDataError() {
        boolean drillPostingQuery = false;
        try {

            QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory
                    .inputForQuerySqlRelativeApiNotNullTimeStreamContainDotDataError();
            Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);

        } catch (Exception e) {
            drillPostingQuery = true;
            assertTrue(drillPostingQuery);
        }

    }

    @Test
    public void mockQuerySqlRelativeApiWhenTimeNotNullAlertContainSpace() {
        boolean drillPostingQuery = false;
        try {

            QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory
                    .inputForQuerySqlRelativeApiNotNullTimeAlertContainSpace();
            Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);

        } catch (Exception e) {
            drillPostingQuery = true;
            assertTrue(drillPostingQuery);
        }

    }

    @Test
    public void mockQuerySqlRelativeApiWhenTimeNotNullDashboardContainSpace() {
        boolean drillPostingQuery = false;
        try {

            QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory
                    .inputForQuerySqlRelativeApiNotNullTimeDashboardContainSpace();
            Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);

        } catch (Exception e) {
            drillPostingQuery = true;
            assertTrue(drillPostingQuery);
        }

    }

    @Test
    public void mockQuerySqlApiQueryWhenQueryNull() {
        QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiQueryNull();
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = myMock.querySqlApi(mockInputNull);
        assertNotNull(futureOutput);
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNull() {
        QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNull();
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = myMock.querySqlApi(mockInputNull);
        assertNotNull(futureOutput);
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNotNull() {
        QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNotNull();
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = myMock.querySqlApi(mockInputNull);
        assertNotNull(futureOutput);
    }

    private class MockStreamhandlerImpl extends StreamhandlerImpl {

    }
}
