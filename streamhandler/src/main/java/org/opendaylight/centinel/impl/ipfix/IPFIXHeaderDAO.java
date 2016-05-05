/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.impl.ipfix;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IPFIXHeaderDAO extends PacketLength {
    private int versionNumber;
    private Date exportTime;
    private long sequenceNumber;
    private long observationDomainID;
    protected int length;
    private List<IPFIXSetsDAO> setHeaders = new ArrayList<IPFIXSetsDAO>();

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Date getExportTime() {
        return exportTime;
    }

    public void setExportTime(Date exportTime) {
        this.exportTime = exportTime;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public long getObservationDomainID() {
        return observationDomainID;
    }

    public void setObservationDomainID(long observationDomainID) {
        this.observationDomainID = observationDomainID;
    }

    public List<IPFIXSetsDAO> getSetHeaders() {
        return setHeaders;
    }

}
