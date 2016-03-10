/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.EventBodyType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.EventNotified;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.EventNotifiedBuilder;

public class EventHandlerImplFactory {

    public EventNotified setNotification() {
        List<String> eventKeys = new ArrayList<String>();
        eventKeys.add("streamId");
        eventKeys.add("timestamp");
        EventNotifiedBuilder mockNotification = new EventNotifiedBuilder();
        mockNotification.setEventBody("testing event body");
        EventBodyType eventBodyType = EventBodyType.Avro;
        mockNotification.setEventBodyType(eventBodyType);
        mockNotification.setEventKeys(eventKeys);
        mockNotification.setEventType("stream");
        return mockNotification.build();
    }
    public EventNotified setNotificationCorrectJsonStream() {
        List<String> eventKeys = new ArrayList<String>();
        eventKeys.add("streamId");
        eventKeys.add("timestamp");
        EventNotifiedBuilder mockNotification = new EventNotifiedBuilder();
        mockNotification.setEventBody("{\"streamId\": \"abc\",\"timestamp\": \"12334\"}");
        EventBodyType eventBodyType = EventBodyType.Avro;
        mockNotification.setEventBodyType(eventBodyType);
        mockNotification.setEventKeys(eventKeys);
        mockNotification.setEventType("stream");
        return mockNotification.build();
    }
    public EventNotified setNotificationCorrectJsonAlert() {
        List<String> eventKeys = new ArrayList<String>();
        eventKeys.add("streamId");
        eventKeys.add("timestamp");
        EventNotifiedBuilder mockNotification = new EventNotifiedBuilder();
        mockNotification.setEventBody("{\"streamId\": \"abc\",\"timestamp\": \"12334\"}");
        EventBodyType eventBodyType = EventBodyType.Avro;
        mockNotification.setEventBodyType(eventBodyType);
        mockNotification.setEventKeys(eventKeys);
        mockNotification.setEventType("alert");
        return mockNotification.build();
    }
    public EventNotified setNotificationEventKeyNull() {
        List<String> eventKeys = new ArrayList<String>();
        EventNotifiedBuilder mockNotification = new EventNotifiedBuilder();
        mockNotification.setEventBody("testing event body");
        EventBodyType eventBodyType = EventBodyType.Avro;
        mockNotification.setEventBodyType(eventBodyType);
        mockNotification.setEventKeys(eventKeys);
        mockNotification.setEventType("stream");
        return mockNotification.build();
    }
}
