/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.laas.impl;

import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.laas.impl.LaasProvider;

/**
 * @author Monika Verma
 * 
 *         This class provides unit tests for LaasProvider.
 * 
 */
public class LaasProviderTest {
    @Test
    public void testOnSessionInitiated() {
        LaasProvider provider = new LaasProvider();
        // ensure no exceptions
        // currently this method is empty
        provider.onSessionInitiated(mock(BindingAwareBroker.ProviderContext.class));
    }

    @Test
    public void testClose() throws Exception {
        LaasProvider provider = new LaasProvider();
        // ensure no exceptions
        // currently this method is empty
        provider.close();
    }
}
