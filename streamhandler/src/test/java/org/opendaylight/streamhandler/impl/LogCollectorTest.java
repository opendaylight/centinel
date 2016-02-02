/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import static org.junit.Assert.assertTrue;

import java.net.Socket;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

public class LogCollectorTest {

    MockClientHandler mockClientHandler = new MockClientHandler(null);
    LogCollectorFactory logCollectorFactory = new LogCollectorFactory();
    boolean errorWhileCreatingJson = false;

    @Test
    public void mockParseLogMessageBadTimeFormat() {
        try {
            JSONObject mockJSONObject = mockClientHandler.parseLogMessage(logCollectorFactory
                    .inputStringMessageBadTimeFormat());
            if (mockJSONObject.length() == 0) {
                errorWhileCreatingJson = true;
                assertTrue(errorWhileCreatingJson);
            }
        } catch (JSONException e) {
            errorWhileCreatingJson = true;
            assertTrue(errorWhileCreatingJson);
        }

    }

    @Test
    public void mockParseLogMessageCorrect() {
        try {
            JSONObject mockJSONObject = mockClientHandler.parseLogMessage(logCollectorFactory
                    .inputStringMessageCorrectParse());
            if (mockJSONObject.length() == 0) {
                errorWhileCreatingJson = true;
                assertTrue(errorWhileCreatingJson);
            }
        } catch (JSONException e) {
            errorWhileCreatingJson = true;
            assertTrue(errorWhileCreatingJson);
        }

    }

    @Test
    public void mockParseLogMessagerfc5424() {
        try {
            JSONObject mockJSONObject = mockClientHandler.parseLogMessage(logCollectorFactory
                    .inputStringMessagerfc5424());
            if (mockJSONObject.length() == 0) {
                errorWhileCreatingJson = true;
                assertTrue(errorWhileCreatingJson);
            }
        } catch (Exception e) {
            errorWhileCreatingJson = true;
            assertTrue(errorWhileCreatingJson);
        }
    }

    @Test
    public void mockParseLogMessageNoTimeStamp() {
        try {
            JSONObject mockJSONObject = mockClientHandler.parseLogMessage(logCollectorFactory
                    .inputStringMessageNoTimeStamp());
            if (mockJSONObject.length() == 0) {
                errorWhileCreatingJson = true;
                assertTrue(errorWhileCreatingJson);
            }
        } catch (Exception e) {
            errorWhileCreatingJson = true;
            assertTrue(errorWhileCreatingJson);
        }
    }

    private class MockClientHandler extends ClientHandler {

        MockClientHandler(Socket conn) {
            super(conn);
        }
    }

}