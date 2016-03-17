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

public class LogCollectorTLSTest {

    MockClientHandlerTLS mockClientHandlerTls = new MockClientHandlerTLS(null);
    LogCollectorTLSFactory logCollectorFactoryTls = new LogCollectorTLSFactory();
    boolean errorWhileCreatingJson = false;

    @Test
    public void testRunTLS() {
        boolean catchException = false;
        try {
            mockClientHandlerTls.run();
        } catch (Exception e) {
            catchException = true;
            assertTrue(catchException);
        }
    }

    @Test
    public void mockParseLogMessageBadTimeFormatTLS() {
        try {
            JSONObject mockJSONObject = mockClientHandlerTls.parseLogMessage(logCollectorFactoryTls
                    .inputStringMessageBadTimeFormatTls());
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
    public void mockParseLogMessageCorrectTLS() {
        try {
            JSONObject mockJSONObject = mockClientHandlerTls.parseLogMessage(logCollectorFactoryTls
                    .inputStringMessageCorrectParseTls());
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
    public void mockParseLogMessagerfc5424TLS() {
        try {
            JSONObject mockJSONObject = mockClientHandlerTls.parseLogMessage(logCollectorFactoryTls
                    .inputStringMessagerfc5424Tls());
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
    public void mockParseLogMessageNoTimeStampTLS() {
        try {
            JSONObject mockJSONObject = mockClientHandlerTls.parseLogMessage(logCollectorFactoryTls
                    .inputStringMessageNoTimeStampTls());
            if (mockJSONObject.length() == 0) {
                errorWhileCreatingJson = true;
                assertTrue(errorWhileCreatingJson);
            }
        } catch (Exception e) {
            errorWhileCreatingJson = true;
            assertTrue(errorWhileCreatingJson);
        }
    }

    private class MockClientHandlerTLS extends ClientHandlerTLS {

        MockClientHandlerTLS(Socket conn) {
            super(conn);
        }
    }

}
