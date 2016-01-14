/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.EventNotified;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.PersistEventInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.StreamhandlerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHandlerImpl implements StreamhandlerListener {

    private static final Logger LOG = LoggerFactory.getLogger(EventHandlerImpl.class);
    StreamhandlerImpl streamHandlerImpl;

    public EventHandlerImpl(StreamhandlerImpl streamHandlerImpl) {
        this.streamHandlerImpl = streamHandlerImpl;
    }

    @Override
    public void onEventNotified(EventNotified notification) {

        LOG.info("Event Notification received: " + notification);

        PersistEventInputBuilder input = new PersistEventInputBuilder();
        input.setEventBody(notification.getEventBody());
        input.setEventBodyType(notification.getEventBodyType());
        input.setEventKeys(notification.getEventKeys());
        input.setEventType(notification.getEventType());
        streamHandlerImpl.persistEvent(input.build());

    }

}
