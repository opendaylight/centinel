/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.laas.impl;

import org.opendaylight.laas.rest.utilities.CentinelAlertConditionRESTServices;
import org.opendaylight.laas.rest.utilities.CentinelStreamRESTServices;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.ConfigurationChanged;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.ConfigurationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationChangeLaasImpl implements ConfigurationListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationChangeLaasImpl.class);
    private CentinelStreamRESTServices restServiceStream;
    private CentinelAlertConditionRESTServices restServiceAlert;

    @Override
    public void onConfigurationChanged(ConfigurationChanged notification) {
        LOG.info("ConfigurationChanged Notification received in Laas");
        restServiceStream.getInstance().setGraylogIp(notification.getGraylogIp(), notification.getGraylogPort());
        restServiceAlert.getInstance().setGraylogIp(notification.getGraylogIp(), notification.getGraylogPort());
        
    }

}
