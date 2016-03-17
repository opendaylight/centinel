/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import java.net.Socket;

import org.junit.Test;

public class ConfigurationChangeImplTest {
    MockConfigurationChangeImpl mockConfigurationChangeImpl = new MockConfigurationChangeImpl();
    LogCollector mockCollect = new LogCollector();
    LogCollectorTLS mockCollectTls = new LogCollectorTLS();
    public static volatile boolean mockCollectThread = false;
    public static volatile boolean mockCollectThreadSecured = false;

    @Test
    public void calllogCollectorStart() {
        mockConfigurationChangeImpl.logCollectorStart();
    }

    @Test
    public void callonConfigurationChangedSecured() {
        ConfigurationChangeImplFactory configurationChangeImplFactory = new ConfigurationChangeImplFactory();
        mockConfigurationChangeImpl.onConfigurationChanged(configurationChangeImplFactory
                .inputNotificationSecureSyslog());
    }

    @Test
    public void callonConfigurationChangedUnSecured() {
        ConfigurationChangeImplFactory configurationChangeImplFactory = new ConfigurationChangeImplFactory();
        mockConfigurationChangeImpl.onConfigurationChanged(configurationChangeImplFactory
                .inputNotificationUnSecureSyslog());
    }

    private class MockConfigurationChangeImpl extends ConfigurationChangeImpl {
        MockConfigurationChangeImpl() {
        }
    }
}
