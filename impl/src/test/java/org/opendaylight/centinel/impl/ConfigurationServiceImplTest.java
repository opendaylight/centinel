/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.ConfigurationRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.SetCentinelConfigurationsInput;

import com.google.common.util.concurrent.CheckedFuture;

public class ConfigurationServiceImplTest {

	@Mock
	private DataBroker mockDataBroker;
	private NotificationProviderService notificationProviderService;

	@Mock
	private ConfigurationServiceImpl mockConfigurationServiceImpl;
	private MockConfigurationServiceImpl MockConfServiceImpl = new MockConfigurationServiceImpl();
	private ConfigurationServiceImplFactory configurationServiceImplFactory = new ConfigurationServiceImplFactory();

	@Before
	public void beforeTest() {
		MockitoAnnotations.initMocks(this);
		configurationServiceImplFactory = new ConfigurationServiceImplFactory();
		MockConfServiceImpl.setDataProvider(mockDataBroker);
		MockConfServiceImpl
				.setNotificationProvider(notificationProviderService);
	}

	@Test
	public void TestsetCentinelConfigurations() {
		SetCentinelConfigurationsInput input = configurationServiceImplFactory
				.setCentinelConfigurationsInput();

		try {
			ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
			/*
			 * doReturn(notificationProviderService).when(
			 * notificationProviderService).publish(new
			 * ConfigurationChangedBuilder(). setGraylogIp(input.getGraylogIp())
			 * .setDrillIp(input.getDrillIp()).setFlumeIp(input.getFlumeIp())
			 * .setFlumePort
			 * (input.getFlumePort()).setDrillPort(input.getDrillPort())
			 * .setGraylogPort(input.getGraylogPort())
			 * .setSyslogPort(input.getSyslogPort())
			 * .setSecureSysLog(input.isSecureSysLog()) .build());
			 */
			doReturn(mockReadWriteTx).when(mockDataBroker)
					.newReadWriteTransaction();
			doNothing().when(mockReadWriteTx).merge(
					LogicalDatastoreType.OPERATIONAL,
					mockConfigurationServiceImpl.configurationRecordId,
					new ConfigurationRecordBuilder()
							.setGraylogIp(input.getGraylogIp())
							.setDrillIp(input.getDrillIp())
							.setFlumeIp(input.getFlumeIp())
							.setFlumePort(input.getFlumePort())
							.setDrillPort(input.getDrillPort())
							.setGraylogPort(input.getGraylogPort())
							.setSyslogPort(input.getSyslogPort())
							.setSecureSysLog(input.isSecureSysLog()).build(),
					false);
			doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
					.submit();
			MockConfServiceImpl.setCentinelConfigurations(input);
			MockConfServiceImpl.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private class MockConfigurationServiceImpl extends ConfigurationServiceImpl {

	}
}
