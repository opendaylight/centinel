/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.alertcallback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Monika Verma
 * 
 * This class provides unit tests for CentinelAlertCallbackMetaData.
 * 
 */
public class CentinelAlertCallbackMetaDataTest {
    
    CentinelAlertCallbackMetaData alertCallbackMetaData;
    
    @Before
    public void init(){
        alertCallbackMetaData = new CentinelAlertCallbackMetaData();
    }
    
    @After
    public void destroy(){
        alertCallbackMetaData = null;
    }
    
    @Test
    public void testGetUniqueId() {
        assertNotNull(alertCallbackMetaData.getUniqueId());
    }

    @Test
    public void testGetName() {
        assertEquals("Centinel Alarmcallback Plugin", alertCallbackMetaData.getName());
    }

    @Test
    public void testGetAuthor() {
        assertEquals("Monika Verma", alertCallbackMetaData.getAuthor());
    }

    @Test
    public void testGetURL() {
        assertNotNull(alertCallbackMetaData.getURL());
    }

    @Test
    public void testGetVersion() {
        assertNotNull(alertCallbackMetaData.getVersion());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Alarm callback plugin that sends all stream alerts to a defined Centinel api.", alertCallbackMetaData.getDescription());
    }

    @Test
    public void testGetRequiredVersion() {
        assertNotNull(alertCallbackMetaData.getRequiredVersion());
    }

    @Test
    public void testGetRequiredCapabilities() {
        assertNotNull(alertCallbackMetaData.getRequiredCapabilities());
    }
}
