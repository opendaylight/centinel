/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.SetCentinelConfigurationsInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.SetCentinelConfigurationsInputBuilder;

public class ConfigurationServiceImplFactory {

	public SetCentinelConfigurationsInput setCentinelConfigurationsInput() {
		SetCentinelConfigurationsInputBuilder setCentinelConfigurationsInputBuilder = new SetCentinelConfigurationsInputBuilder();
		setCentinelConfigurationsInputBuilder.setDrillIp("localhost");
		setCentinelConfigurationsInputBuilder.setDrillPort("8047");
		setCentinelConfigurationsInputBuilder.setFlumeIp("localhost");
		setCentinelConfigurationsInputBuilder.setFlumePort("41414");
		setCentinelConfigurationsInputBuilder.setGraylogIp("localhost");
		setCentinelConfigurationsInputBuilder.setGraylogPort("12900");
		setCentinelConfigurationsInputBuilder.setSecureSysLog(true);
		setCentinelConfigurationsInputBuilder.setSyslogPort("1514");
		return setCentinelConfigurationsInputBuilder.build();
	}

}
