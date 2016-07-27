/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.dashboard;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Before;
import org.junit.Test;

public class StreamCounterInfoCacheTest {

    private StreamCounterInfoCache mockStreamCounterInfoCache;
    StreamCounterInfoCacheFactory streamCounterInfoCacheFactory;
    private List<WidgetStreamCounterVO> mockListofcounter = new ArrayList<WidgetStreamCounterVO>();
    WidgetStreamCounterVO widgetStreamCounterVO = null;

    @Before
    public void beforeTest() {
        streamCounterInfoCacheFactory = new StreamCounterInfoCacheFactory();
        mockStreamCounterInfoCache = StreamCounterInfoCache.getInstance();
        widgetStreamCounterVO = streamCounterInfoCacheFactory.widgetStreamCounterVOInput();
    }

    @Test
    public void getCounterValueTest() {
        mockListofcounter.add(widgetStreamCounterVO);
        mockStreamCounterInfoCache.setListofcounter(mockListofcounter);
        int counter = mockStreamCounterInfoCache.getCounterValue(streamCounterInfoCacheFactory
                .removeCounterForWidgetInput());
        ;
        assertNotNull(counter);
    }

    @Test
    public void removeCounterForWidgetTest() {
        mockListofcounter.add(widgetStreamCounterVO);
        mockStreamCounterInfoCache.setListofcounter(mockListofcounter);
        boolean checkSesInWidget = false;
        try {
            mockStreamCounterInfoCache.removeCounterForWidget(streamCounterInfoCacheFactory
                    .removeCounterForWidgetInput());
        } catch (Exception e) {
            checkSesInWidget = true;
            assertTrue(checkSesInWidget);
        }

    }

    @Test
    public void removeCounterForWidgetListEmptyTest() {
        int counter = mockStreamCounterInfoCache.getCounterValue(streamCounterInfoCacheFactory
                .removeCounterForWidgetInput());
        assertNotNull(counter);
    }

    @Test
    public void incrementCounterTest() {
        mockListofcounter.add(widgetStreamCounterVO);
        mockStreamCounterInfoCache.setListofcounter(mockListofcounter);
        mockStreamCounterInfoCache.incrementCounter(streamCounterInfoCacheFactory.removeCounterForWidgetInput());
        assertNotNull(mockStreamCounterInfoCache.toString());
    }

}
