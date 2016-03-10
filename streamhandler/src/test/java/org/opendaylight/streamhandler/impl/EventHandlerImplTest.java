/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import org.junit.Test;
import org.mockito.Mock;

public class EventHandlerImplTest {

    @Mock
    private StreamhandlerImpl mockStreamhandlerImpl = new StreamhandlerImpl();
    MockEventHandlerImpl mockEventHandlerImpl = new MockEventHandlerImpl(mockStreamhandlerImpl);

    @Test
    public void mockOnEventNotified() {
        EventHandlerImplFactory mockEventHandlerImplFactory = new EventHandlerImplFactory();
        mockEventHandlerImpl.onEventNotified(mockEventHandlerImplFactory.setNotification());
    }
    @Test
    public void mockOnEventNotifiedEventKeyNull() {
        EventHandlerImplFactory mockEventHandlerImplFactory = new EventHandlerImplFactory();
        mockEventHandlerImpl.onEventNotified(mockEventHandlerImplFactory.setNotificationEventKeyNull());
    }
    @Test
    public void mockOnEventNotifiedCorrectJsonStream() {
        EventHandlerImplFactory mockEventHandlerImplFactory = new EventHandlerImplFactory();
        mockEventHandlerImpl.onEventNotified(mockEventHandlerImplFactory.setNotificationCorrectJsonStream());
    }
    @Test
    public void mockOnEventNotifiedCorrectJsonAlert() {
        EventHandlerImplFactory mockEventHandlerImplFactory = new EventHandlerImplFactory();
        mockEventHandlerImpl.onEventNotified(mockEventHandlerImplFactory.setNotificationCorrectJsonAlert());
    }
    private class MockEventHandlerImpl extends EventHandlerImpl {

        public MockEventHandlerImpl(StreamhandlerImpl streamHandlerImpl) {
            super(streamHandlerImpl);
        }
    }
}
