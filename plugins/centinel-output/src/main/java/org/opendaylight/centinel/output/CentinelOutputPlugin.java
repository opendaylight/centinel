/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.output;

import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Monika Verma
 * 
 */
public class CentinelOutputPlugin implements Plugin {
    @Override
    public PluginMetaData metadata() {
        return new CentinelOutputMetaData();
    }

    @Override
    public Collection<PluginModule> modules() {
        return Arrays.<PluginModule> asList(new CentinelOutputModule());
    }
}
