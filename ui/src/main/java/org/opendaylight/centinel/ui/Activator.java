/*
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.ui;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private static final Logger LOG = LoggerFactory.getLogger(Activator.class);

    private ServiceTracker httpTracker;

    @Override
    public void start(BundleContext context) throws Exception {
        httpTracker = new ServiceTracker(context, HttpService.class.getName(), null) {

            @Override
            public void removedService(ServiceReference reference, Object service) {

                try {
                    ((HttpService) service).unregister("/centinel");
                } catch (Exception exception) {
                    LOG.error("removedService", exception);
                }
            }

            @Override
            public Object addingService(ServiceReference reference) {
                // HTTP service is available, register our servlet...
                HttpService httpService = (HttpService) this.context.getService(reference);
                try {
                    HttpContext defaultHttpContext = httpService.createDefaultHttpContext();
                    httpService.registerResources("/centinel", "/pages", new CentinelUiHttpContext(defaultHttpContext, "/pages/"));
                } catch (NamespaceException exception) {
                    LOG.error("addingService", exception);
                }
                return httpService;
            }
        };
        httpTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        httpTracker.close();
    }

}
