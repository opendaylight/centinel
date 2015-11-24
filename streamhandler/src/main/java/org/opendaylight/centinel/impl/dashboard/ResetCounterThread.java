/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.dashboard;

import java.util.Calendar;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetCounterThread implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ResetCounterThread.class);

    WidgetStreamCounterVO widgetStreamCounterVO;

    ResetCounterThread(WidgetStreamCounterVO widgetStreamCounterVO) {
        this.widgetStreamCounterVO = widgetStreamCounterVO;

    }

    @Override
    public void run() {
        // Save the old value in DB
        // save the following in DB
        // widgetStreamCounterVO.getCounter()
        // widgetStreamCounterVO.getWidgetID()
        // widgetStreamCounterVO.getCal()
        saveinDB(widgetStreamCounterVO.getWidgetID(), widgetStreamCounterVO.getCounter(),
                widgetStreamCounterVO.getCal());
        // Reset the counter value to zero

        widgetStreamCounterVO.setCounter(0);
        // increment the Timer count to next slot
        widgetStreamCounterVO.getCal().add(Calendar.MINUTE, widgetStreamCounterVO.getResettime() / 60);

    }

    private void saveinDB(String widgetID, int counter, Calendar calendar) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);

        DashboardPojo dashboardPojo = new DashboardPojo();

        Schema schemaDashboard = ReflectData.get().getSchema(DashboardPojo.class);

        GenericRecord recordDashboard = new Record(schemaDashboard);
        recordDashboard.put("timestamp", dashboardPojo.getTimestamp());
        recordDashboard.put("widgetId", dashboardPojo.getWidgetId());
        recordDashboard.put("value", dashboardPojo.getValue());

        //System.out.println(recordDashboard);

        JsonObject input_json = null;
        input_json = factory.createObjectBuilder()
                .add("input",
                        factory.createObjectBuilder().add("eventType", "dashboard").add("eventBodyType", "avro")
                                .add("eventBody", recordDashboard.toString())
                                .add("eventKeys", factory.createArrayBuilder().add("timestamp").add("widgetId")))
                .build();

        //System.out.println(input_json);
    }

    public static class DashboardPojo {
        String timestamp;
        String widgetId;
        int value;

        public DashboardPojo() {

        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getWidgetId() {
            return widgetId;
        }

        public void setWidgetId(String widgetId) {
            this.widgetId = widgetId;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
