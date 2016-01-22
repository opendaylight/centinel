/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

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

    public QuerySqlApiInput inputForQuerySqlApiLimitNotNull() {
        QuerySqlApiInputBuilder querySqlApiInput = new QuerySqlApiInputBuilder();
        querySqlApiInput.setQueryString("select 1 from centinel");
        querySqlApiInput.setLimit((short) 50);
        return querySqlApiInput.build();

    }
}
