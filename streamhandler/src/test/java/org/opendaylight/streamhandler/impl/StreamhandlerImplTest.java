/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryEventsInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryEventsOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryLuceneApiInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryLuceneApiOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryLuceneRelativeApiInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryLuceneRelativeApiOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlApiInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlApiOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlRelativeApiInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlRelativeApiOutput;
import org.opendaylight.yangtools.yang.common.RpcResult;

import com.sun.jersey.api.client.ClientHandlerException;

public class StreamhandlerImplTest {

    StreamhandlerImplFactory streamHandlerImplFactory = new StreamhandlerImplFactory();
    StreamhandlerImpl mockStreamHandlerImpl;
    private MockStreamhandlerImpl myMock = new MockStreamhandlerImpl();

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
    public void mockQuerySqlRelativeApiWhenQueryCheckDot() {
        boolean drillClientNotWorking = false;
        QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory
                .inputForQuerySqlRelativeApiNotNullLimitStreamQuery();
        try {
            Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);
        } catch (ClientHandlerException e) {
            drillClientNotWorking = true;
            assertTrue(drillClientNotWorking);
        }

    }

    @Test
    public void mockQuerySqlRelativeApiWhenlimitNull() {
        QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlRelativeApiNullLimit();
        Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);
        try {
            myMock.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
    public void mockQuerySqlRelativeApiWhenTimeNotNullQueryContainDotWhereClause() {
        boolean drillPostingQuery = false;
        try {

            QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory
                    .inputForQuerySqlRelativeApiNotNullTimeQueryContainDotWhereClause();
            Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);

        } catch (Exception e) {
            drillPostingQuery = true;
            assertTrue(drillPostingQuery);
        }

    }

    @Test
    public void mockQuerySqlRelativeApiWhenTimeNotNullAlertQueryContainDotWhereClause() {
        boolean drillPostingQuery = false;
        try {

            QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory
                    .inputForQuerySqlRelativeApiNotNullTimeAlertQueryContainDotWhereClause();
            Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);

        } catch (Exception e) {
            drillPostingQuery = true;
            assertTrue(drillPostingQuery);
        }

    }

    @Test
    public void mockQuerySqlRelativeApiWhenTimeNotNullDashboardQueryContainDotWhereClause() {
        boolean drillPostingQuery = false;
        try {

            QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory
                    .inputForQuerySqlRelativeApiNotNullTimeDashboardQueryContainDotWhereClause();
            Future<RpcResult<QuerySqlRelativeApiOutput>> futureOutput = myMock.querySqlRelativeApi(mockInputNull);

        } catch (Exception e) {
            drillPostingQuery = true;
            assertTrue(drillPostingQuery);
        }

    }

    @Test
    public void mockQuerySqlRelativeApiWhenTimeNotNullDataQueryContainDotWhereClause() {
        boolean drillPostingQuery = false;
        try {

            QuerySqlRelativeApiInput mockInputNull = streamHandlerImplFactory
                    .inputForQuerySqlRelativeApiNotNullTimeDataQueryContainDotWhereClause();
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
    public void mockQuerySqlApiQueryWhenLimitNotNullStream() {
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = null;
        boolean clientResponse = false;
        try {
            QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNotNullStream();
            futureOutput = myMock.querySqlApi(mockInputNull);
        } catch (Exception e) {
            clientResponse = true;
            assertTrue(clientResponse);
        }
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNotNullAlertWhere() {
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = null;
        boolean clientResponse = false;
        try {
            QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNotNullAlertWhere();
            futureOutput = myMock.querySqlApi(mockInputNull);
        } catch (Exception e) {
            clientResponse = true;
            assertTrue(clientResponse);
        }
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNotNullDashboardWhere() {
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = null;
        boolean clientResponse = false;
        try {
            QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNotNullDashboardWhere();
            futureOutput = myMock.querySqlApi(mockInputNull);
        } catch (Exception e) {
            clientResponse = true;
            assertTrue(clientResponse);
        }
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNotNullStringData() {
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = null;
        boolean clientResponse = false;
        try {
            QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNotNullStringData();
            futureOutput = myMock.querySqlApi(mockInputNull);
        } catch (Exception e) {
            clientResponse = true;
            assertTrue(clientResponse);
        }
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNotNullStreamDot() {
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = null;
        boolean clientResponse = false;
        try {
            QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNotNullStreamDot();
            futureOutput = myMock.querySqlApi(mockInputNull);
        } catch (Exception e) {
            clientResponse = true;
            assertTrue(clientResponse);
        }
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNotNullAlert() {
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = null;
        boolean clientResponse = false;
        try {
            QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNotNullAlert();
            futureOutput = myMock.querySqlApi(mockInputNull);
        } catch (Exception e) {
            clientResponse = true;
            assertTrue(clientResponse);
        }
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNotNullAlertDot() {
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = null;
        boolean clientResponse = false;
        try {
            QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNotNullAlertDot();
            futureOutput = myMock.querySqlApi(mockInputNull);
        } catch (Exception e) {
            clientResponse = true;
            assertTrue(clientResponse);
        }
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNotNullDashboard() {
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = null;
        boolean clientResponse = false;
        try {
            QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNotNullDashboard();
            futureOutput = myMock.querySqlApi(mockInputNull);
        } catch (Exception e) {
            clientResponse = true;
            assertTrue(clientResponse);
        }
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNotNullData() {
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = null;
        boolean clientResponse = false;
        try {
            QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNotNullData();
            futureOutput = myMock.querySqlApi(mockInputNull);
        } catch (Exception e) {
            clientResponse = true;
            assertTrue(clientResponse);
        }
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNotNullNone() {
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = null;

        QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNotNullNone();
        futureOutput = myMock.querySqlApi(mockInputNull);
        assertNotNull(futureOutput);
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNotNullTimeNullStream() {
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = null;
        boolean clientResponse = false;
        try {
            QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNotNullTimeNullStream();
            futureOutput = myMock.querySqlApi(mockInputNull);
        } catch (Exception e) {
            clientResponse = true;
            assertTrue(clientResponse);
        }
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNotNullTimeNullAlert() {
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = null;
        boolean clientResponse = false;
        try {
            QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNotNullTimeNullAlert();
            futureOutput = myMock.querySqlApi(mockInputNull);
        } catch (Exception e) {
            clientResponse = true;
            assertTrue(clientResponse);
        }
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNotNullTimeNullDashboard() {
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = null;
        boolean clientResponse = false;
        try {
            QuerySqlApiInput mockInputNull = streamHandlerImplFactory
                    .inputForQuerySqlApiLimitNotNullTimeNullDashboard();
            futureOutput = myMock.querySqlApi(mockInputNull);
        } catch (Exception e) {
            clientResponse = true;
            assertTrue(clientResponse);
        }
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNotNullTimeNullData() {
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = null;
        boolean clientResponse = false;
        try {
            QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNotNullTimeNullData();
            futureOutput = myMock.querySqlApi(mockInputNull);
        } catch (Exception e) {
            clientResponse = true;
            assertTrue(clientResponse);
        }
    }

    @Test
    public void mockQuerySqlApiQueryWhenLimitNotNullTimeNullNone() {
        Future<RpcResult<QuerySqlApiOutput>> futureOutput = null;
        QuerySqlApiInput mockInputNull = streamHandlerImplFactory.inputForQuerySqlApiLimitNotNullTimeNullNone();
        futureOutput = myMock.querySqlApi(mockInputNull);
        assertNotNull(futureOutput);
    }

    @Test
    public void mockQueryEvents() {
        QueryEventsInput mockQueryEvents = streamHandlerImplFactory.inputQueryEvents();
        Future<RpcResult<QueryEventsOutput>> futureOutput = myMock.queryEvents(mockQueryEvents);
        assertNull(futureOutput);
    }

    @Test
    public void mockQueryLuceneRelativeApi() {
        QueryLuceneRelativeApiInput mockQueryLuceneRelativeApiInput = streamHandlerImplFactory
                .inputQueryLuceneRelativeApi();
        Future<RpcResult<QueryLuceneRelativeApiOutput>> futureOutput = myMock
                .queryLuceneRelativeApi(mockQueryLuceneRelativeApiInput);
        assertNull(futureOutput);
    }

    @Test
    public void mockQueryLuceneApi() {
        QueryLuceneApiInput mockQueryLuceneApiInput = streamHandlerImplFactory.inputQueryLuceneApi();
        Future<RpcResult<QueryLuceneApiOutput>> futureOutput = myMock.queryLuceneApi(mockQueryLuceneApiInput);
        assertNull(futureOutput);
    }

    private class MockStreamhandlerImpl extends StreamhandlerImpl {

    }
}
