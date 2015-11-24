/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.dashboard;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WidgetStreamCounterVO {
    private static final Logger LOG = LoggerFactory.getLogger(WidgetStreamCounterVO.class);

    private String WidgetID;
    private String StreamID;
    private volatile int counter;
    private int resettime;
    private Enum<?> mode;
    private Calendar cal;

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if (obj instanceof WidgetStreamCounterVO) {
            return this.WidgetID.equals(((WidgetStreamCounterVO) obj).getWidgetID());
        } else
            return false;

    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return (WidgetID + StreamID).hashCode();
    }

    public String getWidgetID() {
        return WidgetID;
    }

    public void setWidgetID(String widgetID) {
        WidgetID = widgetID;
    }

    public String getStreamID() {
        return StreamID;
    }

    public void setStreamID(String streamID) {
        StreamID = streamID;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getResettime() {
        return resettime;
    }

    public void setResettime(int resettime) {
        this.resettime = resettime;
    }

    public Enum getMode() {
        return mode;
    }

    public void setMode(Enum mode) {
        this.mode = mode;
    }

    public Calendar getCal() {
        return cal;
    }

    public void setCal(Calendar cal) {
        this.cal = cal;
    }

}
