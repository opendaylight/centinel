/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.flume;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.ComponentConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Amandeep Singh
 * 
 *         This class provides the test case handling for SplittingSerializer.
 * 
 */

public class SplittingSerializerTest {

    SplittingSerializer splittingSerializer;
    SplittingSerializerFactory splittingSerializerFactory;
    byte[] table = "centinel".getBytes();
    byte[] cf = "columnFamily".getBytes();

    @Before
    public void init() {
        splittingSerializer = new SplittingSerializer();
        splittingSerializerFactory = new SplittingSerializerFactory();
    }

    @After
    public void destroy() {
        splittingSerializer = null;
    }

    @Test
    public void testInitializeTableCfNull() {
        table = null;
        cf = null;
        splittingSerializer.initialize(table, cf);
    }

    @Test
    public void testSetEvent() {
        Event event = splittingSerializerFactory.flumeEvent();
        doReturn(splittingSerializerFactory.map).when(event).getHeaders();
        doReturn("EventBody".getBytes()).when(event).getBody();

        splittingSerializer.setEvent(event);

        splittingSerializer.initialize(splittingSerializerFactory.table, splittingSerializerFactory.cf);
        splittingSerializer.getActions();
    }

    @Test
    public void testSetEventEmpty() {
        Event event = splittingSerializerFactory.flumeEventEmptyEventType();
        doReturn(splittingSerializerFactory.map).when(event).getHeaders();
        doReturn("EventBody".getBytes()).when(event).getBody();

        splittingSerializer.setEvent(event);

        splittingSerializer.initialize(splittingSerializerFactory.table, splittingSerializerFactory.cf);
        try {
            splittingSerializer.getActions();
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testConfigure() {
        Context inputContext = splittingSerializerFactory.flumeContext();
        splittingSerializer.configure(inputContext);
    }

    @Test
    public void testConfigureCompConfig() {
        ComponentConfiguration conf = null;
        splittingSerializer.configure(conf);
    }

    @Test
    public void testGetIncrements() {
        splittingSerializer.initialize(splittingSerializerFactory.table, splittingSerializerFactory.cf);
        assertNotNull(splittingSerializer.getIncrements());

    }

    @Test
    public void testCleanUp() {
        splittingSerializer.cleanUp();

    }

}
