package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.streamhandler.impl.rev141210;

import org.opendaylight.centinel.impl.dashboard.CentinelDashboardImpl;
import org.opendaylight.centinel.impl.dashboard.StreamCounterInfoCache;
import org.opendaylight.centinel.impl.ipfix.IpfixCollector;
import org.opendaylight.centinel.impl.openflow.CentinelOpenFlowCollector;
import org.opendaylight.centinel.impl.subscribe.SubscriberImpl;
import org.opendaylight.centinel.impl.subscribe.SubscriberInfoCache;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.RpcRegistration;
import org.opendaylight.streamhandler.impl.ConfigurationChangeImpl;
import org.opendaylight.streamhandler.impl.EventHandlerImpl;
import org.opendaylight.streamhandler.impl.LogCollectorTLS;

import org.opendaylight.streamhandler.impl.StreamhandlerImpl;
import org.opendaylight.streamhandler.impl.StreamhandlerProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DashboardruleService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.StreamhandlerService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeService;

public class StreamhandlerModule extends
        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.streamhandler.impl.rev141210.AbstractStreamhandlerModule {
    public StreamhandlerModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier,
            org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public StreamhandlerModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier,
            org.opendaylight.controller.config.api.DependencyResolver dependencyResolver,
            org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.streamhandler.impl.rev141210.StreamhandlerModule oldModule,
            java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        final StreamhandlerImpl streamhandlerImpl = new StreamhandlerImpl();
        StreamhandlerProvider streamhandlerProvider = new StreamhandlerProvider();
        EventHandlerImpl eventHandlerImpl = new EventHandlerImpl(streamhandlerImpl);
        ConfigurationChangeImpl configurationChangeImpl = new ConfigurationChangeImpl();
        configurationChangeImpl.logCollectorStart();
        DataBroker dataBrokerService = getDataBrokerDependency();
        final CentinelOpenFlowCollector centinelOpenFlowCollector = new CentinelOpenFlowCollector(dataBrokerService);
        IpfixCollector ipfixCollector = new IpfixCollector();
        ipfixCollector.start();

        /***
         * load the CentinelDashboardImpl and StreamCounterInfoCache register
         * for RPC
         */
        StreamCounterInfoCache streamCounterInfoCache = StreamCounterInfoCache.getInstance();
        final CentinelDashboardImpl centinelDashboardImpl = new CentinelDashboardImpl(streamhandlerImpl);
        centinelDashboardImpl.setDataProvider(dataBrokerService);
        final BindingAwareBroker.RpcRegistration<DashboardruleService> rpcDashboardruleServiceRegistration = getRpcRegistryDependency()
                .addRpcImplementation(DashboardruleService.class, centinelDashboardImpl);
        /**
         * end of CentinelDashboardImpl
         */

        /**
         * load the subscriber cache in memory initalize Impl
         */
        SubscriberInfoCache subscriberInfoCache = SubscriberInfoCache.getInstance();

        final SubscriberImpl subscriberImpl = new SubscriberImpl();
        subscriberImpl.setDataProvider(dataBrokerService);

        final RpcRegistration<SubscribeService> rpcSubscribeRegistration = getRpcRegistryDependency()
                .addRpcImplementation(SubscribeService.class, subscriberImpl);

        /**
         * end of subscribe module
         */

        final BindingAwareBroker.RpcRegistration<StreamhandlerService> rpcRegistrationhandler = getRpcRegistryDependency()
                .addRpcImplementation(StreamhandlerService.class, streamhandlerImpl);

        // Register notification listeners to receive notifications
        getNotificationServiceDependency().registerNotificationListener(eventHandlerImpl);
        getNotificationServiceDependency().registerNotificationListener(configurationChangeImpl);

        getBrokerDependency().registerProvider(streamhandlerProvider);

        final class AutoCloseableToaster implements AutoCloseable {

            @Override
            public void close() throws Exception {
                centinelOpenFlowCollector.close();
                rpcRegistrationhandler.close();
                closeQuietly(streamhandlerImpl);

                /**
                 * close all resources for subscribe and dashboard:start
                 */
                rpcDashboardruleServiceRegistration.close();
                rpcSubscribeRegistration.close();
                closeQuietly(rpcDashboardruleServiceRegistration);

                closeQuietly(subscriberImpl);
                /**
                 * close all resources for subscribe and dashboard:end
                 */
            }

            private void closeQuietly(final AutoCloseable resource) {
                try {
                    resource.close();
                } catch (final Exception e) {
                }
            }
        }
        AutoCloseable ret = new AutoCloseableToaster();
        return ret;
    }

}
