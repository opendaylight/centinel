/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.subscribe;

import java.util.List;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.Subscription;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.subscription.Subscriptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SubscriberInfoCache {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriberInfoCache.class);

    private static SubscriberInfoCache subscriberInfoCache;

    private volatile Subscription subscription;

    public synchronized Subscription getSubscription() {
        return subscription;
    }

    public synchronized void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    private SubscriberInfoCache() {

    }

    public static synchronized SubscriberInfoCache getInstance() {
        if (null == subscriberInfoCache) {
            LOG.info("Creating new instance of SubscriberInfoCache");
            subscriberInfoCache = new SubscriberInfoCache();
            Subscription subscriptioninit = null;

            subscriberInfoCache.setSubscription(subscriptioninit);

            return subscriberInfoCache;
        } else
            return subscriberInfoCache;

    }

    @Override
    public String toString() {

        List<Subscriptions> subscriptions = getSubscription().getSubscriptions();
        String Final = null;

        for (Subscriptions subscriptionitem : subscriptions) {
            Final = Final + subscriptionitem.getUserName().concat(":") + subscriptionitem.getURL().concat(":") +

            subscriptionitem.getMode();

        }
        return Final;

    }

}
