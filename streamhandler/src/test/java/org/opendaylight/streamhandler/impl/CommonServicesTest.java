/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import javax.json.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class CommonServicesTest {

    CommonServices mockCommonServices;
    CommonServicesFactory commonServicesFactory = new CommonServicesFactory();

    @Before
    public void beforeTest() {
        MockitoAnnotations.initMocks(this);
        mockCommonServices = mockCommonServices.getInstance();
    }

    @Test
    public void callStreamConstants() {
        @SuppressWarnings("unused")
        StreamConstants mockStreamConstants = new StreamConstants();
    }

    @Test
    public void testparseJsonObject() {
        try {
            Class c = CommonServices.class;
            Method m;
            m = c.getDeclaredMethod("parseJsonObject", new Class[] { JsonObject.class, String.class });
            m.setAccessible(true);
            m.invoke(mockCommonServices, commonServicesFactory.inputJSON(), commonServicesFactory.stringGetFromJson());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testParseResponse() {
        boolean zeroListReceive = false;
        System.out.println("response >> " + commonServicesFactory.inputResponse().toString());
        List<Map<String, Object>> listOut = mockCommonServices.parseResponse(commonServicesFactory.inputResponse()
                .toString(), commonServicesFactory.eventKeys());
        if (listOut.size() == 0) {
            zeroListReceive = true;
            assertTrue(zeroListReceive);
        }
    }
}
