/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.impl.ipfix;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.opendaylight.streamhandler.impl.CommonServices;
import org.opendaylight.streamhandler.impl.LogCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sunaina Khanna
 * 
 *         This class collects the packet data from the socket and forwards it
 *         to Ipfix parser
 * 
 */
public class IpfixCollector extends Thread {

    private DatagramSocket socket;
    public DatagramSocket socketAssigned;

    public DatagramSocket assignSocket() throws Exception {
        try{
        this.socket = new DatagramSocket(2056);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return socket;
    }

    private boolean running = true;
    private static final Logger LOG = LoggerFactory.getLogger(LogCollector.class);
    CommonServices commonServices = CommonServices.getInstance();

    public void run() {
        try {
            socketAssigned = assignSocket();
            if (socketAssigned == null || socketAssigned.isClosed()) {
                close();
            } else {
                while (running) {
                    byte data[] = new byte[2048];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    try {
                        if (packet != null) {
                            socketAssigned.receive(packet);
                            byte[] buff = packet.getData();
                            IPFIXPacketHeaderParser.parse(buff);
                        }
                    } catch (IOException e) {
                        LOG.debug("Error in receiving packet", e);
                        close();
                    }
                }
            }
        } catch (Exception e) {
            LOG.debug("Error", e);
        }
    }

    public void close() {
        running = false;
        try {
            if (socketAssigned != null) {
                socketAssigned.close();
            }
        } catch (Exception e) {
            LOG.debug("Error in closing the socket", e);
        }
    }
}
