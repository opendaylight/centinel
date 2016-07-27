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
import java.util.ArrayList;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.Counter32;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.Counter64;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.statistics.rev131111.NodeMeterStatistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.statistics.rev131111.NodeMeterStatisticsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.statistics.rev131111.nodes.node.meter.MeterStatistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.statistics.rev131111.nodes.node.meter.MeterStatisticsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.BandId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.MeterId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.statistics.DurationBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.statistics.MeterBandStats;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.statistics.MeterBandStatsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.statistics.meter.band.stats.BandStat;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.statistics.meter.band.stats.BandStatBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.statistics.meter.band.stats.BandStatKey;
import org.opendaylight.yangtools.yang.binding.DataObject;

public class CentinelOpenFlowNodeMeterStatisticsExtractorTest {

    JsonBuilderFactory factory;
    DataObject dataObj;
    Properties properties;
    JsonObject jsonObject;
    JsonArrayBuilder jsonArrayObject;
    CentinelOpenFlowNodeMeterStatisticsExtractor centinelOpenFlowNodeStatExecutor;
    java.util.List<BandStat> bandsList = new ArrayList<BandStat>();

    @Before
    public void beforeTest() {
        dataObj = buildMeterStatistics();
        loadPropertiesFile();
        factory = Json.createBuilderFactory(null);
        centinelOpenFlowNodeStatExecutor = new CentinelOpenFlowNodeMeterStatisticsExtractor();
        centinelOpenFlowNodeStatExecutor.factory = factory;
        centinelOpenFlowNodeStatExecutor.properties = properties;

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

        }

    }

    public NodeMeterStatistics buildNodeMeterStatistics() {
        NodeMeterStatisticsBuilder b = new NodeMeterStatisticsBuilder();
        b.setMeterStatistics(buildMeterStatistics());
        return b.build();
    }

    public MeterStatistics buildMeterStatistics() {
        MeterStatisticsBuilder b = new MeterStatisticsBuilder();
        b.setByteInCount(new Counter64(new BigInteger("1")));
        b.setFlowCount(new Counter32(1l));
        b.setPacketInCount(new Counter64(new BigInteger("2")));
        b.setDuration(new DurationBuilder().setNanosecond(new Counter32(new Long("2")))
                .setSecond(new Counter32(new Long("2"))).build());
        b.setMeterId(new MeterId(1l));
        b.setMeterBandStats(createMeterBandStats());
        return b.build();
    }

    private MeterBandStats createMeterBandStats() {
        MeterBandStatsBuilder builder = new MeterBandStatsBuilder();
        builder.setBandStat(createBandsList());
        return builder.build();
    }

    private java.util.List<BandStat> createBandsList() {
        java.util.List<BandStat> bandsList = new ArrayList<BandStat>();

        BandStatBuilder bandStatBuilder = new BandStatBuilder();
        bandStatBuilder.setBandId(new BandId(1L));
        bandStatBuilder.setByteBandCount(new Counter64(new BigInteger("2564564")));
        bandStatBuilder.setKey(new BandStatKey(new BandId(1L)));
        bandStatBuilder.setPacketBandCount(new Counter64(new BigInteger("25456")));
        bandsList.add(bandStatBuilder.build());

        return bandsList;
    }

    @Test
    public void objectToJsonMapperForNullValuesTest() {
        NodeMeterStatisticsBuilder nodeMeterStatBuilder = new NodeMeterStatisticsBuilder();
        MeterStatisticsBuilder meterStatBuilder = new MeterStatisticsBuilder();
        meterStatBuilder.setByteInCount(new Counter64(new BigInteger("1")));
        meterStatBuilder.setFlowCount(new Counter32(1l));
        meterStatBuilder.setPacketInCount(new Counter64(new BigInteger("2")));
        meterStatBuilder.setDuration(new DurationBuilder().setNanosecond(new Counter32(new Long("2")))
                .setSecond(new Counter32(new Long("2"))).build());
        meterStatBuilder.setMeterId(new MeterId(1l));
        meterStatBuilder.build();
        nodeMeterStatBuilder.setMeterStatistics(meterStatBuilder.build());
        jsonObject = centinelOpenFlowNodeStatExecutor.objectToJsonMapper(meterStatBuilder.build());
        assertEquals(null, jsonObject);

    }

    @Test
    public void objectToJsonMapperForDuratioNanoSecondTest() {
        JsonObject jsonObj = null;
        jsonObject = centinelOpenFlowNodeStatExecutor.objectToJsonMapper(buildMeterStatistics());
        assertTrue((jsonObject.getJsonObject("stats").get("bytein_count")).toString().equals("1"));
        assertTrue((jsonObject.getJsonObject("stats").get("flow_count")).toString().equals("1"));
        assertTrue((jsonObject.getJsonObject("stats").get("packetin_count")).toString().equals("2"));
        assertTrue((jsonObject.getJsonObject("stats").get("duration_second")).toString().equals("2"));
        assertTrue((jsonObject.getJsonObject("stats").get("duration_nanosecond")).toString().equals("2"));
        assertTrue((jsonObject.getJsonObject("stats").get("meter_id")).toString().equals("1"));
        JsonValue bandStats = jsonObject.getJsonObject("stats").getJsonArray("bandStats").get(0);

        if ((bandStats.getValueType() == JsonValue.ValueType.OBJECT) && (bandStats instanceof JsonObject)) {
            jsonObj = (JsonObject) bandStats;
            assertTrue(jsonObj.get("band_id").toString().equals("1"));
            assertTrue(jsonObj.get("byteband_count").toString().equals("2564564"));
            assertTrue(jsonObj.get("key").toString().equals("1"));
            assertTrue(jsonObj.get("packetband_count").toString().equals("25456"));

        }
    }
}
