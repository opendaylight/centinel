/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.output;

import static org.junit.Assert.assertNotNull;


/**
 * @author Amandeep Singh 
 * 
 * 			 This class provides the test case handling
 *           
 */

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CentinelOutputModuleTest {
	
	CentinelOutputModule centinelOutputModule;
	
	 @Before
	    public void init(){
		 centinelOutputModule = new CentinelOutputModule();
	    }
	    
	    @After
	    public void destroy(){
	    	centinelOutputModule = null;
	    }
	    
	    @Test
	    public void testGetConfigBeans() {
	    	assertNotNull(centinelOutputModule.getConfigBeans());
	    }

}
