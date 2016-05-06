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

public class IPFIXDataSetParser extends DataSetRecord {
    public static final int LENGTH = 16; // 2 bytes padding
    static PersistIpfix persistipfix = new PersistIpfix();
    private static final Logger LOG = LoggerFactory.getLogger(IPFIXDataSetParser.class);

    private long observationPointId;
    private int selectorAlgorithm;
    private long samplingPacketInterval;
    private long samplingPacketSpace;
    private long sourceMacAddress;

    public long getObservationDomainId() {
        return observationPointId;
    }

    public void setObservationDomainId(long observationDomainId) {
        this.observationPointId = observationDomainId;
    }

    public int getSelectorAlgorithm() {
        return selectorAlgorithm;
    }

    public void setSelectorAlgorithm(int selectorAlgorithm) {
        this.selectorAlgorithm = selectorAlgorithm;
    }

    public long getSamplingPacketInterval() {
        return samplingPacketInterval;
    }

    public void setSamplingPacketInterval(long samplingPacketInterval) {
        this.samplingPacketInterval = samplingPacketInterval;
    }

    public long getSamplingPacketSpace() {
        return samplingPacketSpace;
    }

    public void setSamplingPacketSpace(long samplingPacketSpace) {
        this.samplingPacketSpace = samplingPacketSpace;
    }

    public void setSourceMacAddress(long sourceMacAddress) {
        this.sourceMacAddress = sourceMacAddress;
    }

    @Override
    public int getLength() {
        return LENGTH;
    }

    public static IPFIXDataSetParser parse(byte[] data) throws Exception {
        try {
            if (data.length < LENGTH)
                throw new Exception("Data array too short.");
            IPFIXDataSetParser sdr = new IPFIXDataSetParser();
            // observationDomainId
            byte[] observationPointId = new byte[4];
            System.arraycopy(data, 0, observationPointId, 0, 4);
            sdr.setObservationDomainId(ConverterUtil.fourBytesToLong(observationPointId));

            // selectorAlgorithm
            byte[] sourceMacAddress = new byte[6];
            System.arraycopy(data, 4, sourceMacAddress, 0, 2);
            sdr.setSourceMacAddress(ConverterUtil.sixBytesToLong(sourceMacAddress));

            persistipfix.setSetDataObjectInJson(sdr);
            return sdr;
        } catch (Exception e) {
            LOG.debug("Parse Error", e);
            throw new Exception("Parse error:" + e.getMessage());

        }
    }

    public byte[] getBytes() throws Exception {
        try {
            byte[] data = new byte[LENGTH];
            // observationDomainId
            System.arraycopy(ConverterUtil.longToFourBytes(getObservationDomainId()), 0, data, 0, 4);
            // selectorAlgorithm
            System.arraycopy(ConverterUtil.intToTwoBytes(getSelectorAlgorithm()), 0, data, 4, 2);
            // samplingPacketInterval
            System.arraycopy(ConverterUtil.longToFourBytes(getSamplingPacketInterval()), 0, data, 6, 4);
            // samplingPacketSpace
            System.arraycopy(ConverterUtil.longToFourBytes(getSamplingPacketSpace()), 0, data, 10, 4);
            return data;
        } catch (Exception e) {
            LOG.debug("Error while generating the bytes:", e);
            throw new Exception("Error while generating the bytes: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[SamplingDataRecord]: ");
        sb.append(" Observation domain ID: ");
        sb.append(observationPointId);
        sb.append(", Selector algorithm: ");
        sb.append(selectorAlgorithm);
        sb.append(", Sampling packet interval: ");
        sb.append(samplingPacketInterval);
        sb.append(", Sampling packet space: ");
        sb.append(samplingPacketSpace);

        return sb.toString();
    }
}
