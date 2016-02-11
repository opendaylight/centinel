package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.laas.impl.rev141210;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.RpcRegistration;
import org.opendaylight.laas.impl.CentinelEventNotifierImpl;
import org.opendaylight.laas.impl.CentinelLaasAlertConditionImpl;
import org.opendaylight.laas.impl.CentinelLaasStreamImpl;
import org.opendaylight.laas.impl.ConfigurationChangeLaasImpl;
import org.opendaylight.laas.impl.LaasProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.eventinput.rev150105.EventinputService;
import org.opendaylight.yangtools.concepts.ListenerRegistration;

public class LaaSModule
        extends
        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.laas.impl.rev141210.AbstractLaaSModule {
    public LaaSModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier,
            org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public LaaSModule(
            org.opendaylight.controller.config.api.ModuleIdentifier identifier,
            org.opendaylight.controller.config.api.DependencyResolver dependencyResolver,
            org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.laas.impl.rev141210.LaaSModule oldModule,
            java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        final CentinelLaasStreamImpl streamlaasImpl = new CentinelLaasStreamImpl();
        final CentinelLaasAlertConditionImpl centinelAlertConditionImpl = new CentinelLaasAlertConditionImpl();
        LaasProvider provider = new LaasProvider();
        DataBroker dataBrokerService = getDataBrokerDependency();
        centinelAlertConditionImpl.setDataProvider(dataBrokerService);
        streamlaasImpl.setDataProvider(dataBrokerService);

        final ListenerRegistration<DataChangeListener> alertMessageCountRuleChangeListener = dataBrokerService
                .registerDataChangeListener(LogicalDatastoreType.CONFIGURATION,
                        CentinelLaasAlertConditionImpl.alertMessageCountRuleRecordId, centinelAlertConditionImpl,
                        DataChangeScope.SUBTREE);

        final ListenerRegistration<DataChangeListener> alertFeildContentRuleChangeListener = dataBrokerService
                .registerDataChangeListener(LogicalDatastoreType.CONFIGURATION,
                        CentinelLaasAlertConditionImpl.alertFeildContentRuleRecordId, centinelAlertConditionImpl,
                        DataChangeScope.SUBTREE);

        final ListenerRegistration<DataChangeListener> alertFeildValueRuleChangeListener = dataBrokerService
                .registerDataChangeListener(LogicalDatastoreType.CONFIGURATION,
                        CentinelLaasAlertConditionImpl.alertFieldValueRuleRecordId, centinelAlertConditionImpl,
                        DataChangeScope.SUBTREE);

        final ListenerRegistration<DataChangeListener> streamRuleChangeListener = dataBrokerService
                .registerDataChangeListener(LogicalDatastoreType.CONFIGURATION, CentinelLaasStreamImpl.streamRecordId,
                        streamlaasImpl, DataChangeScope.SUBTREE);

        /**
         * stream handler
         */
        final CentinelEventNotifierImpl centinelEventNotifierImpl = new CentinelEventNotifierImpl();
        centinelEventNotifierImpl.setNotificationProvider(getNotificationServiceDependency());

        final RpcRegistration<EventinputService> rpcRegistrationStreamHandler = getRpcRegistryDependency()
                .addRpcImplementation(EventinputService.class, centinelEventNotifierImpl);

        /**
         * end of stream handler module
         */

        /**
         * configuration module
         */
        final ConfigurationChangeLaasImpl configurationChangeLaasImpl = new ConfigurationChangeLaasImpl();
        getNotificationServiceDependency().registerNotificationListener(configurationChangeLaasImpl);
        /**
         * end of configuration module
         */

        getBrokerDependency().registerProvider(provider);
        final class AutoCloseableLaasCentinel implements AutoCloseable {

            @Override
            public void close() throws Exception {
                alertMessageCountRuleChangeListener.close();
                alertFeildValueRuleChangeListener.close();
                alertFeildContentRuleChangeListener.close();
                streamRuleChangeListener.close();
                closeQuietly(centinelAlertConditionImpl);

            }

            private void closeQuietly(final AutoCloseable resource) {
                try {
                    resource.close();
                } catch (final Exception e) {
                }
            }
        }

        AutoCloseable ret = new AutoCloseableLaasCentinel();
        return ret;

    }

}
