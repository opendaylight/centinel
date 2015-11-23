/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.alertcallback;

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

/**
 * @author Monika Verma
 * 
 */
public class CentinelAlertCallbackMetaData implements PluginMetaData {
    @Override
    public String getUniqueId() {
        return CentinelAlertCallback.class.getCanonicalName();
    }

    @Override
    public String getName() {
        return "Centinel Alarmcallback Plugin";
    }

    @Override
    public String getAuthor() {
        return "Monika Verma";
    }

    @Override
    public URI getURL() {
        return URI.create("https://www.graylog.org");
    }

    @Override
    public Version getVersion() {
        return new Version(1, 1, 5);
    }

    @Override
    public String getDescription() {
        return "Alarm callback plugin that sends all stream alerts to a defined Centinel api.";
    }

    @Override
    public Version getRequiredVersion() {
        return new Version(1, 0, 0);
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}