/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.output;

import static org.junit.Assert.assertEquals;

/**
 * @author Amandeep Singh 
 * 
 * 			 This class provides the test case handling
 *           for different scenarios
 */


import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CentinelOutputMetaDataTest {
	
	CentinelOutputMetaData centinelOutputMetaData;
	
	 @Before
	    public void init(){
		 centinelOutputMetaData = new CentinelOutputMetaData();
	    }
	    
	    @After
	    public void destroy(){
	    	centinelOutputMetaData = null;
	    }
	    
	    
	    @Test
	    public void testGetUniqueId() {
	    	assertEquals("org.opendaylight.centinel.output.CentinelOutputPlugin",centinelOutputMetaData.getUniqueId());
	    }

	    @Test
	    public void testGetName() {
	        assertEquals("CentinelMessageOutputPlugin", centinelOutputMetaData.getName());
	    }

	    @Test
	    public void testGetAuthor() {
	        assertEquals("Monika Verma", centinelOutputMetaData.getAuthor());
	    }

	    @Test
	    public void testGetURL() {
	        assertNotNull(centinelOutputMetaData.getURL());
	    }

	    @Test
	    public void testGetVersion() {
	        assertNotNull(centinelOutputMetaData.getVersion());
	    }

	    @Test
	    public void testGetDescription() {
	        assertEquals("Centinel message output plugin that forwards the graylog stream events to Centinel REST API", centinelOutputMetaData.getDescription());
	    }

	    @Test
	    public void testGetRequiredVersion() {
	        assertNotNull(centinelOutputMetaData.getRequiredVersion());
	    }

	    @Test
	    public void testGetRequiredCapabilities() {
	        assertNotNull(centinelOutputMetaData.getRequiredCapabilities());
	    }

}
