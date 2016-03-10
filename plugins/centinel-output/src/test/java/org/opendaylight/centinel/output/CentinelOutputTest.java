/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.output;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

import org.graylog2.plugin.Message;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.streams.Stream;
import org.graylog2.plugin.streams.StreamRule;
import org.graylog2.plugin.streams.StreamRuleType;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;

/**
 * @author Amandeep Singh 
 * 
 * 			 This class provides the dummy params and test case handling for the different scenarios
 *           
 */


public class CentinelOutputTest {
	
	boolean exceptionCaught= false;
	
	 CentinelOutput centinelOutput;
	    

	    @After
	    public void destroy() {
	    	centinelOutput = null;
	    }

	    @Test
	    public void testWrite() {
	    	
	    	/**
	         * Expected dummy params
	         */
	        
	        Configuration configuration=mock(Configuration.class);
	        Message mesg=mock(Message.class);
	        List<Stream> streamList  = mock(List.class);
	        Stream stream = mock(Stream.class);
	        doReturn(stream).when(streamList).get(0);
	        List<StreamRule> streamRules  = mock(List.class);
	        StreamRule streamRule = mock(StreamRule.class);
	        doReturn(streamRules).when(stream).getStreamRules();
	        doReturn(streamRule).when(streamRules).get(0);
	        
	        doReturn(StreamRuleType.EXACT).when(streamRule).getType();
	        doReturn(new HashMap<String, Object>()).when(mesg).getFields();
	        doReturn("id").when(mesg).getId();
	        InetAddress inet = mock(InetAddress.class);
	        doReturn(inet).when(mesg).getInetAddress();
	        doReturn("Hostname-Centinel").when(inet).getHostName();
	        doReturn(true).when(mesg).getIsSourceInetAddress();
	        doReturn(60L).when(mesg).getJournalOffset();
	        doReturn("Test Plugin").when(mesg).getMessage();
	        doReturn("Source").when(mesg).getSource();
	        doReturn("SourceInputId").when(mesg).getSourceInputId();
	        doReturn(streamList).when(mesg).getStreams();
	        doReturn(new DateTime()).when(mesg).getTimestamp();
	        doReturn("No errors").when(mesg).getValidationErrors();
	       
	        /**Test case handling
	         */
	        try {
	        	centinelOutput = new CentinelOutput(stream,configuration);
	        	centinelOutput.write(mesg);
	        } catch (Exception e) {
	        	exceptionCaught = true;
	        }
	        assertTrue(exceptionCaught);
	    }
	    
}
