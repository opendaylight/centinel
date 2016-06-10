/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.impl.ofstatsextractor;

import java.util.Properties;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.statistics.rev130819.FlowStatisticsData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.statistics.rev130819.flow.statistics.FlowStatistics;
import org.opendaylight.yangtools.yang.binding.DataObject;

public class CentinelOpenFlowFlowStatisticsExtractor extends CentinelOpenFlowAbstractStatsExtractor {

    JsonBuilderFactory factory;

    @Override
    public void processStats(DataObject dataObject, JsonBuilderFactory factory, Properties properties) {

        this.factory = factory;
        this.properties = properties;
        FlowStatisticsData ofStatData = (FlowStatisticsData) dataObject;
        FlowStatistics ofStat = ofStatData.getFlowStatistics();
        if (ofStat == null) {
            return;
        }
        sendToNewPersistenceService(ofStat, objectToJsonMapper(ofStat));
    }

    @Override
    public JsonObject objectToJsonMapper(DataObject ofStat) {

        JsonObject flowStatisticsJsonObject = null;
        FlowStatistics flowStat = (FlowStatistics) ofStat;

        if (flowStat.getByteCount() != null && flowStat.getDuration() != null && flowStat.getPacketCount() != null) {
            flowStatisticsJsonObject = factory.createObjectBuilder()
                    .add(properties.getProperty("STAT_TYPE"), properties.getProperty("FLOW_STAT"))
                    .add(properties.getProperty("TIMESTAMP"), System.currentTimeMillis())
                    .add(properties.getProperty("OFSTATS"), factory.createObjectBuilder()
                            .add(properties.getProperty("BYTE_COUNT"), flowStat.getByteCount().getValue()).add(
                                    properties.getProperty("DURATION_SEC"),
                                    flowStat.getDuration().getSecond().getValue())
                            .add(properties.getProperty("DURATION_NANOSEC"),
                                    flowStat.getDuration().getNanosecond().getValue())
                            .add(properties.getProperty("PACK_COUNT"), flowStat.getPacketCount().getValue()))
                    .build();
        }
        return flowStatisticsJsonObject;
    }

}
