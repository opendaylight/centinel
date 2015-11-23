/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.output;

import org.graylog2.plugin.PluginConfigBean;
import org.graylog2.plugin.PluginModule;
import org.graylog2.plugin.outputs.MessageOutput;

import com.google.inject.multibindings.MapBinder;

import java.util.Collections;
import java.util.Set;

/**
 * @author Monika Verma
 * 
 */
public class CentinelOutputModule extends PluginModule {
    /**
     * Returns all configuration beans required by this plugin.
     * 
     * Implementing this method is optional. The default method returns an empty
     * {@link Set}.
     */
    @Override
    public Set<? extends PluginConfigBean> getConfigBeans() {
        return Collections.emptySet();
    }

    @Override
    protected void configure() {
        final MapBinder<String, MessageOutput.Factory<? extends MessageOutput>> outputMapBinder = outputsMapBinder();
        installOutput(outputMapBinder, CentinelOutput.class, CentinelOutput.Factory.class);
    }
}
