/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.analytics.impl.rev141210;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;


/**
 * @author Amandeep Singh
 *
 */
public class AnalyticSModule
        extends
        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.analytics.impl.rev141210.AbstractAnalyticSModule {
    public AnalyticSModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier,
            org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public AnalyticSModule(
            org.opendaylight.controller.config.api.ModuleIdentifier identifier,
            org.opendaylight.controller.config.api.DependencyResolver dependencyResolver,
            org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.analytics.impl.rev141210.AnalyticSModule oldModule,
            java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
	
        DataBroker dataBrokerService = getDataBrokerDependency();

	final class AutoCloseableAnalyticSCentinel implements AutoCloseable {

            @Override
            public void close() throws Exception {
                

            }

            private void closeQuietly(final AutoCloseable resource) {
                try {
                    resource.close();
                } catch (final Exception e) {
                }
            }
        }

        AutoCloseable ret = new AutoCloseableAnalyticSCentinel();
        return ret;

    }

}
