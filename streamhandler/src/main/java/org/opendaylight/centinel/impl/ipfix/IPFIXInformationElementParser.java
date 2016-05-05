/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.impl.ipfix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPFIXInformationElementParser implements IPFIXElement {

    /**
     * @author Sunaina Khanna
     * 
     *         This class parses the Information Elements of the IPFIX packet.
     * 
     * 
     */
    static TemplateRecord tr = new TemplateRecord();
    protected static final int LENGTH = 4;
    private static final Logger LOG = LoggerFactory.getLogger(IPFIXInformationElementParser.class);

    private int informationElementID;
    private int fieldLength;

    public int getInformationElementID() {
        return informationElementID;
    }

    public void setInformationElementID(int informationElementID) {
        this.informationElementID = informationElementID;
    }

    public int getFieldLength() {
        return fieldLength;
    }

    public void setFieldLength(int fieldLength) {
        this.fieldLength = fieldLength;
    }

    public static IPFIXInformationElementParser parse(byte[] data) throws Exception {
        try {
            System.out.println("information element parse function called");
            if (data.length < LENGTH)
                throw new Exception("Data array too short.");
            IPFIXInformationElementParser ie = new IPFIXInformationElementParser();

            // information element ID
            byte[] informationElementID = new byte[2];
            System.arraycopy(data, 0, informationElementID, 0, 2);
            ie.setInformationElementID(ConverterUtil.twoBytesToInteger(informationElementID));

            // field length
            byte[] fieldLength = new byte[2];
            System.arraycopy(data, 2, fieldLength, 0, 2);
            ie.setFieldLength(ConverterUtil.twoBytesToInteger(fieldLength));
            return ie;
        } catch (Exception e) {
            LOG.debug("Parse error: ", e);
            throw new Exception("Parse error: " + e.getMessage());
        }
    }

    public byte[] getBytes() throws Exception {
        try {
            byte[] data = new byte[LENGTH];
            // information element ID
            System.arraycopy(ConverterUtil.intToTwoBytes(getInformationElementID()), 0, data, 0, 2);
            // field length
            System.arraycopy(ConverterUtil.intToTwoBytes(getFieldLength()), 0, data, 2, 2);
            return data;
        } catch (Exception e) {
            LOG.debug("Error while generating the bytes: ", e);
            throw new Exception("Error while generating the bytes: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[InformationElement]: ");
        sb.append("ID: ");
        sb.append(informationElementID);
        sb.append(", ");
        sb.append("Field length: ");
        sb.append(fieldLength);

        return sb.toString();
    }
}
