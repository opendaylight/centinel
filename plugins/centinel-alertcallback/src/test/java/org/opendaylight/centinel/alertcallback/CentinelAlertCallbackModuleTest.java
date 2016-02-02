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
 * This class provides unit tests for CentinelAlertCallbackModule.
 * 
 */
public class CentinelAlertCallbackModuleTest {
    
    CentinelAlertCallbackModule alertCallbackModule;
    
    @Before
    public void init(){
        alertCallbackModule = new CentinelAlertCallbackModule();
    }
    
    @After
    public void destroy(){
        alertCallbackModule = null;
    }
    
    @Test
    public void testGetConfigBeans() {
        assertNotNull(alertCallbackModule.getConfigBeans());
    }
}
