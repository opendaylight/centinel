/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.flume;

import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.apache.flume.Context;
import org.apache.flume.Event;

/**
 * @author Amandeep Singh
 * 
 *         This class provides the dummy inputs for test case
 * 
 */

public class SplittingSerializerFactory {

    byte[] table = "centinel".getBytes();
    byte[] cf = "columnFamily".getBytes();
    Map<String, String> map = new HashMap<String, String>();
    Context context = new Context();
    Event mockEvent = mock(Event.class);

    public Event flumeEvent() {
        map.put("eventType", "stream");
        return mockEvent;
    }

    public Event flumeEventEmptyEventType() {
        map.put("event", "stream");
        return mockEvent;

    }

    public Context flumeContext() {
        context.put("columns", "stringdata,alert,dashboard");
        return context;
    }

}
