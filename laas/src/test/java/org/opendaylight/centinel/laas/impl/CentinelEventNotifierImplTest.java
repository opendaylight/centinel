/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.laas.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.laas.impl.CentinelEventNotifierImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.eventinput.rev150105.NotifyEventInputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;

/**
 * @author Monika Verma
 * 
 *         This class provides unit tests for CentinelEventNotifierImpl.
 * 
 */
public class CentinelEventNotifierImplTest {

    CentinelEventNotifierImpl centinelEventNotifierImpl = new CentinelEventNotifierImpl();

    @Mock
    private NotificationProviderService mockNotificationProviderService;

    @Before
    public void beforeTest() {
        MockitoAnnotations.initMocks(this);
        centinelEventNotifierImpl.setNotificationProvider(mockNotificationProviderService);
    }

    @After
    public void AfterTest() {
        try {
            centinelEventNotifierImpl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        centinelEventNotifierImpl = null;
    }

    @Test
    public void testnotifyEvent() {

        NotifyEventInputBuilder input = new NotifyEventInputBuilder();
        Future<RpcResult<Void>> result = centinelEventNotifierImpl.notifyEvent(input.build());
        try {
            assertTrue(result.get().isSuccessful());
        } catch (InterruptedException e) {
            fail("InterruptedException caught");
            e.printStackTrace();
        } catch (ExecutionException e) {
            fail("ExecutionException caught");
            e.printStackTrace();
        }
    }

}
