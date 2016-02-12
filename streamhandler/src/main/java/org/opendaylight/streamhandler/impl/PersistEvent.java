/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.FlumeException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.opendaylight.centinel.impl.dashboard.StreamCounterInfoCache;
import org.opendaylight.centinel.impl.subscribe.SubscriberImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.PersistEventInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistEvent {

    private static RpcClient client = null;
    private Event event;
    private static final Logger LOG = LoggerFactory.getLogger(PersistEvent.class);

    PersistEvent(String hostname, String port) {
        try {
            this.client = RpcClientFactory.getDefaultInstance(hostname, Integer.parseInt(port));
        } catch (FlumeException e) {
            LOG.error("Unable to connect to Flume.Flume specifications Flume Hostname -> " + hostname
                    + " / Flume Port -> " + port);
        }
    }

    public boolean sendDataToFlume(PersistEventInput input) throws IOException, JSONException {

        String eventFormat = input.getEventType();
        String eventBody = input.getEventBody();
        List<String> eventKey = null;

        if (!input.getEventKeys().isEmpty()) {
            eventKey = input.getEventKeys();
        } else {
            return false;
        }

        event = EventBuilder.withBody(eventBody, Charset.forName(StreamConstants.UTF_8));

        JSONObject body = null;
        JSONObject subbody = null;
        try {
            body = new JSONObject(eventBody);
        } catch (JSONException e2) {
            LOG.error(e2.getLocalizedMessage(), e2);
        }

        Map<String, String> header = new HashMap<String, String>();
        Iterator<String> itr = eventKey.iterator();
        String keyEvent = null;
        String[] keyEvents = null;

        while (itr.hasNext()) {
            keyEvent = (String) itr.next();
            if (!keyEvent.contains(StreamConstants.COLON)) {
                if (body.get(keyEvent) != null) {
                    header.put(keyEvent, (String) body.get(keyEvent));
                } else {
                    return false;
                }
            } else {
                keyEvents = keyEvent.split(StreamConstants.COLON);
                subbody = (JSONObject) body;
                for (String key : keyEvents) {
                    if (subbody.get(key) instanceof JSONObject) {
                        subbody = (JSONObject) subbody.get(key);
                    } else {
                        header.put(keyEvent.replace(StreamConstants.COLON, StreamConstants.UNDERSCORE),
                                (String) subbody.get(key));

                    }
                }
            }
        }
        header.put(StreamConstants.EVENT_TYPE, eventFormat);

        if (eventFormat.equalsIgnoreCase(StreamConstants.STREAM)) {
            StreamCounterInfoCache streamCounterInfoCache = StreamCounterInfoCache.getInstance();
            streamCounterInfoCache.incrementCounter(header.get(StreamConstants.STREAM_STREAM_ID));
            SubscriberImpl.publishToURL(null, header.get(StreamConstants.STREAM_STREAM_ID), eventBody);
        }
        if (eventFormat.equalsIgnoreCase(StreamConstants.ALERT)) {
            StreamCounterInfoCache streamCounterInfoCache = StreamCounterInfoCache.getInstance();
            streamCounterInfoCache.incrementCounter(header.get(StreamConstants.ALERT_CONDITION_ID));
            SubscriberImpl.publishToURL(header.get(StreamConstants.ALERT_CONDITION_ID),
                    header.get(StreamConstants.ALERT_STREAM_ID), eventBody);
        }
        String rowKeyStr = null;
        int rowKeyLength = 0;

        for (Map.Entry<String, String> entry : header.entrySet()) {
            if (rowKeyStr == null)
                rowKeyStr = entry.getValue() + StreamConstants.COLON;
            else
                rowKeyStr = rowKeyStr + entry.getValue() + StreamConstants.COLON;
        }
        rowKeyLength = rowKeyStr.length();
        rowKeyStr = rowKeyStr.substring(0, rowKeyLength - 1);
        event.setHeaders(header);
        try {
            if (client != null)
                client.append(event);
        } catch (EventDeliveryException e) {
            client.close();
            client = null;
            LOG.error(e.getLocalizedMessage(), e);
        }
        return true;
    }

    public void cleanUp() {
        client.close();
    }
}
