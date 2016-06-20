/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.impl.ofstatsextractor;

import java.util.List;
import java.util.Properties;

import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.statistics.rev131111.NodeMeterStatistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.statistics.rev131111.nodes.node.meter.MeterStatistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.statistics.meter.band.stats.BandStat;
import org.opendaylight.yangtools.yang.binding.DataObject;

public class CentinelOpenFlowNodeMeterStatisticsExtractor extends CentinelOpenFlowAbstractStatsExtractor {

    JsonBuilderFactory factory;

    @Override
    public void processStats(DataObject dataObject, JsonBuilderFactory factory, Properties properties) {

        this.factory = factory;
        this.properties = properties;

        NodeMeterStatistics nodeMeterStats = (NodeMeterStatistics) dataObject;
        MeterStatistics meterStats = nodeMeterStats.getMeterStatistics();
        if (meterStats == null) {
            return;
        }
        sendToNewPersistenceService(meterStats, objectToJsonMapper(meterStats));
    }

    @Override
    public JsonObject objectToJsonMapper(DataObject meterStats) {

        JsonObject flowStatisticsJsonObject = null;
        MeterStatistics meterStat = (MeterStatistics) meterStats;

        if (meterStat.getByteInCount() != null && meterStat.getDuration() != null && meterStat.getFlowCount() != null
                && meterStat.getMeterBandStats() != null && meterStat.getMeterId() != null
                && meterStat.getPacketInCount() != null) {
            List<BandStat> bandStats = meterStat.getMeterBandStats().getBandStat();

            flowStatisticsJsonObject = factory.createObjectBuilder()
                    .add(properties.getProperty("STAT_TYPE"), properties.getProperty("MTR_STAT"))
                    .add(properties.getProperty("TIMESTAMP"), System.currentTimeMillis())
                    .add(properties.getProperty("OFSTATS"), factory.createObjectBuilder()
                            .add(properties.getProperty("MET_ID"), meterStat.getMeterId().getValue())
                            .add(properties.getProperty("BYT_IN_COUNT"), meterStat.getByteInCount().getValue())
                            .add(properties.getProperty("FLOW_COUNT"), meterStat.getFlowCount().getValue())
                            .add(properties.getProperty("PACK_IN_COUNT"), meterStat.getPacketInCount().getValue())
                            .add(properties.getProperty("DURATION_SEC"), meterStat.getDuration().getSecond().getValue())
                            .add(properties.getProperty("DURATION_NANOSEC"),
                                    meterStat.getDuration().getNanosecond().getValue())
                            .add(properties.getProperty("BAND_STAT"), createBandStatsJsonArrayObject(bandStats)))
                    .build();
        }
        return flowStatisticsJsonObject;
    }

    private JsonArrayBuilder createBandStatsJsonArrayObject(List<BandStat> bandStats) {
        JsonArrayBuilder jsonArrayObject = factory.createArrayBuilder();
        for (BandStat bandStat : bandStats) {
            jsonArrayObject.add(factory.createObjectBuilder()
                    .add(properties.getProperty("BAND_ID"), bandStat.getBandId().getValue())
                    .add(properties.getProperty("BYT_BAND_COUNT"), bandStat.getByteBandCount().getValue())
                    .add(properties.getProperty("KEY"), bandStat.getKey().getBandId().getValue())
                    .add(properties.getProperty("PCK_BAND_COUNT"), bandStat.getPacketBandCount().getValue()));
        }
        return jsonArrayObject;
    }

}
