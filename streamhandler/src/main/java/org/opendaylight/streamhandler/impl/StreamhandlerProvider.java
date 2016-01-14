/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.RpcRegistration;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.StreamhandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamhandlerProvider implements AutoCloseable, BindingAwareProvider {

    private static final Logger LOG = LoggerFactory.getLogger(StreamhandlerProvider.class);
    private RpcRegistration<StreamhandlerService> streamhandlerService;
	
	@Override
	public void onSessionInitiated(ProviderContext session) {
		streamhandlerService = session.addRpcImplementation(StreamhandlerService.class, new StreamhandlerImpl());
		 LOG.info("Stream handler provider initated");
	}

	@Override
	public void close() throws Exception {
		if (streamhandlerService != null) {
			streamhandlerService.close();
        }
        LOG.info("Stream handler provider Closed");
    }

}
