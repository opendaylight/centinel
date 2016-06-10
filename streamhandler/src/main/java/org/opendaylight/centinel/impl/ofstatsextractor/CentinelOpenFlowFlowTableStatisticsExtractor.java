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

import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.table.statistics.rev131215.FlowTableStatisticsData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.table.statistics.rev131215.flow.table.statistics.FlowTableStatistics;
import org.opendaylight.yangtools.yang.binding.DataObject;

public class CentinelOpenFlowFlowTableStatisticsExtractor extends CentinelOpenFlowAbstractStatsExtractor {

    JsonBuilderFactory factory;

    @Override
    public void processStats(DataObject dataObject, JsonBuilderFactory factory, Properties properties) {

        this.factory = factory;
        this.properties = properties;

        FlowTableStatisticsData ofTableStatsData = (FlowTableStatisticsData) dataObject;
        FlowTableStatistics ofTableStats = ofTableStatsData.getFlowTableStatistics();
        if (ofTableStats == null) {
            return;
        }
        sendToNewPersistenceService(ofTableStats, objectToJsonMapper(ofTableStats));
    }

    @Override
    public JsonObject objectToJsonMapper(DataObject ofTableStat) {

        JsonObject flowTableJsonObject = null;
        FlowTableStatistics flowTableStats = (FlowTableStatistics) ofTableStat;

        if (flowTableStats.getActiveFlows() != null && flowTableStats.getPacketsLookedUp() != null
                && flowTableStats.getPacketsMatched() != null) {
            flowTableJsonObject = factory.createObjectBuilder()
                    .add(properties.getProperty("STAT_TYPE"), properties.getProperty("FLOW_TAB"))
                    .add(properties.getProperty("TIMESTAMP"), System.currentTimeMillis())
                    .add(properties.getProperty("OFSTATS"), factory.createObjectBuilder()
                            .add(properties.getProperty("ACT_FLOWS"), flowTableStats.getActiveFlows().getValue()).add(
                                    properties.getProperty("PACK_LOOK_UP"),
                                    flowTableStats.getPacketsLookedUp().getValue())
                            .add(properties.getProperty("PACK_MATCH"), flowTableStats.getPacketsMatched().getValue()))
                    .build();
        }
        return flowTableJsonObject;
    }

}
