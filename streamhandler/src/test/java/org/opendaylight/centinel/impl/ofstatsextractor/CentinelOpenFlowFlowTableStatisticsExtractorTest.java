/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.ofstatsextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.junit.Before;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.Counter32;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.Counter64;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.table.statistics.rev131215.FlowTableStatisticsData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.table.statistics.rev131215.FlowTableStatisticsDataBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.table.statistics.rev131215.flow.table.statistics.FlowTableStatisticsBuilder;
import org.opendaylight.yangtools.yang.binding.DataObject;

public class CentinelOpenFlowFlowTableStatisticsExtractorTest {

    DataObject dataObj;
    JsonBuilderFactory factory;
    Properties properties;
    CentinelOpenFlowFlowTableStatisticsExtractor statExecutor;
    JsonObject jsonObject = null;

    @Before
    public void beforeTest() {
        dataObj = buildFlowTableStatistics();
        loadPropertiesFile();
        factory = Json.createBuilderFactory(null);
        statExecutor = new CentinelOpenFlowFlowTableStatisticsExtractor();
        statExecutor.factory = factory;
        statExecutor.properties = properties;

    }

    private void loadPropertiesFile() {
        properties = new Properties();
        try {
            InputStream inputStreamForData = getClass().getClassLoader().getResourceAsStream("data.properties");
            properties.load(inputStreamForData);
        } catch (Exception e) {

        }
    }

    private org.opendaylight.yang.gen.v1.urn.opendaylight.flow.table.statistics.rev131215.flow.table.statistics.FlowTableStatistics buildFlowTableStatistics() {
        FlowTableStatisticsBuilder b = new FlowTableStatisticsBuilder();
        b.setActiveFlows(new Counter32(1l));
        b.setPacketsLookedUp(new Counter64(new BigInteger("1")));
        b.setPacketsMatched(new Counter64(new BigInteger("2")));
        return b.build();
    }

    private FlowTableStatisticsData buildFlowTableStatisticsData() {
        FlowTableStatisticsDataBuilder b = new FlowTableStatisticsDataBuilder();
        b.setFlowTableStatistics(buildFlowTableStatistics());
        return b.build();
    }

    @Test
    public void objectToJsonMapperTestPacketLookedUp() {
        statExecutor.factory = factory;
        statExecutor.properties = properties;
        JsonObject jsonObj;
        jsonObj = statExecutor.objectToJsonMapper(dataObj);
        assertTrue((jsonObj.getJsonObject("stats").get("packet_lookedUp")).toString().equals("1"));

    }

    @Test
    public void objectToJsonMapperTestPacketMatched() {
        statExecutor.processStats(buildFlowTableStatisticsData(), factory, properties);
        JsonObject jsonObj;
        jsonObj = statExecutor.objectToJsonMapper(dataObj);
        assertTrue((jsonObj.getJsonObject("stats").get("packet_matched")).toString().equals("2"));

    }

    @Test
    public void objectToJsonMapperTestActiveFlows() {
        statExecutor.processStats(buildFlowTableStatisticsData(), factory, properties);
        JsonObject jsonObj;
        jsonObj = statExecutor.objectToJsonMapper(dataObj);
        assertTrue((jsonObj.getJsonObject("stats").get("active_flows")).toString().equals("1"));

    }

    @Test
    public void objectToJsonMapperNullValue() {
        FlowTableStatisticsBuilder tableStatBuilder = new FlowTableStatisticsBuilder();
        tableStatBuilder.setActiveFlows(new Counter32(1l));
        tableStatBuilder.setPacketsLookedUp(new Counter64(new BigInteger("1")));
        tableStatBuilder.setPacketsMatched(null);
        tableStatBuilder.build();
        jsonObject = statExecutor.objectToJsonMapper(tableStatBuilder.build());
        assertEquals(null, jsonObject);
    }

}
