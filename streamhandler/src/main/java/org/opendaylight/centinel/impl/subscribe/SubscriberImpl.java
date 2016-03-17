/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeDeleteInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeDeleteOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeDeleteOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeTestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeTestOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeTestOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeUserInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeUserOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeUserOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.Subscription;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscriptionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.subscription.Subscriptions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.subscription.SubscriptionsBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorType;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.SettableFuture;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class SubscriberImpl implements AutoCloseable, SubscribeService {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriberImpl.class);

    public static final InstanceIdentifier<Subscription> SUBSCRIPTION_ID = InstanceIdentifier
            .builder(Subscription.class).build();

    private DataBroker dataProvider;
    private final ExecutorService executor;

    public SubscriberImpl() {
        executor = Executors.newFixedThreadPool(1);
    }

    public void setDataProvider(final DataBroker salDataProvider) {
        this.dataProvider = salDataProvider;
        LOG.info("data provider set for subscribe");
    }

    @Override
    public void close() throws Exception {
        // When we close this service we need to shutdown our executor!
        executor.shutdown();

    }

    private Subscriptions buildSubscriptions(final SubscribeUserInput input) {
        if (input.getSubscribeID() == null || input.getSubscribeID().isEmpty()) {
            return new SubscriptionsBuilder().setMode(input.getMode()).setURL(input.getURL())
                    .setStreamID(input.getStreamID()).setRuleID(input.getRuleID()).setUserName(input.getUserName())
                    .setSubscribeID(String.format("%x", (int) (Math.random() * 10000))).build();
        }

        else {
            return new SubscriptionsBuilder().setMode(input.getMode()).setURL(input.getURL())
                    .setStreamID(input.getStreamID()).setRuleID(input.getRuleID()).setUserName(input.getUserName())
                    .setSubscribeID(input.getSubscribeID()).build();

        }

    }

    public static void publishToURL(String alertID, String streamID, String message) {
        SubscriberInfoCache subscriberInfoCache = SubscriberInfoCache.getInstance();

        Subscription subscription = subscriberInfoCache.getSubscription();
        if(subscription != null) {
            for (Subscriptions subscriptionitem : subscription.getSubscriptions()) {
                switch (subscriptionitem.getMode()) {
                case Stream:
                    if (streamID != null && message != null && streamID.equals(subscriptionitem.getStreamID())) {
                        invokeRESTService(message, subscriptionitem.getURL());
                    }
                    break;

                case Alert:
                    if (alertID != null && message != null && alertID.equals(subscriptionitem.getRuleID())) {
                        invokeRESTService(message, subscriptionitem.getURL());
                    }
                    break;
                }
            }
        }

    }

    private static void invokeRESTService(String message, String url) {
        // TODO Auto-generated method stub

        // JsonObject configJson = objectToJsonMapper(dataObjectRuleList);

        try {
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            LOG.info("sending request to HTTP URL:" + url);

            ClientResponse cr = webResource.type("application/json").post(ClientResponse.class, message);

            LOG.info("HTTP STATUS:" + cr.getStatus());
            // release the resources
            webResource.delete();
            client.destroy();
        } catch (Exception ex) {
            LOG.error("Exception in sending REST call for URL:" + url + ":Message:" + ex);

        }
    }

    @Override
    public Future<RpcResult<SubscribeUserOutput>> subscribeUser(final SubscribeUserInput input) {
        final SettableFuture<RpcResult<SubscribeUserOutput>> futureResult = SettableFuture.create();
        final SubscribeUserOutputBuilder subscribeUserOutputBuilder = new SubscribeUserOutputBuilder();
        subscribeUserOutputBuilder.setStatus("SUCCESS");
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();

        List<Subscriptions> subscriptionlist = new ArrayList<Subscriptions>();

        subscriptionlist.add(buildSubscriptions(input));

        try {

            tx.merge(LogicalDatastoreType.CONFIGURATION, SUBSCRIPTION_ID,
                    new SubscriptionBuilder().setSubscriptions(subscriptionlist)

                            .build(),
                    true);
            tx.submit();
            /***
             * update the SubscriberInfoCache
             */
            SubscriberInfoCache subscriberInfoCache = SubscriberInfoCache.getInstance();
            Subscription subscription = new SubscriptionBuilder().setSubscriptions(subscriptionlist).build();
            subscriberInfoCache.setSubscription(subscription);

            /**
             * end of update SubscriberInfoCache
             */

            futureResult
                    .set(RpcResultBuilder.<SubscribeUserOutput> success(subscribeUserOutputBuilder.build()).build());
            LOG.info(" Subscribe Request recieved for Update/Creation:ID::" + subscriptionlist.get(0).getSubscribeID());

        } catch (Exception e) {
            LOG.error("Subscribe Request recieved for Update/Creation:ID" + subscriptionlist.get(0).getSubscribeID()
                    + ":ErrorMessage:" + e.getMessage(), e);
            futureResult.set(RpcResultBuilder.<SubscribeUserOutput> failed().build());
        }

        return futureResult;
    }

    @Override
    public Future<RpcResult<SubscribeTestOutput>> subscribeTest(SubscribeTestInput input) {
        // TODO Auto-generated method stub
        final SettableFuture<RpcResult<SubscribeTestOutput>> futureResult = SettableFuture.create();

        publishToURL(input.getAlertID(), input.getStreamID(), input.getMessage());

        futureResult.set(RpcResultBuilder.<SubscribeTestOutput> success(
                new SubscribeTestOutputBuilder().setStatus("SUCCESS").build()).build());
        return futureResult;
    }

    @Override
    public Future<RpcResult<SubscribeDeleteOutput>> subscribeDelete(SubscribeDeleteInput input) {
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<SubscribeDeleteOutput>> futureResult = SettableFuture.create();
        final SubscribeDeleteOutputBuilder subscribeDeleteOutputBuilder = new SubscribeDeleteOutputBuilder();
        Subscriptions subscriptiontodelete = new SubscriptionsBuilder().setSubscribeID(input.getSubscribeID()).build();

        try {
            tx.delete(LogicalDatastoreType.OPERATIONAL,
                    SUBSCRIPTION_ID.child(Subscriptions.class, subscriptiontodelete.getKey()));
            tx.submit();

            subscribeDeleteOutputBuilder.setStatus("SUCCESS");
            futureResult.set(RpcResultBuilder.<SubscribeDeleteOutput> success(subscribeDeleteOutputBuilder.build())
                    .build());

            LOG.info("Subscription deleted:" + input.getSubscribeID());
        } catch (Exception ex) {
            LOG.error(
                    "Exception in Subscription deleted:" + input.getSubscribeID() + ":ErrorMessage:" + ex.getMessage(),
                    ex);
            ErrorType errorType = ErrorType.APPLICATION;
            futureResult.set(RpcResultBuilder.<SubscribeDeleteOutput> failed()
                    .withError(errorType, "Exception Caught at widget deletion:" + ex.getMessage())

                    .build());
        }
        return futureResult;

    }

}
