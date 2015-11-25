/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.laas.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.eventinput.rev150105.EventinputService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.eventinput.rev150105.NotifyEventInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.EventNotifiedBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.SettableFuture;

/**
 * This class provides rpc implementation to publish graylog events to md-sal.
 * 
 * @author Monika Verma
 * 
 */
public class CentinelEventNotifierImpl implements AutoCloseable, EventinputService {

    private static final Logger LOG = LoggerFactory.getLogger(CentinelEventNotifierImpl.class);

    private NotificationProviderService notificationProvider;
    private final ExecutorService executor;

    public CentinelEventNotifierImpl() {
        executor = Executors.newFixedThreadPool(1);
    }

    public void setNotificationProvider(final NotificationProviderService salService) {
        this.notificationProvider = salService;
        LOG.info("notification provider set");
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
    }

    @Override
    public Future<RpcResult<Void>> notifyEvent(NotifyEventInput input) {
        LOG.info("Input for rpc notifyEvent : " + input);

        final SettableFuture<RpcResult<Void>> futureResult = SettableFuture.create();
        futureResult.set(RpcResultBuilder.<Void> success().build());

        notificationProvider.publish(new EventNotifiedBuilder().setEventType(input.getEventType())
                .setEventBodyType(input.getEventBodyType()).setEventBody(input.getEventBody())
                .setEventKeys(input.getEventKeys()).build());
        return futureResult;
    }

}