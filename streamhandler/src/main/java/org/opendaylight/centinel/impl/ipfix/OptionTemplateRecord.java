/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.impl.ipfix;

import java.util.ArrayList;
import java.util.List;


public class OptionTemplateRecord extends Record {
    private final static int HEADERLENGTH = 6;
    private int templateID;
    private int fieldCount;
    private int scopeFieldCount;
    private List<IPFIXInformationElementParser> informationElements = new ArrayList<IPFIXInformationElementParser>();
    private List<IPFIXInformationElementParser> scopeInformationElements = new ArrayList<IPFIXInformationElementParser>();

    public int getTemplateID() {
        return templateID;
    }

    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(int fieldCount) {
        this.fieldCount = fieldCount;
    }

    public int getScopeFieldCount() {
        return scopeFieldCount;
    }

    public void setScopeFieldCount(int scopeFieldCount) {
        this.scopeFieldCount = scopeFieldCount;
    }

    public List<IPFIXInformationElementParser> getInformationElements() {
        return informationElements;
    }

    public void setInformationElements(List<IPFIXInformationElementParser> informationElements) {
        this.informationElements = informationElements;
    }

    public List<IPFIXInformationElementParser> getScopeInformationElements() {
        return scopeInformationElements;
    }

    public void setScopeInformationElements(List<IPFIXInformationElementParser> scopeInformationElements) {
        this.scopeInformationElements = scopeInformationElements;
    }

    public static OptionTemplateRecord parse(byte[] data) throws Exception {
        try {
            if (data.length < 6)
                throw new Exception("Data array too short.");
            OptionTemplateRecord otr = new OptionTemplateRecord();
            // template ID
            byte[] templateID = new byte[2];
            System.arraycopy(data, 0, templateID, 0, 2);
            otr.setTemplateID(ConverterUtil.twoBytesToInteger(templateID));
            // field count
            byte[] fieldCount = new byte[2];
            System.arraycopy(data, 2, fieldCount, 0, 2);
            otr.setFieldCount(ConverterUtil.twoBytesToInteger(fieldCount));
            // scope field count
            byte[] scopeFieldCount = new byte[2];
            System.arraycopy(data, 4, scopeFieldCount, 0, 2);
            otr.setScopeFieldCount(ConverterUtil.twoBytesToInteger(scopeFieldCount));
            int offset = 6;
            for (int i = 0; i < otr.getFieldCount(); i++) {
                byte[] subData = new byte[IPFIXInformationElementParser.LENGTH];
                System.arraycopy(data, offset + (i * IPFIXInformationElementParser.LENGTH), subData, 0,
                        IPFIXInformationElementParser.LENGTH);
                IPFIXInformationElementParser ie = IPFIXInformationElementParser.parse(subData);
                if (i < otr.getScopeFieldCount()) {
                    otr.getScopeInformationElements().add(ie);
                } else {
                    otr.getInformationElements().add(ie);
                }
            }
            otr.length = data.length;
            return otr;
        } catch (Exception e) {
            //Log.debug("parse error", e);
            throw new Exception("Parse error: " + e.getMessage());
        }
    }

    public byte[] getBytes() throws Exception {
        try {
            int length = getLength();
            if (length % 4 != 0)
                length += (length % 4); // padding
            byte[] data = new byte[length];
            // template ID
            System.arraycopy(ConverterUtil.intToTwoBytes(getTemplateID()), 0, data, 0, 2);
            // field count
            System.arraycopy(ConverterUtil.intToTwoBytes(getFieldCount()), 0, data, 2, 2);
            // scope field count
            System.arraycopy(ConverterUtil.intToTwoBytes(getScopeFieldCount()), 0, data, 4, 2);
            // information elements
            int offset = 6;
            for (IPFIXInformationElementParser ie : scopeInformationElements) {
                System.arraycopy(ie.getBytes(), 0, data, offset, IPFIXInformationElementParser.LENGTH);
                offset += IPFIXInformationElementParser.LENGTH;
            }
            for (IPFIXInformationElementParser ie : informationElements) {
                System.arraycopy(ie.getBytes(), 0, data, offset, IPFIXInformationElementParser.LENGTH);
                offset += IPFIXInformationElementParser.LENGTH;
            }
            return data;
        } catch (Exception e) {
           // Log.debug("Error while getting the bytes:", e);
            throw new Exception("Error while getting the bytes: " + e.getMessage());
        }
    }

    @Override
    public int getLength() {
        int paddingLength = HEADERLENGTH + (scopeInformationElements.size() * IPFIXInformationElementParser.LENGTH)
                + (informationElements.size() * IPFIXInformationElementParser.LENGTH);
        while (paddingLength % 4 != 0) {
            paddingLength++;
        }
        return paddingLength;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[OptionTemplateRecord]: ");
        sb.append("Template ID: ");
        sb.append(templateID);
        sb.append(", Field count: ");
        sb.append(fieldCount);
        sb.append(", Scope field count: ");
        sb.append(scopeFieldCount);
        sb.append(", Information elements: ");
        sb.append(informationElements);
        sb.append(", Scope Information elements: ");
        sb.append(scopeInformationElements.size());
        sb.append(", ");
        for (IPFIXInformationElementParser ie : informationElements) {
            sb.append(ie);
            sb.append(", ");
        }

        return sb.toString();
    }
}
