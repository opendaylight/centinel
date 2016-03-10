/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.output;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Amandeep Singh 
 * 
 * 			 This class provides the test case handling
 *           
 */

public class CentinelOutputPluginTest {
	
	CentinelOutputPlugin centinelOutputPlugin;
	
	@Before
    public void init(){
		centinelOutputPlugin = new CentinelOutputPlugin();
    }
    
    @After
    public void destroy(){
    	centinelOutputPlugin = null;
    }
    
    
    @Test
    public void testMetadata() {
    	assertNotNull(centinelOutputPlugin.metadata());
    }

    @Test
    public void testModules() {
    	assertNotNull(centinelOutputPlugin.modules());
    }

}
