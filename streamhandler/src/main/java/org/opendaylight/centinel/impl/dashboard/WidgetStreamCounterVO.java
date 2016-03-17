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

    private String widgetID;
    private String streamID;
    private volatile int counter;
    private int resettime;
    private Enum<?> mode;
    private Calendar cal;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WidgetStreamCounterVO) {
            return this.widgetID.equals(((WidgetStreamCounterVO) obj).getWidgetID());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (widgetID + streamID).hashCode();
    }

    public String getWidgetID() {
        return widgetID;
    }

    public void setWidgetID(String widgtID) {
        widgetID = widgtID;
    }

    public String getStreamID() {
        return streamID;
    }

    public void setStreamID(String strmID) {
        streamID = strmID;
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
