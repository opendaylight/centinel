/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.laas.rest.utilities;

/*
 * @author: Sumit Kapoor
 *
 *  This provides Kafka properties
  */

public interface KafkaProperties {
    static final String zkConnect = "localhost:9092";
    static final String syslogTopic = "syslog";
    static final String openflowTopic = "openflow";
    static final String ipfixTopic = "ipfix";

}

