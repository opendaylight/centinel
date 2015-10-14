/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl;

import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.RpcRegistration;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertruleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CentinelProvider implements BindingAwareProvider, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(CentinelProvider.class);
    private RpcRegistration<AlertruleService> alertruleService;

    @Override
    public void onSessionInitiated(ProviderContext session) {
        LOG.info("CentinelProvider Session Initiated");
        // alertruleService =
        // session.addRpcImplementation(AlertruleService.class, new
        // CentinelImpl());
    }

    @Override
    public void close() throws Exception {
        LOG.info("CentinelProvider Closed");
        if (alertruleService != null) {
            alertruleService.close();
        }
    }
}
