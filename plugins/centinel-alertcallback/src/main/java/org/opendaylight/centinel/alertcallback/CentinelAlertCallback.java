/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.alertcallback;

import java.util.Map;

import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.alarms.callbacks.AlarmCallback;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackConfigurationException;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationException;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.streams.Stream;

import com.google.common.collect.Maps;

/**
 * @author Monika Verma
 * 
 *         This is the centinel-alarmCallback plugin. It forwards the graylog
 *         alerts to centinel api
 * 
 */

public class CentinelAlertCallback implements AlarmCallback {

    private static String name = "centinel-alarm-callback";

    private Configuration configuration;

    @Override
    public void initialize(final Configuration config) throws AlarmCallbackConfigurationException {
        this.configuration = config;
    }

    @Override
    public void call(Stream stream, AlertCondition.CheckResult result) throws AlarmCallbackException {
        final CentinelRESTClient client = CentinelRESTClient.getInstance();

        client.trigger(result, stream);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Maps.transformEntries(configuration.getSource(), new Maps.EntryTransformer<String, Object, Object>() {
            @Override
            public Object transformEntry(String key, Object value) {
                return value;
            }
        });
    }

    @Override
    public void checkConfiguration() throws ConfigurationException {
        // no configurations to check
    }

    @Override
    public ConfigurationRequest getRequestedConfiguration() {
        return new ConfigurationRequest();
    }

    @Override
    public String getName() {
        return name;
    }

}