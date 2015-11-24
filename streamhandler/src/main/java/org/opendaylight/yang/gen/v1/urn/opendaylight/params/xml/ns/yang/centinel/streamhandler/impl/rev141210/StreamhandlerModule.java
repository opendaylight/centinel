package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.streamhandler.impl.rev141210;

import org.opendaylight.centinel.impl.dashboard.CentinelDashboardImpl;
import org.opendaylight.centinel.impl.dashboard.StreamCounterInfoCache;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.streamhandler.impl.StreamhandlerProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DashboardruleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamhandlerModule
        extends
        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.centinel.streamhandler.impl.rev141210.AbstractStreamhandlerModule {
    private static final Logger LOG = LoggerFactory.getLogger(StreamhandlerModule.class);

    public StreamhandlerModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier,
            org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public StreamhandlerModule(
            org.opendaylight.controller.config.api.ModuleIdentifier identifier,
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
        // TODO:implement

        LOG.info("module StreamHandler Initiated");

        StreamhandlerProvider streamhandlerProvider = new StreamhandlerProvider();
        DataBroker dataBrokerService = getDataBrokerDependency();

        /***
         * load the CentinelDashboardImpl and StreamCounterInfoCache register
         * for RPC
         */

        StreamCounterInfoCache streamCounterInfoCache = StreamCounterInfoCache.getInstance();

        final CentinelDashboardImpl centinelDashboardImpl = new CentinelDashboardImpl();
        centinelDashboardImpl.setDataProvider(dataBrokerService);
        final BindingAwareBroker.RpcRegistration<DashboardruleService> rpcDashboardruleServiceRegistration = getRpcRegistryDependency()
                .addRpcImplementation(DashboardruleService.class, centinelDashboardImpl);
        /**
         * end of CentinelDashboardImpl
         */

        LOG.info("module StreamHandler done");

        getBrokerDependency().registerProvider(streamhandlerProvider);
        final class AutoCloseableCentinel implements AutoCloseable {

            @Override
            public void close() throws Exception {

                /**
                 * close all resources for subscribe and dashboard:start
                 */
                rpcDashboardruleServiceRegistration.close();
                closeQuietly(rpcDashboardruleServiceRegistration);

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

        AutoCloseable ret = new AutoCloseableCentinel();
        return ret;
    }

}
