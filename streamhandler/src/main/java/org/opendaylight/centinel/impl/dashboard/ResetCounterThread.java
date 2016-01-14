/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.dashboard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.opendaylight.streamhandler.impl.StreamhandlerImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.EventBodyType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.PersistEventInputBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetCounterThread implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ResetCounterThread.class);

    WidgetStreamCounterVO widgetStreamCounterVO;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private StreamhandlerImpl streamhandlerImpl2;

    ResetCounterThread(WidgetStreamCounterVO widgetStreamCounterVO, StreamhandlerImpl streamhandlerImpl2) {
        this.widgetStreamCounterVO = widgetStreamCounterVO;
        this.streamhandlerImpl2=streamhandlerImpl2;

    }

    @Override
    public void run() {
        // Save the old value in DB
        // save the following in DB
        // widgetStreamCounterVO.getCounter()
        // widgetStreamCounterVO.getWidgetID()
        // widgetStreamCounterVO.getCal()
    	LOG.info("inside run of ResetCounterThread for widgetStreamCounterVO.getWidgetID(): "+ widgetStreamCounterVO.getWidgetID());
    	saveinDB(widgetStreamCounterVO.getWidgetID(), widgetStreamCounterVO.getCounter(),widgetStreamCounterVO.getCal());
        // Reset the counter value to zero

        widgetStreamCounterVO.setCounter(0);
        // increment the Timer count to next slot
        widgetStreamCounterVO.getCal().add(Calendar.MINUTE, widgetStreamCounterVO.getResettime() / 60);

    }
    private void saveinDB(String widgetID, int counter, Calendar calendar) {
    	 JsonBuilderFactory factory = Json.createBuilderFactory(null);
    	 
     	LOG.info("inside saveinDB of ResetCounterThread for WidgetID(): "+widgetID+":counter:"+counter+":calendar:"+dateFormat.format(calendar.getTime()));

     	JsonObject inputJson = null;
     	inputJson = factory.createObjectBuilder()
              .add("resetTime", dateFormat.format(calendar.getTime()).toString())
              .add("widgetId", widgetID)
              .add("counter", counter).build();
         
         List<String> eventKeys = new ArrayList<String>();
         eventKeys.add("resetTime");
         eventKeys.add("widgetId");
         
         PersistEventInputBuilder input = new PersistEventInputBuilder();
         input.setEventBody(inputJson.toString());
         input.setEventBodyType(EventBodyType.Json);
         input.setEventKeys(eventKeys);
         input.setEventType("dashboard");
         LOG.info("dashboard persist event input: "+ input.toString());
         
         streamhandlerImpl2.persistEvent(input.build());
	}
	
}
