/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.impl.ipfix;

import java.util.logging.Level;
import java.util.logging.Logger;

public class IPFIXRecordSets {
    private final static Logger LOG = Logger.getLogger(IPFIXRecordSets.class.getName());

    public static IPFIXSetsDAO parse(byte[] setData) throws Exception {
        try {

            PersistIpfix persistipfix = new PersistIpfix();
            if (setData.length < 4)
                throw new Exception("Data array too short.");
            IPFIXSetsDAO setsObject = new IPFIXSetsDAO();

            // set id
            byte[] setID = new byte[2];
            System.arraycopy(setData, 0, setID, 0, 2);
            setsObject.setSetID(ConverterUtil.twoBytesToInteger(setID));

            // length
            byte[] length = new byte[2];
            System.arraycopy(setData, 2, length, 0, 2);
            setsObject.setLength(ConverterUtil.twoBytesToInteger(length));
            persistipfix.setSetHeaderObjectinJson(setsObject);

            // 2 -> template sets;
            if (setsObject.getSetID() == 2) {
                int offset = 4;
                byte[] subData = new byte[setsObject.getLength() - offset];
                System.arraycopy(setData, offset, subData, 0, subData.length);
                TemplateRecord tr = new TemplateRecord();
                tr.parse(subData);
                setsObject.getTemplateRecords().add(tr);
            }
            // 3 -> template option sets
            else if (setsObject.getSetID() == 3) {
                int offset = 4;
                byte[] subData = new byte[setsObject.getLength() - offset];
                System.arraycopy(setData, offset, subData, 0, subData.length);
                OptionTemplateRecord otr = OptionTemplateRecord.parse(subData);
                setsObject.getOptionTemplateRecords().add(otr);
            }

            // > 256 -> data record;
            else if (setsObject.getSetID() == 256) {
                int offset = 4;
                byte[] subData = new byte[setsObject.getLength() - offset];
                System.arraycopy(setData, offset, subData, 0, IPFIXDataSetParser.LENGTH);
                IPFIXDataSetParser sdr = IPFIXDataSetParser.parse(subData);
                setsObject.getDataRecords().add(sdr);

            } else {
                LOG.log(Level.INFO, "Set ID " + setsObject.getSetID() + " is unknown and not handled");
            }
            return setsObject;
        } catch (Exception e) {
            throw new Exception("Parse error: " + e.getMessage());
        }
    }

}
