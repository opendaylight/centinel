/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import org.opendaylight.centinel.impl.dashboard.CentinelDashboardImpl;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.RpcRegistration;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DashboardruleService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.StreamhandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamhandlerProvider implements AutoCloseable, BindingAwareProvider {

    private static final Logger LOG = LoggerFactory.getLogger(StreamhandlerProvider.class);
    private RpcRegistration<StreamhandlerService> streamhandlerService;
    private RpcRegistration<DashboardruleService> dashboardruleService;

    @Override
    public void onSessionInitiated(ProviderContext session) {
        dashboardruleService = session.addRpcImplementation(DashboardruleService.class, new CentinelDashboardImpl());
        LOG.info("Stream handler provider initated");
    }

    @Override
    public void close() throws Exception {
        if (dashboardruleService != null) {
            dashboardruleService.close();
        }
        LOG.info("Stream handler provider Closed");
    }

}
