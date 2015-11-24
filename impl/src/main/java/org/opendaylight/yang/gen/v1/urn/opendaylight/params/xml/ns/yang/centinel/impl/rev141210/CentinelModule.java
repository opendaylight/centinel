/*
 * Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.impl.rev141210;

import org.opendaylight.centinel.impl.CentinelAlertConditionImpl;
import org.opendaylight.centinel.impl.CentinelProvider;
import org.opendaylight.centinel.impl.CentinelStreamImpl;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertruleService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CentinelModule extends
        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.impl.rev141210.AbstractCentinelModule {
    private static final Logger log = LoggerFactory.getLogger(CentinelModule.class);

    public CentinelModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier,
            org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public CentinelModule(
            org.opendaylight.controller.config.api.ModuleIdentifier identifier,
            org.opendaylight.controller.config.api.DependencyResolver dependencyResolver,
            org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.impl.rev141210.CentinelModule oldModule,
            java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        final CentinelAlertConditionImpl alertImpl = new CentinelAlertConditionImpl();
        final CentinelStreamImpl streamImpl = new CentinelStreamImpl();
        CentinelProvider provider = new CentinelProvider();
        DataBroker dataBrokerService = getDataBrokerDependency();

        System.out.println("dataBrokerService created:" + dataBrokerService);
        streamImpl.setDataProvider(dataBrokerService);

        alertImpl.setDataProvider(dataBrokerService);
       
        final BindingAwareBroker.RpcRegistration<AlertruleService> rpcRegistration = getRpcRegistryDependency()
                .addRpcImplementation(AlertruleService.class, alertImpl);

        final BindingAwareBroker.RpcRegistration<StreamService> rpcRegistrationStream = getRpcRegistryDependency()
                .addRpcImplementation(StreamService.class, streamImpl);

        getBrokerDependency().registerProvider(provider);

        final class AutoCloseableCentinel implements AutoCloseable {

            @Override
            public void close() throws Exception {
                rpcRegistration.close();
                rpcRegistrationStream.close();
                closeQuietly(alertImpl);
               
                log.info(" provider (instance {}) torn down.", this);
            }

            private void closeQuietly(final AutoCloseable resource) {
                try {
                    resource.close();
                } catch (final Exception e) {
                    log.debug("Ignoring exception while closing {}", resource, e);
                }
            }
        }

        AutoCloseable ret = new AutoCloseableCentinel();
        log.info("Centinel provider (instance {}) initialized.", ret);
        return ret;

        // return provider;
    }

}
