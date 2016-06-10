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

import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.statistics.rev130819.flow.statistics.FlowStatistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.statistics.rev131111.nodes.node.meter.MeterStatistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.statistics.rev131111.group.statistics.GroupStatistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.table.statistics.rev131215.flow.table.statistics.FlowTableStatistics;

public abstract class CentinelOpenFlowAbstractStatsExtractor {
    
    Properties properties;

    private static final Logger LOG = LoggerFactory.getLogger(CentinelOpenFlowAbstractStatsExtractor.class);

    /**
     * @param dataObject
     * @param factory
     * @param properties
     */
    public abstract void processStats(DataObject dataObject, JsonBuilderFactory factory, Properties properties);

    /**
     * @param dataObject
     * @return JsonObject
     */
    public abstract JsonObject objectToJsonMapper(DataObject dataObject);

    /**
     * @param objectToJsonMapper
     */
    public void sendToNewPersistenceService(DataObject dataObject, JsonObject objectToJsonMapper) {

        if (objectToJsonMapper != null) {
            if (dataObject instanceof FlowStatistics) {
                LOG.info("JSON for FlowStatistics    " + objectToJsonMapper.toString());
            }

            if (dataObject instanceof MeterStatistics) {
                LOG.info("JSON for MeterStatistics    " + objectToJsonMapper.toString());
            }

            if (dataObject instanceof GroupStatistics) {
                LOG.info("JSON for GroupStatistics    " + objectToJsonMapper.toString());
            }

            if (dataObject instanceof FlowTableStatistics) {
                LOG.info("JSON for FlowTableStatistics    " + objectToJsonMapper.toString());
            }

        }

    }

}
