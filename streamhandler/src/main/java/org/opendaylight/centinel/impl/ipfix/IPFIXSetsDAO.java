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

public class IPFIXSetsDAO extends PacketLength {
    private List<DataSetRecord> dataRecords = new ArrayList<DataSetRecord>();
    private List<TemplateRecord> templateRecords = new ArrayList<TemplateRecord>();
    private final static int SETHEADERLENGTH = 4;
    private int setID;

    public int getSetID() {
        return setID;
    }

    public void setSetID(int setID) {
        this.setID = setID;
    }

    public List<DataSetRecord> getDataRecords() {
        return dataRecords;
    }

    public void setDataRecords(List<DataSetRecord> dataRecords) {
        this.dataRecords = dataRecords;
        updateLength();
    }

    public List<TemplateRecord> getTemplateRecords() {
        return templateRecords;
    }

    public void setTemplateRecords(List<TemplateRecord> templateRecords) {
        this.templateRecords = templateRecords;
        updateLength();
    }

    private void updateLength() {
        int newLength = SETHEADERLENGTH;
        for (TemplateRecord template : templateRecords) {
            newLength += template.getLength();
        }
        for (DataSetRecord record : dataRecords) {
            newLength += record.getLength();
        }
        this.length = newLength;
    }

}
