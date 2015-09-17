/*
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.ui;

import java.io.IOException;
import java.net.URL;
import org.osgi.service.http.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CentinelUiHttpContext implements HttpContext {

    private static final Logger LOG = LoggerFactory.getLogger(CentinelUiHttpContext.class);

    private final HttpContext defaultHttpContext;
    private final String root;

    protected CentinelUiHttpContext(HttpContext defaultHttpContext, String root) {
        this.defaultHttpContext = defaultHttpContext;
        this.root = root; // should end with '/'
    }

    @Override
    public boolean handleSecurity(javax.servlet.http.HttpServletRequest hsr, javax.servlet.http.HttpServletResponse hsr1) throws IOException {
        return defaultHttpContext.handleSecurity(hsr, hsr1);
    }

    @Override
    public URL getResource(String string) {
        String reqested = string;

        if (root.equals(string)) { // 'serve' root/index.html inplace of root/
            reqested = root + "index.html";
            LOG.debug("getResource: " + string);
        }

        return defaultHttpContext.getResource(reqested);
    }

    @Override
    public String getMimeType(String string) {
        return defaultHttpContext.getMimeType(string);
    }

}
