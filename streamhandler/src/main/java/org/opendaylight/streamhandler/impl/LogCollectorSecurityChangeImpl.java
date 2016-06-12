/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.streamhandler.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.ConfigurationChanged;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.ConfigurationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @author Sumit kapoor
 * This class listen for settings for collector type i.e. TCP or TLS.
 */
public class LogCollectorSecurityChangeImpl implements ConfigurationListener {

    private static final Logger log = LoggerFactory.getLogger(LogCollectorSecurityChangeImpl.class);
    CommonServices commonServices = CommonServices.getInstance();

    LogCollectorTCP collect = new LogCollectorTCP();
    SecureLogCollector collectTls = new SecureLogCollector();

    public void logCollectorStart() {
        collect.start();
    }

    @Override
    public void onConfigurationChanged(ConfigurationChanged notification) {
        log.info("ConfigurationChanged Notification received in streamhandler");

        if (notification.isSecureSysLog()) {
            collect.stop();
            collect = null;
            collectTls = new SecureLogCollector();
            collectTls.start();
        } else {
            collectTls.stop();
            collectTls = null;
            collect = new LogCollectorTCP();
            collect.start();
        }

        commonServices.updateConfigurationProperties(notification);

    }

}
