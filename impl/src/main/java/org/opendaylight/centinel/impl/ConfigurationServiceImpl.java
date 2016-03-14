/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.ConfigurationChangedBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.ConfigurationRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.ConfigurationRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.ConfigurationService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.SetCentinelConfigurationsInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.SetCentinelConfigurationsOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.SetCentinelConfigurationsOutputBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorType;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;

public class ConfigurationServiceImpl implements ConfigurationService,
		AutoCloseable {

	private static final Logger LOG = LoggerFactory
			.getLogger(ConfigurationServiceImpl.class);

	public static final InstanceIdentifier<ConfigurationRecord> configurationRecordId = InstanceIdentifier
			.builder(ConfigurationRecord.class).build();
	private final ExecutorService executor;
	private DataBroker dataProvider;
	private NotificationProviderService notificationProvider;
	public static String graylogIp;
	public static String graylogPort;
	public static String flumeIp;
	public static String flumePort;
	public static String drillIp;
	public static String drillPort;
	public static String syslogPort;
	public static boolean secureSyslog;

	public ConfigurationServiceImpl() {
		executor = Executors.newFixedThreadPool(1);
	}

	public void setDataProvider(final DataBroker salDataProvider) {
		LOG.info(" Entered to Data Provider configuration");
		this.dataProvider = salDataProvider;
		LOG.info("data provider set configuration");
	}

	public void setNotificationProvider(
			final NotificationProviderService salService) {
		this.notificationProvider = salService;
		LOG.info("notification provider set");
	}

	@Override
	public Future<RpcResult<SetCentinelConfigurationsOutput>> setCentinelConfigurations(
			SetCentinelConfigurationsInput input) {
		final SettableFuture<RpcResult<SetCentinelConfigurationsOutput>> futureResult = SettableFuture
				.create();

		final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();

		if (input.getGraylogIp().isEmpty()
				|| input.getGraylogIp().trim().isEmpty()
				|| input.getGraylogIp() == null) {
			LOG.debug("IP cannot be null");
			return Futures
					.immediateFailedCheckedFuture(new TransactionCommitFailedException(
							"invalid-input", RpcResultBuilder.newError(
									ErrorType.APPLICATION, "invalid-input",
									"Graylog IP cannot be null")));
		}
		graylogIp = input.getGraylogIp();
		flumeIp = input.getFlumeIp();
		drillIp = input.getDrillIp();
		graylogPort = input.getGraylogPort();
		flumePort = input.getFlumePort();
		drillPort = input.getDrillPort();
		syslogPort = input.getSyslogPort();
		secureSyslog = input.isSecureSysLog();

		final SetCentinelConfigurationsOutputBuilder centinelConfigurationsOutputBuilder = new SetCentinelConfigurationsOutputBuilder();

		centinelConfigurationsOutputBuilder.setGraylogIp(graylogIp);
		centinelConfigurationsOutputBuilder.setDrillIp(drillIp);
		centinelConfigurationsOutputBuilder.setFlumeIp(flumeIp);
		centinelConfigurationsOutputBuilder.setGraylogPort(graylogPort);
		centinelConfigurationsOutputBuilder.setFlumePort(flumePort);
		centinelConfigurationsOutputBuilder.setDrillPort(drillPort);
		centinelConfigurationsOutputBuilder.setSyslogPort(syslogPort);
		centinelConfigurationsOutputBuilder.setSecureSysLog(secureSyslog);
		try {
			tx.merge(
					LogicalDatastoreType.OPERATIONAL,
					configurationRecordId,
					new ConfigurationRecordBuilder().setGraylogIp(graylogIp)
							.setDrillIp(drillIp).setFlumeIp(flumeIp)
							.setFlumePort(flumePort).setDrillPort(drillPort)
							.setGraylogPort(graylogPort)
							.setSyslogPort(syslogPort)
							.setSecureSysLog(secureSyslog).build(), true);
			tx.submit();

			notificationProvider.publish(new ConfigurationChangedBuilder()
					.setGraylogIp(graylogIp).setDrillIp(drillIp)
					.setFlumeIp(flumeIp).setGraylogPort(graylogPort)
					.setFlumePort(flumePort).setDrillPort(drillPort)
					.setSyslogPort(syslogPort).setSecureSysLog(secureSyslog)
					.build());
			LOG.info("notification published");
			futureResult.set(RpcResultBuilder
					.<SetCentinelConfigurationsOutput> success(
							centinelConfigurationsOutputBuilder.build())
					.build());
		} catch (Exception e) {
			LOG.info("Failed to commit ConfigurationRecord in operational", e);

			futureResult.set(RpcResultBuilder
					.<SetCentinelConfigurationsOutput> failed()
					.withRpcErrors(
							((TransactionCommitFailedException) e)
									.getErrorList()).build());
		}

		return futureResult;
	}

	@Override
	public void close() throws Exception {
		executor.shutdown();
	}

}
