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

import org.json.JSONObject;
import org.opendaylight.streamhandler.impl.StreamhandlerImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.PersistEventInputBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateRecord extends PacketLength {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateRecord.class);

    static PersistEventInputBuilder input = null;
    static StreamhandlerImpl streamHandlerImpl = new StreamhandlerImpl();
    static JSONObject headerJson = new JSONObject();
    static IPFIXSetsDAO setsObject = new IPFIXSetsDAO();
    static TemplateRecord tr = new TemplateRecord();
    static PersistIpfix persistipfix = new PersistIpfix();

    private int templateID;
    private int fieldCount;
    private List<IPFIXInformationElementParser> informationElements = new ArrayList<IPFIXInformationElementParser>();
    private List<Integer> templateIds = new ArrayList<Integer>();

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

    public List<IPFIXInformationElementParser> getInformationElements() {
        return informationElements;
    }

    public List<Integer> getTemplateIds() {
        return templateIds;
    }

    public List<Integer> setTemplateIds() {
        return templateIds;
    }

    public void setInformationElements(List<IPFIXInformationElementParser> informationElements) {
        this.informationElements = informationElements;
    }

    public TemplateRecord parse(byte[] data) throws Exception {
        try {
            if (data.length < 4)
                throw new Exception("Data array too short.");

            // template ID
            byte[] templateID = new byte[2];
            System.arraycopy(data, 0, templateID, 0, 2);
            tr.setTemplateID(ConverterUtil.twoBytesToInteger(templateID));
            tr.setTemplateIds().add(ConverterUtil.twoBytesToInteger(templateID));

            // field count
            byte[] fieldCount = new byte[2];
            System.arraycopy(data, 2, fieldCount, 0, 2);
            tr.setFieldCount(ConverterUtil.twoBytesToInteger(fieldCount));

            PersistIpfix.setTemplateHeaderObjectinJson(tr);
            int offset = 4;

            // passing subdata to retreive information elements from template
            // record.
            for (int i = 0; i <= tr.getFieldCount() - 1; i++) {
                byte[] subData = new byte[IPFIXInformationElementParser.LENGTH];
                System.arraycopy(data, offset + (i * IPFIXInformationElementParser.LENGTH), subData, 0,
                        IPFIXInformationElementParser.LENGTH);
                IPFIXInformationElementParser ie = IPFIXInformationElementParser.parse(subData);
                tr.getInformationElements().add(ie);
            }
            PersistIpfix.setInformationElementObjectinJson(tr.getInformationElements());
            tr.length = data.length;

            return tr;
        } catch (Exception e) {
            LOG.debug("parse error", e);
            throw new Exception("Parse error: " + e.getMessage());
        }
    }

}
