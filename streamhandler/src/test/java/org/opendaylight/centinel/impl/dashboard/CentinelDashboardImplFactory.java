/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.dashboard;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DashboardIncrementTestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DashboardIncrementTestInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DeleteDashboardInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DeleteDashboardInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DeleteWidgetInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DeleteWidgetInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetWidgetHistogramInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetWidgetHistogramInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetWidgetMessageCountInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetWidgetMessageCountInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetDashboardInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetDashboardInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetDashboardOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetDashboardOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetWidgetInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetWidgetInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.WidgetMode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.dashboardrecord.DashboardList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.dashboardrecord.DashboardListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.Subscribe.Mode;

public class CentinelDashboardImplFactory {

    /**
     * Provided dummy params for SetDashboard
     */
    public SetDashboardInput setInputForDashboard() {
        SetDashboardInputBuilder setDashboardInputBuilder = new SetDashboardInputBuilder();
        setDashboardInputBuilder.setDescription("description");
        setDashboardInputBuilder.setTitle("title");
        return setDashboardInputBuilder.build();
    }

    /**
     * Provided dummy params for SetWidget
     */
    public SetWidgetInput setInputForWidget() {
        SetWidgetInputBuilder setInputForWidgetBuilder = new SetWidgetInputBuilder();
        setInputForWidgetBuilder.setAlertID("1001");
        setInputForWidgetBuilder.setDashboardID("1001");
        setInputForWidgetBuilder.setDescription("rule1");
        WidgetMode mockWidgetMode = WidgetMode.Alert;
        setInputForWidgetBuilder.setMode(mockWidgetMode);
        setInputForWidgetBuilder.setStreamID("1001");
        setInputForWidgetBuilder.setTimeRange((short) 4);
        setInputForWidgetBuilder.setWidgetID("1001");
        return setInputForWidgetBuilder.build();
    }

    /**
     * Provided dummy params for SetWidget
     */
    public SetWidgetInput setInputForWidgetDashboardNull() {
        SetWidgetInputBuilder setInputForWidgetBuilder = new SetWidgetInputBuilder();
        setInputForWidgetBuilder.setAlertID("1001");
        setInputForWidgetBuilder.setDescription("rule1");
        setInputForWidgetBuilder.setStreamID("1001");
        setInputForWidgetBuilder.setTimeRange((short) 4);
        setInputForWidgetBuilder.setWidgetID("1001");
        return setInputForWidgetBuilder.build();
    }

    /**
     * Provided dummy params for SetWidget
     */
    public SetWidgetInput setInputForWidgetNull() {
        SetWidgetInputBuilder setInputForWidgetBuilder = new SetWidgetInputBuilder();
        setInputForWidgetBuilder.setAlertID("1001");
        setInputForWidgetBuilder.setDescription("rule1");
        setInputForWidgetBuilder.setMode(null);
        setInputForWidgetBuilder.setStreamID("1001");
        setInputForWidgetBuilder.setTimeRange((short) 4);
        setInputForWidgetBuilder.setWidgetID("1001");
        return setInputForWidgetBuilder.build();
    }

    /**
     * Provided dummy params for SetWidget
     */
    public SetWidgetInput setInputForWidgetModeNull() {
        SetWidgetInputBuilder setInputForWidgetBuilder = new SetWidgetInputBuilder();
        setInputForWidgetBuilder.setDashboardID("100");
        setInputForWidgetBuilder.setAlertID("1001");
        setInputForWidgetBuilder.setDescription("rule1");
        setInputForWidgetBuilder.setStreamID("1001");
        setInputForWidgetBuilder.setTimeRange((short) 4);
        setInputForWidgetBuilder.setWidgetID("1001");
        return setInputForWidgetBuilder.build();
    }

    /**
     * Provided dummy params for SetWidget
     */
    public SetWidgetInput setInputForWidgetIdNullDashboardIsNull() {
        SetWidgetInputBuilder setInputForWidgetBuilder = new SetWidgetInputBuilder();
        setInputForWidgetBuilder.setAlertID("1001");
        setInputForWidgetBuilder.setDescription("rule1");
        setInputForWidgetBuilder.setMode(null);
        setInputForWidgetBuilder.setStreamID("1001");
        setInputForWidgetBuilder.setTimeRange((short) 4);
        setInputForWidgetBuilder.setWidgetID(null);
        return setInputForWidgetBuilder.build();
    }

    /**
     * Provided dummy params for SetWidget
     */
    public SetWidgetInput setInputForModeIsNull() {
        SetWidgetInputBuilder setInputForWidgetBuilder = new SetWidgetInputBuilder();
        setInputForWidgetBuilder.setAlertID("1001");
        setInputForWidgetBuilder.setDescription("rule1");
        setInputForWidgetBuilder.setMode(null);
        setInputForWidgetBuilder.setStreamID("1001");
        setInputForWidgetBuilder.setDashboardID("1001");
        setInputForWidgetBuilder.setTimeRange((short) 4);
        setInputForWidgetBuilder.setWidgetID(null);
        return setInputForWidgetBuilder.build();
    }

    /**
     * Provided dummy params for SetWidget
     */
    public SetWidgetInput setInputForWidgetIdNull() {
        SetWidgetInputBuilder setInputForWidgetBuilder = new SetWidgetInputBuilder();
        setInputForWidgetBuilder.setAlertID("1001");
        setInputForWidgetBuilder.setDescription("rule1");
        setInputForWidgetBuilder.setMode(WidgetMode.Stream);
        setInputForWidgetBuilder.setStreamID("1001");
        setInputForWidgetBuilder.setDashboardID("1001");
        setInputForWidgetBuilder.setTimeRange((short) 4);
        setInputForWidgetBuilder.setWidgetID(null);
        return setInputForWidgetBuilder.build();
    }

    /**
     * Providing dummy params for DeleteDashboard
     */
    public DeleteDashboardInput mockDeleteWidgetInputDashboardListIsNull() {
        DeleteDashboardInputBuilder deleteDashboardInputBuilder = new DeleteDashboardInputBuilder();
        return deleteDashboardInputBuilder.build();
    }

    /**
     * Providing dummy params for DeleteDashboard
     */
    public DeleteDashboardInput mockDeleteWidgetInputDashboardListValues() {
        DeleteDashboardInputBuilder deleteDashboardInputBuilder = new DeleteDashboardInputBuilder();
        deleteDashboardInputBuilder.setDashboardID("1001");
        return deleteDashboardInputBuilder.build();
    }

    /**
     * Providing dummy params for DeleteDashboard
     */
    public DeleteWidgetInput mockDeleteWidgetInput() {
        DeleteWidgetInputBuilder deleteWidgetInputBuilder = new DeleteWidgetInputBuilder();
        // deleteWidgetInputBuilder.setDashboardID("1001");
        return deleteWidgetInputBuilder.build();
    }

    /**
     * Mock params for SetDashboard Rpc
     */
    public SetDashboardOutput expectedDashboardObject() {
        SetDashboardOutputBuilder setDashboardOutputBuilder = new SetDashboardOutputBuilder();

        setDashboardOutputBuilder.setTitle("description");
        setDashboardOutputBuilder.setDescription("title");
        return setDashboardOutputBuilder.build();
    }

    /**
     * Mock params for GetWidgetMessageCount Rpc
     */
    public GetWidgetMessageCountInput mockGetWidgetMessageCountInput() {
        GetWidgetMessageCountInputBuilder getWidgetMessageCountInputBuilder = new GetWidgetMessageCountInputBuilder();
        getWidgetMessageCountInputBuilder.setWidgetID("100");
        return getWidgetMessageCountInputBuilder.build();
    }

    /**
     * Mock params for DashboardIncrementTest Rpc
     */
    public DashboardIncrementTestInput setInputForDashboardIncrementTest() {
        DashboardIncrementTestInputBuilder dashboardIncrementTestInputBuilder = new DashboardIncrementTestInputBuilder();
        dashboardIncrementTestInputBuilder.setStreamID("100");
        return dashboardIncrementTestInputBuilder.build();
    }

    /**
     * Mock params for GetWidgetHistogram Rpc
     */
    public GetWidgetHistogramInput setInputForGetWidgetHistogramInput() {
        GetWidgetHistogramInputBuilder getWidgetHistogramInputBuilder = new GetWidgetHistogramInputBuilder();
        getWidgetHistogramInputBuilder.setDashboardID("100");
        getWidgetHistogramInputBuilder.setFromTimestamp("1");
        getWidgetHistogramInputBuilder.setNow(true);
        getWidgetHistogramInputBuilder.setToTimestamp("10");
        getWidgetHistogramInputBuilder.setWidgetID("100");
        return getWidgetHistogramInputBuilder.build();
    }

    /**
     * Mock params for GetWidgetHistogram Rpc
     */
    public GetWidgetHistogramInput setInputForGetWidgetHistogramInputListNotNull() {
        GetWidgetHistogramInputBuilder getWidgetHistogramInputBuilder = new GetWidgetHistogramInputBuilder();
        getWidgetHistogramInputBuilder.setDashboardID("100");
        getWidgetHistogramInputBuilder.setFromTimestamp("1");
        getWidgetHistogramInputBuilder.setNow(true);
        getWidgetHistogramInputBuilder.setToTimestamp("10");
        getWidgetHistogramInputBuilder.setWidgetID("100");
        return getWidgetHistogramInputBuilder.build();
    }
    
    public String inputForRemoveCounterForWidget() {
        return "100";
    }

}
