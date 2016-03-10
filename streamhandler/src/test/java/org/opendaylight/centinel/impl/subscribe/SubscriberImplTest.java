/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.subscribe;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.Subscribe.Mode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeDeleteInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeDeleteOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeTestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeTestOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeUserInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeUserOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.Subscription;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscriptionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.subscription.Subscriptions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.subscription.SubscriptionsBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;

import com.google.common.util.concurrent.CheckedFuture;

/**
 * @author Sunaina Khanna
 * 
 *         This class consists of junit test cases for SubscriberImpl .
 */
public class SubscriberImplTest {

    @Mock
    private DataBroker mockDataBroker;
    private final ExecutorService mockExecutor;
    private SubscriberImpl mockSubscriberImpl;
    private SubscriberImplFactory subscriberImplFactory = new SubscriberImplFactory();
    private MockCentinelSubscriberImpl myMock = new MockCentinelSubscriberImpl();
    public static final InstanceIdentifier<Subscription> subscriptionIID = InstanceIdentifier.builder(
            Subscription.class).build();

    public SubscriberImplTest() {
        mockExecutor = Executors.newFixedThreadPool(1);
    }

    /**
     * Test method for WidgetInputRule on valid inputs
     */
    @Before
    public void beforeTest() {
        MockitoAnnotations.initMocks(this);
        mockSubscriberImpl = new SubscriberImpl();
        myMock.setDataProvider(mockDataBroker);
    }

    @After
    public void AfterTest() {
        mockSubscriberImpl = null;
    }

    @Test
    public void subscribeDeleteTest() {
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();

        SubscribeDeleteInput input = subscriberImplFactory.mockInputForSubscriberDelete();

        Subscriptions subscriptiontodelete = new SubscriptionsBuilder().setSubscribeID(input.getSubscribeID()).build();

        mockReadWriteTx.delete(LogicalDatastoreType.OPERATIONAL,
                subscriptionIID.child(Subscriptions.class, subscriptiontodelete.getKey()));
        mockReadWriteTx.submit();
        doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx).submit();
        Future<RpcResult<SubscribeDeleteOutput>> futureOutput = myMock.subscribeDelete(input);
        assertNotNull(futureOutput);
    }

    @Test
    public void subscribeDeleteException() {
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        SubscribeDeleteInput input = subscriberImplFactory.mockInputForSubscriberDelete();
        Subscriptions subscriptiontodelete = new SubscriptionsBuilder().setSubscribeID(input.getSubscribeID()).build();
        mockReadWriteTx.delete(LogicalDatastoreType.OPERATIONAL,
                subscriptionIID.child(Subscriptions.class, subscriptiontodelete.getKey()));
        mockReadWriteTx.submit();
        doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx).submit();
        Future<RpcResult<SubscribeDeleteOutput>> futureOutput = myMock.subscribeDelete(input);
        assertNotNull(futureOutput);
    }

    @Test
    public void subscribeUserTest() {
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        SubscribeUserInput input = subscriberImplFactory.mockInputForSubscribeUser();
        Subscriptions inpt = new SubscriptionsBuilder().setSubscribeID(input.getSubscribeID())
                .setStreamID(input.getStreamID()).setMode(input.getMode()).setRuleID(input.getRuleID()).build();
        List<Subscriptions> subscriptionlist = new ArrayList<Subscriptions>();
        subscriptionlist.add(inpt);
        mockReadWriteTx.merge(LogicalDatastoreType.CONFIGURATION, subscriptionIID, new SubscriptionBuilder()
                .setSubscriptions(subscriptionlist).build(), true);
        mockReadWriteTx.submit();
        doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx).submit();
        Future<RpcResult<SubscribeUserOutput>> futureOutput = myMock.subscribeUser(input);
        assertNotNull(futureOutput);
    }

    @Test
    public void subscribeUserTestException() {

        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        SubscribeUserInput input = subscriberImplFactory.mockInputForSubscribeUser();
        Subscriptions inpt = new SubscriptionsBuilder().setSubscribeID(input.getSubscribeID())
                .setStreamID(input.getStreamID()).setMode(input.getMode()).setRuleID(input.getRuleID()).build();
        List<Subscriptions> subscriptionlist = new ArrayList<Subscriptions>();
        subscriptionlist.add(inpt);
        mockReadWriteTx.merge(LogicalDatastoreType.CONFIGURATION, subscriptionIID, new SubscriptionBuilder()
                .setSubscriptions(subscriptionlist).build(), true);
        mockReadWriteTx.submit();
        doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx).submit();
        Future<RpcResult<SubscribeUserOutput>> futureOutput = myMock.subscribeUser(input);
        assertNotNull(futureOutput);
    }

    @Test
    public void subscribeTestTestAlert() {
        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        SubscribeTestInput input = subscriberImplFactory.mockInputForSubscribeTest();
        List<Subscriptions> value = new ArrayList<Subscriptions>();
        Subscriptions e = new SubscriptionsBuilder().setMode(Mode.Alert).setRuleID("100").build();
        value.add(e);
        Subscription inpt = new SubscriptionBuilder().setSubscriptions(value).build();
        SubscriberInfoCache mockSubscriberInfoCache = SubscriberInfoCache.getInstance();
        mockSubscriberInfoCache.setSubscription(inpt);
        Future<RpcResult<SubscribeTestOutput>> futureOutput = myMock.subscribeTest(input);
        assertNotNull(futureOutput);
    }

    @Test
    public void subscribeTestTestStream() {

        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        SubscribeTestInput input = subscriberImplFactory.mockInputForSubscribeTest();
        List<Subscriptions> value = new ArrayList<Subscriptions>();
        Subscriptions e = new SubscriptionsBuilder().setMode(Mode.Stream).setStreamID("100").build();
        value.add(e);
        Subscription inpt = new SubscriptionBuilder().setSubscriptions(value).build();
        SubscriberInfoCache mockSubscriberInfoCache = SubscriberInfoCache.getInstance();
        mockSubscriberInfoCache.setSubscription(inpt);
        Future<RpcResult<SubscribeTestOutput>> futureOutput = myMock.subscribeTest(input);
        assertNotNull(futureOutput);
    }

    @Test
    public void subscribeUserTestNull() {

        ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
        doReturn(mockReadWriteTx).when(mockDataBroker).newReadWriteTransaction();
        SubscribeUserInput input = subscriberImplFactory.mockInputForSubscribeUserNull();
        Subscriptions inpt = new SubscriptionsBuilder().setSubscribeID(input.getSubscribeID())
                .setStreamID(input.getStreamID()).setMode(input.getMode()).setRuleID(input.getRuleID()).build();
        List<Subscriptions> subscriptionlist = new ArrayList<Subscriptions>();
        subscriptionlist.add(inpt);
        mockReadWriteTx.merge(LogicalDatastoreType.CONFIGURATION, subscriptionIID, new SubscriptionBuilder()
                .setSubscriptions(subscriptionlist).build(), true);
        mockReadWriteTx.submit();
        doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx).submit();
        Future<RpcResult<SubscribeUserOutput>> futureOutput = myMock.subscribeUser(input);
        assertNotNull(futureOutput);
    }

    private class MockCentinelSubscriberImpl extends SubscriberImpl {

    }

}
