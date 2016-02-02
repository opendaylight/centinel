/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.dashboard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.junit.Test;
import org.opendaylight.streamhandler.impl.StreamhandlerImpl;

public class ResetCounterThreadTest {

    private StreamhandlerImpl mockStreamhandlerImpl = new StreamhandlerImpl();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void runTest() {
        ResetCounterThreadFactory resetCounterThreadFactory = new ResetCounterThreadFactory();
        WidgetStreamCounterVO mockWidgetStreamCounterVO = resetCounterThreadFactory.setValuesForSaveInDb();
        ResetCounterThread mockResetCounterThread = new ResetCounterThread(mockWidgetStreamCounterVO,
                mockStreamhandlerImpl);
        mockResetCounterThread.run();
    }
}
