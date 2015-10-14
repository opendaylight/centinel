/*
 * Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.impl.rev141210;

import org.opendaylight.centinel.impl.CentinelImpl;
import org.opendaylight.centinel.impl.CentinelProvider;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertruleService;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
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
        final CentinelImpl centinelImpl = new CentinelImpl();
        CentinelProvider provider = new CentinelProvider();
        DataBroker dataBrokerService = getDataBrokerDependency();
        centinelImpl.setDataProvider(dataBrokerService);

        final ListenerRegistration<DataChangeListener> alertMessageCountRuleChangeListener = dataBrokerService
                .registerDataChangeListener(LogicalDatastoreType.CONFIGURATION,
                        CentinelImpl.alertMessageCountRuleRecordId, centinelImpl, DataChangeScope.SUBTREE);

        final ListenerRegistration<DataChangeListener> alertFeildContentRuleChangeListener = dataBrokerService
                .registerDataChangeListener(LogicalDatastoreType.CONFIGURATION,
                        CentinelImpl.alertFeildContentRuleRecordId, centinelImpl, DataChangeScope.SUBTREE);

        final ListenerRegistration<DataChangeListener> alertFeildValueRuleChangeListener = dataBrokerService
                .registerDataChangeListener(LogicalDatastoreType.CONFIGURATION,
                        CentinelImpl.alertFieldValueRuleRecordId, centinelImpl, DataChangeScope.SUBTREE);

        // final BindingAwareBroker.RpcRegistration<AlertruleService>
        // rpcRegistration = getRpcRegistryDependency()
        // .addRpcImplementation(AlertruleService.class, centinelImpl);
        getBrokerDependency().registerProvider(provider);

        final class AutoCloseableToaster implements AutoCloseable {

            @Override
            public void close() throws Exception {
                alertMessageCountRuleChangeListener.close();
                alertFeildValueRuleChangeListener.close();
                alertFeildContentRuleChangeListener.close();
                // rpcRegistration.close();
                // runtimeReg.close();
                closeQuietly(centinelImpl);
                log.info("Toaster provider (instance {}) torn down.", this);
            }

            private void closeQuietly(final AutoCloseable resource) {
                try {
                    resource.close();
                } catch (final Exception e) {
                    log.debug("Ignoring exception while closing {}", resource, e);
                }
            }
        }

        AutoCloseable ret = new AutoCloseableToaster();
        log.info("Toaster provider (instance {}) initialized.", ret);
        return ret;

        // return provider;
    }

}
