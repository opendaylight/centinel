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

import org.opendaylight.yang.gen.v1.urn.opendaylight.group.statistics.rev131111.NodeGroupStatistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.statistics.rev131111.group.statistics.GroupStatistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.types.rev131018.group.statistics.buckets.BucketCounter;
import org.opendaylight.yangtools.yang.binding.DataObject;

public class CentinelOpenFlowNodeGroupStatisticsExtractor extends CentinelOpenFlowAbstractStatsExtractor {

    JsonBuilderFactory factory;

    @Override
    public void processStats(DataObject dataObject, JsonBuilderFactory factory, Properties properties) {

        this.factory = factory;
        this.properties = properties;

        NodeGroupStatistics ofNodeGroupStatData = (NodeGroupStatistics) dataObject;
        GroupStatistics ofNodeGroupStat = ofNodeGroupStatData.getGroupStatistics();
        if (ofNodeGroupStat == null) {
            return;
        }
        sendToNewPersistenceService(ofNodeGroupStat, objectToJsonMapper(ofNodeGroupStat));
    }

    @Override
    public JsonObject objectToJsonMapper(DataObject ofStat) {

        JsonObject nodeGroupStatisticsJsonObject = null;
        GroupStatistics groupStat = (GroupStatistics) ofStat;

        if (groupStat.getByteCount() != null && groupStat.getDuration() != null && groupStat.getPacketCount() != null
                && groupStat.getBuckets() != null && groupStat.getGroupId() != null
                && groupStat.getRefCount() != null) {
            nodeGroupStatisticsJsonObject = factory.createObjectBuilder()
                    .add(properties.getProperty("STAT_TYPE"), properties.getProperty("GRP_STAT"))
                    .add(properties.getProperty("TIMESTAMP"), System.currentTimeMillis())
                    .add(properties.getProperty("OFSTATS"), factory.createObjectBuilder()
                            .add(properties.getProperty("BYTE_COUNT"), groupStat.getByteCount().getValue())
                            .add(properties.getProperty("DURATION_SEC"), groupStat.getDuration().getSecond().getValue())
                            .add(properties.getProperty("DURATION_NANOSEC"),
                                    groupStat.getDuration().getNanosecond().getValue())
                            .add(properties.getProperty("PACK_COUNT"), groupStat.getPacketCount().getValue())
                            .add(properties.getProperty("REF_COUNT"), groupStat.getRefCount().getValue())
                            .add(properties.getProperty("GRP_ID"), groupStat.getGroupId().getValue())
                            .add(properties.getProperty("BUCK"),
                                    createBandStatsJsonArrayObject(groupStat.getBuckets().getBucketCounter())))
                    .build();
        }
        return nodeGroupStatisticsJsonObject;
    }

    private JsonArrayBuilder createBandStatsJsonArrayObject(List<BucketCounter> bucketCounters) {
        JsonArrayBuilder jsonArrayObject = factory.createArrayBuilder();
        for (BucketCounter bucketCounter : bucketCounters) {
            jsonArrayObject.add(factory.createObjectBuilder()
                    .add(properties.getProperty("BCK_ID"), bucketCounter.getBucketId().getValue())
                    .add(properties.getProperty("BYTE_COUNT"), bucketCounter.getByteCount().getValue())
                    .add(properties.getProperty("PACK_COUNT"), bucketCounter.getPacketCount().getValue())
                    .add(properties.getProperty("BCK_KEY"), bucketCounter.getKey().getBucketId().getValue()));
        }
        return jsonArrayObject;
    }

}
