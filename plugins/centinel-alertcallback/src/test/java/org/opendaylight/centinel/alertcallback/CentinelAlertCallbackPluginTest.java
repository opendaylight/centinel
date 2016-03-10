/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.alertcallback;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Monika Verma
 * 
 * This class provides unit tests for CentinelAlertCallbackPlugin.
 * 
 */
public class CentinelAlertCallbackPluginTest {
    
    CentinelAlertCallbackPlugin alertCallbackPlugin;
    
    @Before
    public void init(){
        alertCallbackPlugin = new CentinelAlertCallbackPlugin();
    }
    
    @After
    public void destroy(){
        alertCallbackPlugin = null;
    }
    
    @Test
    public void testModules() {
        assertNotNull(alertCallbackPlugin.modules());
    }

    @Test
    public void testMetadata() {
        assertNotNull(alertCallbackPlugin.metadata());
    }
}
