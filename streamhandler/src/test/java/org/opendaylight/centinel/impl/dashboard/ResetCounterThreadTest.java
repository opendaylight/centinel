/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.dashboard;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.junit.Test;

import org.mockito.Mockito;
import org.opendaylight.streamhandler.impl.StreamhandlerImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.EventBodyType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.PersistEventInputBuilder;

public class ResetCounterThreadTest {

    private StreamhandlerImpl mockStreamhandlerImpl = new StreamhandlerImpl();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void run() {
        ResetCounterThreadFactory resetCounterThreadFactory = new ResetCounterThreadFactory();
        WidgetStreamCounterVO mockWidgetStreamCounterVO = resetCounterThreadFactory.setValuesForSaveInDb();
        ResetCounterThread mockResetCounterThread = new ResetCounterThread(mockWidgetStreamCounterVO,
                mockStreamhandlerImpl);
        try {
            Class c = ResetCounterThread.class;
            Method m;
            m = c.getDeclaredMethod("saveinDB", new Class[] { String.class, int.class, Calendar.class });
            m.setAccessible(true);
            m.invoke(mockResetCounterThread, mockWidgetStreamCounterVO.getWidgetID(),
                    mockWidgetStreamCounterVO.getCounter(), mockWidgetStreamCounterVO.getCal());
        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
