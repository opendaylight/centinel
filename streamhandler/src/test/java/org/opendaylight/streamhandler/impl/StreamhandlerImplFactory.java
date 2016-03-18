/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryEventsInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryEventsInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryLuceneApiInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryLuceneApiInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryLuceneRelativeApiInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryLuceneRelativeApiInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlApiInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlApiInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlRelativeApiInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlRelativeApiInputBuilder;

public class StreamhandlerImplFactory {

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNullQuery() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString(null);
        querySqlRelativeApiInputBuilder.setEventFields(null);
        querySqlRelativeApiInputBuilder.setLimit(null);
        querySqlRelativeApiInputBuilder.setTimeRange(null);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNullLimit() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString("select '1' from centinel");
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNotNullLimitStreamQuery() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString("select stream.stringdata from centinel limit 50");
        querySqlRelativeApiInputBuilder.setLimit((short) 50);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNullLimitStreamQuery() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString("select stream from centinel limit 50");
        querySqlRelativeApiInputBuilder.setLimit((short) 50);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNullLimitAlertQuery() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString("select alert from centinel limit 50");
        querySqlRelativeApiInputBuilder.setLimit((short) 50);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNullLimitDashboardQuery() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString("select dashboard from centinel limit 50");
        querySqlRelativeApiInputBuilder.setLimit((short) 50);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNullLimitDataQuery() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString("select data from centinel limit 50");
        querySqlRelativeApiInputBuilder.setLimit((short) 50);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNotNullTimeQueryContainDot() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString("select stream.data from centinel limit 50");
        querySqlRelativeApiInputBuilder.setLimit((short) 50);
        querySqlRelativeApiInputBuilder.setTimeRange((short) 50);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNotNullTimeQueryContainDotWhereClause() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString("select stream from centinel where stream.event_timestamp>0");
        querySqlRelativeApiInputBuilder.setLimit((short) 50);
        querySqlRelativeApiInputBuilder.setTimeRange((short) 50);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNotNullTimeAlertQueryContainDotWhereClause() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString("select alert from centinel where alert.event_timestamp>0");
        querySqlRelativeApiInputBuilder.setLimit((short) 50);
        querySqlRelativeApiInputBuilder.setTimeRange((short) 50);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNotNullTimeDashboardQueryContainDotWhereClause() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder
                .setQueryString("select dashboard from centinel where dashboard.event_timestamp>0");
        querySqlRelativeApiInputBuilder.setLimit((short) 50);
        querySqlRelativeApiInputBuilder.setTimeRange((short) 50);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNotNullTimeDataQueryContainDotWhereClause() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString("select stringdata from centinel ");
        querySqlRelativeApiInputBuilder.setLimit((short) 50);
        querySqlRelativeApiInputBuilder.setTimeRange((short) 50);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNotNullTimeStreamContainSpace() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString("select stream from centinel limit 50");
        querySqlRelativeApiInputBuilder.setLimit((short) 50);
        querySqlRelativeApiInputBuilder.setTimeRange((short) 50);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNotNullTimeStreamContainDot() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString("select stream.data from centinel limit 50");
        querySqlRelativeApiInputBuilder.setLimit((short) 50);
        querySqlRelativeApiInputBuilder.setTimeRange((short) 50);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNotNullTimeStreamContainDotDataError() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString("select stream. from centinel limit 50");
        querySqlRelativeApiInputBuilder.setLimit((short) 50);
        querySqlRelativeApiInputBuilder.setTimeRange((short) 50);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNotNullTimeAlertContainSpace() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString("select alert from centinel limit 50");
        querySqlRelativeApiInputBuilder.setLimit((short) 50);
        querySqlRelativeApiInputBuilder.setTimeRange((short) 50);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlRelativeApiInput inputForQuerySqlRelativeApiNotNullTimeDashboardContainSpace() {
        QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder = new QuerySqlRelativeApiInputBuilder();
        querySqlRelativeApiInputBuilder.setQueryString("select dashboard from centinel limit 50");
        querySqlRelativeApiInputBuilder.setLimit((short) 50);
        querySqlRelativeApiInputBuilder.setTimeRange((short) 50);
        return querySqlRelativeApiInputBuilder.build();
    }

    public QuerySqlApiInput inputForQuerySqlApiQueryNull() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString(null);
        return querySqlApiInput.build();
    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNull() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select 1 from centinel");
        querySqlApiInput.setLimit(null);
        return querySqlApiInput.build();
    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNullStream() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select stream from centinel");
        querySqlApiInput.setLimit((short) 50);
        querySqlApiInput.setFromTime("12");
        querySqlApiInput.setToTime("200");
        return querySqlApiInput.build();
    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNullAlertWhere() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select alert from centinel where alert.event_timestamp>0");
        querySqlApiInput.setLimit((short) 50);
        querySqlApiInput.setFromTime("12");
        querySqlApiInput.setToTime("200");
        return querySqlApiInput.build();
    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNullDashboardWhere() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select dashboard from centinel where dashboard.event_timestamp>0");
        querySqlApiInput.setLimit((short) 50);
        querySqlApiInput.setFromTime("12");
        querySqlApiInput.setToTime("200");
        return querySqlApiInput.build();
    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNullStringData() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select stringdata from centinel");
        querySqlApiInput.setLimit((short) 50);
        querySqlApiInput.setFromTime("12");
        querySqlApiInput.setToTime("200");
        return querySqlApiInput.build();
    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNullStreamDot() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select stream. from centinel");
        querySqlApiInput.setLimit((short) 50);
        querySqlApiInput.setFromTime("12");
        querySqlApiInput.setToTime("200");
        return querySqlApiInput.build();

    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNullAlert() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select alert from centinel");
        querySqlApiInput.setLimit((short) 50);
        querySqlApiInput.setFromTime("12");
        querySqlApiInput.setToTime("200");
        return querySqlApiInput.build();
    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNullAlertDot() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select alert. from centinel");
        querySqlApiInput.setLimit((short) 50);
        querySqlApiInput.setFromTime("12");
        querySqlApiInput.setToTime("200");
        return querySqlApiInput.build();

    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNullDashboard() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select dashboard from centinel");
        querySqlApiInput.setLimit((short) 50);
        querySqlApiInput.setFromTime("12");
        querySqlApiInput.setToTime("200");
        return querySqlApiInput.build();
    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNullData() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select data from centinel");
        querySqlApiInput.setLimit((short) 50);
        querySqlApiInput.setFromTime("12");
        querySqlApiInput.setToTime("200");
        return querySqlApiInput.build();
    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNullNone() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select 1 from centinel");
        querySqlApiInput.setLimit((short) 50);
        querySqlApiInput.setFromTime("12");
        querySqlApiInput.setToTime("200");
        return querySqlApiInput.build();
    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNullTimeNullStream() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select stream from centinel");
        querySqlApiInput.setLimit((short) 50);
        querySqlApiInput.setFromTime(null);
        querySqlApiInput.setToTime(null);
        return querySqlApiInput.build();

    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNullTimeNullAlert() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select alert from centinel");
        querySqlApiInput.setLimit((short) 50);
        querySqlApiInput.setFromTime(null);
        querySqlApiInput.setToTime(null);
        return querySqlApiInput.build();
    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNullTimeNullDashboard() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select dashboard from centinel");
        querySqlApiInput.setLimit((short) 50);
        querySqlApiInput.setFromTime(null);
        querySqlApiInput.setToTime(null);
        return querySqlApiInput.build();
    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNullTimeNullData() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select stringdata from centinel");
        querySqlApiInput.setLimit((short) 50);
        querySqlApiInput.setFromTime(null);
        querySqlApiInput.setToTime(null);
        return querySqlApiInput.build();
    }

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNullTimeNullNone() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select 1 from centinel");
        querySqlApiInput.setLimit((short) 50);
        querySqlApiInput.setFromTime(null);
        querySqlApiInput.setToTime(null);
        return querySqlApiInput.build();
    }

    public QueryEventsInput inputQueryEvents() {
        QueryEventsInputBuilder queryEventsInputBuilder = new QueryEventsInputBuilder();
        queryEventsInputBuilder.setEventType("STREAM");
        queryEventsInputBuilder.setQueryString("select * from centinel");
        return queryEventsInputBuilder.build();
    }

    public QueryLuceneRelativeApiInput inputQueryLuceneRelativeApi() {
        QueryLuceneRelativeApiInputBuilder queryLuceneRelativeApiInput = new QueryLuceneRelativeApiInputBuilder();
        queryLuceneRelativeApiInput.setQueryString("select * from centinel");
        return queryLuceneRelativeApiInput.build();
    }

    public QueryLuceneApiInput inputQueryLuceneApi() {
        QueryLuceneApiInputBuilder queryLuceneApiInput = new QueryLuceneApiInputBuilder();
        queryLuceneApiInput.setQueryString("select * from centinel");
        return queryLuceneApiInput.build();
    }

}
