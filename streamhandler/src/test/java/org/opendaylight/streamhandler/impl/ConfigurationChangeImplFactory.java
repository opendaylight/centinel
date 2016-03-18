/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.ConfigurationChanged;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.ConfigurationChangedBuilder;

public class ConfigurationChangeImplFactory {

    public ConfigurationChanged inputNotificationSecureSyslog() {
        ConfigurationChangedBuilder mockConfigurationChanged = new ConfigurationChangedBuilder();
        mockConfigurationChanged.setSecureSysLog(true);
        mockConfigurationChanged.setFlumePort("41414");
        mockConfigurationChanged.setFlumeIp("localhost");
        return mockConfigurationChanged.build();
    }

    public ConfigurationChanged inputNotificationUnSecureSyslog() {
        ConfigurationChangedBuilder mockConfigurationChanged = new ConfigurationChangedBuilder();
        mockConfigurationChanged.setSecureSysLog(false);
        mockConfigurationChanged.setFlumePort("41414");
        mockConfigurationChanged.setFlumeIp("localhost");
        return mockConfigurationChanged.build();
    }
}
