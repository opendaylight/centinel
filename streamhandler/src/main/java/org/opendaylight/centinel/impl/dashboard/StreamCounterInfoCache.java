/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opendaylight.streamhandler.impl.StreamhandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StreamCounterInfoCache {
    private static final Logger LOG = LoggerFactory.getLogger(StreamCounterInfoCache.class);

    private HashMap<String, ScheduledExecutorService> sesmap = new HashMap<String, ScheduledExecutorService>();

    private static StreamCounterInfoCache streamCounterInfoCache;

    private volatile List<WidgetStreamCounterVO> listofcounter = new ArrayList<WidgetStreamCounterVO>();

    private StreamCounterInfoCache() {

    }

    public static synchronized StreamCounterInfoCache getInstance() {
        if (null == streamCounterInfoCache) {
            LOG.info("Creating new instance of StreamCounterInfoCache");
            streamCounterInfoCache = new StreamCounterInfoCache();

            return streamCounterInfoCache;
        } else
            return streamCounterInfoCache;

    }

    @Override
    public String toString() {
        return "Current Size of Cache is:" + getListofcounter().size();

        // TODO Auto-generated method stub

    }

    public synchronized void removeCounterForWidget(String widgetID) {
        WidgetStreamCounterVO compobject = new WidgetStreamCounterVO();
        compobject.setWidgetID(widgetID);
        LOG.info("removing widgetID counter:" + widgetID);
        getListofcounter().remove(compobject);

        sesmap.get(widgetID).shutdownNow();

    }

    public synchronized List<WidgetStreamCounterVO> getListofcounter() {
        return listofcounter;
    }

    public synchronized void setListofcounter(List<WidgetStreamCounterVO> listofcounter) {
        this.listofcounter = listofcounter;
    }

    public synchronized void addCounter(WidgetStreamCounterVO counter, StreamhandlerImpl streamhandlerImpl2) {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

        Runnable threadForReset = new ResetCounterThread(counter,streamhandlerImpl2);
        Calendar cal = Calendar.getInstance();
        int minute = cal.get(Calendar.MINUTE);
        long intialdelay = 5 - minute % 5;
        cal.add(Calendar.MINUTE, (int) intialdelay);
        cal.set(Calendar.SECOND, 0);
        counter.setCal(cal);

        long period = counter.getResettime();
        TimeUnit tu = TimeUnit.SECONDS;

        ses.scheduleAtFixedRate(threadForReset, intialdelay, period, tu);
        sesmap.put(counter.getWidgetID(), ses);
        LOG.info("Add counter: initial delay: "+ intialdelay);

        this.listofcounter.add(counter);
    }

    public synchronized void incrementCounter(String streamID) {
        for (WidgetStreamCounterVO widgetStreamCounterVO : listofcounter) {
            if (widgetStreamCounterVO.getStreamID().equals(streamID)) {

                widgetStreamCounterVO.setCounter(widgetStreamCounterVO.getCounter() + 1);
            }
        }
    }

    public synchronized int getCounterValue(String widgetID) {
        // TODO Auto-generated method stub
        for (WidgetStreamCounterVO widgetStreamCounterVO : listofcounter) {
            if (widgetStreamCounterVO.getWidgetID().equals(widgetID)) {

                return widgetStreamCounterVO.getCounter();

            }
        }
        return 0;
    }

}
