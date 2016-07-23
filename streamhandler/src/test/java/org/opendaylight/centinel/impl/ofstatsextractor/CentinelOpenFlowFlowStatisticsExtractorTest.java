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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.Counter32;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.Counter64;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.statistics.rev130819.FlowStatisticsDataBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.statistics.rev130819.flow.statistics.FlowStatisticsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.statistics.types.rev130925.duration.DurationBuilder;

public class CentinelOpenFlowFlowStatisticsExtractorTest {

    JsonBuilderFactory factory;
    Properties properties;
    CentinelOpenFlowFlowStatisticsExtractor centinelOpenFlowStatExecutor;
    JsonObject jsonObject;

    @Before
    public void beforeTest() {
        loadPropertiesFile();
        factory = Json.createBuilderFactory(null);
        centinelOpenFlowStatExecutor = new CentinelOpenFlowFlowStatisticsExtractor();
        centinelOpenFlowStatExecutor.factory = factory;
        centinelOpenFlowStatExecutor.properties = properties;

    }

    @After
    public void afterTest() {
        factory = null;
    }

    private void loadPropertiesFile() {
        properties = new Properties();
        try {
            InputStream inputStreamForData = getClass().getClassLoader().getResourceAsStream("data.properties");
            properties.load(inputStreamForData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void objectToJsonMapperForPacketCountTest() {
        JsonObject jsonObj;
        FlowStatisticsDataBuilder flowStatDataBuilder = new FlowStatisticsDataBuilder();
        FlowStatisticsBuilder flowStatBuilder = new FlowStatisticsBuilder();
        flowStatBuilder.setPacketCount(new Counter64(new BigInteger("1")));
        flowStatBuilder.setByteCount(new Counter64(new BigInteger("2")));
        flowStatBuilder.setDuration(new DurationBuilder().setNanosecond(new Counter32(new Long("2")))
                .setSecond(new Counter32(new Long("2"))).build());
        flowStatBuilder.build();
        flowStatDataBuilder.setFlowStatistics(flowStatBuilder.build());
        jsonObj = centinelOpenFlowStatExecutor.objectToJsonMapper(flowStatBuilder.build());
        assertTrue((jsonObj.getJsonObject("stats").get("packet_count")).toString().equals("1"));
    }

    @Test
    public void objectToJsonMapperForByteCountTest() {
        JsonObject jsonObj;
        FlowStatisticsDataBuilder flowStatDataBuilder = new FlowStatisticsDataBuilder();
        FlowStatisticsBuilder flowStatBuilder = new FlowStatisticsBuilder();
        flowStatBuilder.setPacketCount(new Counter64(new BigInteger("1")));
        flowStatBuilder.setByteCount(new Counter64(new BigInteger("2")));
        flowStatBuilder.setDuration(new DurationBuilder().setNanosecond(new Counter32(new Long("2")))
                .setSecond(new Counter32(new Long("2"))).build());
        flowStatBuilder.build();
        flowStatDataBuilder.setFlowStatistics(flowStatBuilder.build());
        jsonObj = centinelOpenFlowStatExecutor.objectToJsonMapper(flowStatBuilder.build());
        assertTrue((jsonObj.getJsonObject("stats").get("byte_Count")).toString().equals("2"));
    }

    @Test
    public void objectToJsonMapperForDurationSecondTest() {
        JsonObject jsonObj;
        FlowStatisticsDataBuilder flowStatDataBuilder = new FlowStatisticsDataBuilder();
        FlowStatisticsBuilder flowStatBuilder = new FlowStatisticsBuilder();
        flowStatBuilder.setPacketCount(new Counter64(new BigInteger("1")));
        flowStatBuilder.setByteCount(new Counter64(new BigInteger("2")));
        flowStatBuilder.setDuration(new DurationBuilder().setNanosecond(new Counter32(new Long("2")))
                .setSecond(new Counter32(new Long("2"))).build());
        flowStatBuilder.build();
        flowStatDataBuilder.setFlowStatistics(flowStatBuilder.build());
        jsonObj = centinelOpenFlowStatExecutor.objectToJsonMapper(flowStatBuilder.build());
        assertTrue((jsonObj.getJsonObject("stats").get("duration_second")).toString().equals("2"));
    }

    @Test
    public void objectToJsonMapperForDurationNanoSecondTest() {
        JsonObject jsonObj;
        FlowStatisticsDataBuilder flowStatDataBuilder = new FlowStatisticsDataBuilder();
        FlowStatisticsBuilder flowStatBuilder = new FlowStatisticsBuilder();
        flowStatBuilder.setPacketCount(new Counter64(new BigInteger("1")));
        flowStatBuilder.setByteCount(new Counter64(new BigInteger("2")));
        flowStatBuilder.setDuration(new DurationBuilder().setNanosecond(new Counter32(new Long("2")))
                .setSecond(new Counter32(new Long("2"))).build());
        flowStatBuilder.build();
        flowStatDataBuilder.setFlowStatistics(flowStatBuilder.build());
        jsonObj = centinelOpenFlowStatExecutor.objectToJsonMapper(flowStatBuilder.build());
        assertTrue((jsonObj.getJsonObject("stats").get("duration_nanosecond")).toString().equals("2"));
    }

    @Test
    public void objectToJsonMapperForNullValuesTest() {
        JsonObject jsonObj;
        FlowStatisticsDataBuilder flowStatDataBuilder = new FlowStatisticsDataBuilder();
        FlowStatisticsBuilder flowStatBuilder = new FlowStatisticsBuilder();
        flowStatBuilder.setPacketCount(new Counter64(new BigInteger("1")));
        flowStatBuilder.setByteCount(new Counter64(new BigInteger("2")));
        flowStatBuilder.build();
        flowStatDataBuilder.setFlowStatistics(flowStatBuilder.build());
        jsonObj = centinelOpenFlowStatExecutor.objectToJsonMapper(flowStatBuilder.build());
        assertEquals(null, jsonObj);
    }
}
