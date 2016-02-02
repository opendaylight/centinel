/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.subscribe;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.Subscribe.Mode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeDeleteInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeDeleteInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeTestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeTestInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeUserInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.SubscribeUserInputBuilder;

public class SubscriberImplFactory {

    public SubscribeDeleteInput mockInputForSubscriberDelete() {
        SubscribeDeleteInputBuilder subscribeDeleteInputBuilder = new SubscribeDeleteInputBuilder();
        subscribeDeleteInputBuilder.setSubscribeID("100");
        return subscribeDeleteInputBuilder.build();
    }

    public SubscribeUserInput mockInputForSubscribeUser() {

        SubscribeUserInputBuilder subscribeUserInputBuilder = new SubscribeUserInputBuilder();
        subscribeUserInputBuilder.setRuleID("1");
        subscribeUserInputBuilder.setStreamID("100");
        subscribeUserInputBuilder.setUserName("name");
        subscribeUserInputBuilder.setMode(Mode.Alert);
        subscribeUserInputBuilder.setSubscribeID("100");
        return subscribeUserInputBuilder.build();
    }

    public SubscribeTestInput mockInputForSubscribeTest() {
        SubscribeTestInputBuilder subscribeTestInputBuilder = new SubscribeTestInputBuilder();
        subscribeTestInputBuilder.setAlertID("100");
        subscribeTestInputBuilder.setMessage("message");
        subscribeTestInputBuilder.setStreamID("100");

        return subscribeTestInputBuilder.build();
    }

    public SubscribeUserInput mockInputForSubscribeUserNull() {

        SubscribeUserInputBuilder subscribeUserInputBuilder = new SubscribeUserInputBuilder();
        subscribeUserInputBuilder.setRuleID(null);
        subscribeUserInputBuilder.setStreamID(null);
        subscribeUserInputBuilder.setUserName(null);
        subscribeUserInputBuilder.setMode(null);
        subscribeUserInputBuilder.setSubscribeID(null);
        return subscribeUserInputBuilder.build();
    }

}
