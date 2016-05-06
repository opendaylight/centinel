/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.analytics.impl;

import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Amandeep Singh 
 * 
 * 
 *
 */
public class AnalyticsProvider implements AutoCloseable, BindingAwareProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AnalyticsProvider.class);

    @Override
    public void close() throws Exception {
        LOG.info("Analytics provider Closed");
    }

    @Override
    public void onSessionInitiated(ProviderContext session) {
        LOG.info("Analytics provider Session Initiated");
    }

}
