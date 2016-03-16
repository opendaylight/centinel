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

public class ConfigurationChangeImpl implements ConfigurationListener {

	private static final Logger LOG = LoggerFactory
			.getLogger(ConfigurationChangeImpl.class);
	CommonServices commonServices = CommonServices.getInstance();

	LogCollector collect = new LogCollector();
	LogCollectorTLS collectTls = new LogCollectorTLS();
	public static volatile boolean collectThread = false;
	public static volatile boolean collectThreadSecured = false;

	public void logCollectorStart() {
		collectThread = true;
		collect.start();
	}

	@Override
	public void onConfigurationChanged(ConfigurationChanged notification) {
		LOG.info("ConfigurationChanged Notification received in streamhandler");

		if (notification.isSecureSysLog()) {
			collectThread = false;
			collectThreadSecured = true;
			collect = null;
			LOG.info("before start of secured channel");
			collectTls = new LogCollectorTLS();
			collectTls.start();
		} else {
			LOG.info("before start of un-secured channel");
			collectThreadSecured = false;
			collectThread = true;
			collectTls = null;
			collect = new LogCollector();
			collect.start();

		}

		commonServices.updateConfigurationProperties(notification);

	}

}
