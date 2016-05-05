/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.impl.ipfix;

import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opendaylight.streamhandler.impl.StreamhandlerImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.PersistEventInputBuilder;

public class IPFIXPacketHeaderParser extends PacketLength {

    /**
     * @author Sunaina Khanna
     * 
     *         This class parses the header data of the IPFIX packet.
     * 
     * 
     */

    private final static Logger LOGGER = Logger.getLogger(IPFIXRecordSets.class.getName());
    static PersistEventInputBuilder input = null;
    static StreamhandlerImpl streamHandlerImpl = new StreamhandlerImpl();
    static PersistIpfix persistipfix = new PersistIpfix();

    public static IPFIXHeaderDAO parse(byte[] ipfixBuff) throws Exception {
        try {
            if (ipfixBuff.length < 16)
                throw new Exception("Ipfix data length too short");

            IPFIXHeaderDAO ipfixHeaderObject = new IPFIXHeaderDAO();

            // version number
            byte[] versionNumber = new byte[2];
            System.arraycopy(ipfixBuff, 0, versionNumber, 0, 2);
            ipfixHeaderObject.setVersionNumber(ConverterUtil.twoBytesToInteger(versionNumber));

            // length
            byte[] length = new byte[2];
            System.arraycopy(ipfixBuff, 2, length, 0, 2);
            ipfixHeaderObject.setLength(ConverterUtil.twoBytesToInteger(length));

            // export time
            byte[] exportTime = new byte[4];
            System.arraycopy(ipfixBuff, 4, exportTime, 0, 4);
            long secondsSinceEpoche = ConverterUtil.fourBytesToLong(exportTime);
            long milliSecondsSinceEpoche = secondsSinceEpoche * 1000;
            ipfixHeaderObject.setExportTime(new Date(milliSecondsSinceEpoche));

            // sequence number
            byte[] sequenceNumber = new byte[4];
            System.arraycopy(ipfixBuff, 8, sequenceNumber, 0, 4);
            ipfixHeaderObject.setSequenceNumber(ConverterUtil.fourBytesToLong(sequenceNumber));

            // observation domain id
            byte[] observationDomainID = new byte[4];
            System.arraycopy(ipfixBuff, 12, observationDomainID, 0, 4);
            ipfixHeaderObject.setObservationDomainID(ConverterUtil.fourBytesToLong(observationDomainID));

            persistipfix.setHeaderObjectinJson(ipfixHeaderObject);

            // set header
            int offset = 16;

            while ((ipfixHeaderObject.getLength() - offset) > 0) {
                byte[] setData = new byte[ipfixHeaderObject.getLength() - offset];
                System.arraycopy(ipfixBuff, offset, setData, 0, setData.length);
                IPFIXSetsDAO sh = IPFIXRecordSets.parse(setData);
                ipfixHeaderObject.getSetHeaders().add(sh);
                offset += ipfixHeaderObject.getLength();
            }

            if ((ipfixHeaderObject.getLength() - offset) != 0)
                LOGGER.log(Level.INFO, "Unused bytes: " + (ipfixHeaderObject.getLength() - offset));
            return ipfixHeaderObject;
        }

        catch (ParseException e) {
            throw new Exception("Parse error: " + e.getMessage());
        }
    }

}
