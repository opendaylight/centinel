package org.opendaylight.centinel.impl.ofstatsextractor;

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
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.statistics.rev130819.FlowStatisticsDataBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.statistics.rev130819.flow.statistics.FlowStatisticsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.statistics.meter.band.stats.BandStat;
//import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.statistics.DurationBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.statistics.types.rev130925.duration.DurationBuilder;
import org.opendaylight.yangtools.yang.binding.DataObject;

public class CentinelOpenFlowAbstractStatsExtractorTest {

    JsonBuilderFactory factory;
    DataObject dataObj;
    Properties properties;
    JsonObject jsonObject;
    JsonObject jsonObject1;
    JsonArrayBuilder jsonArrayObject;
    CentinelOpenFlowNodeMeterStatisticsExtractorTest centinelOpenFlowNodeStatExecutorTest;
    CentinelOpenFlowNodeMeterStatisticsExtractor centinelOpenFlowNodeStatExecutor;
    CentinelOpenFlowAbstractStatsExtractor CentinelOpenFlowAbstractStatsExtractor;
    CentinelOpenFlowFlowStatisticsExtractor CentinelOpenFlowFlowStatisticsExtractor;
    java.util.List<BandStat> bandsList = new ArrayList<BandStat>();

    @Before
    public void beforeTest() {

        loadPropertiesFile();
        factory = Json.createBuilderFactory(null);
        centinelOpenFlowNodeStatExecutorTest = new CentinelOpenFlowNodeMeterStatisticsExtractorTest();
        centinelOpenFlowNodeStatExecutor = new CentinelOpenFlowNodeMeterStatisticsExtractor();
        CentinelOpenFlowFlowStatisticsExtractor = new CentinelOpenFlowFlowStatisticsExtractor();
        centinelOpenFlowNodeStatExecutor.factory = factory;
        centinelOpenFlowNodeStatExecutor.properties = properties;
        dataObj = centinelOpenFlowNodeStatExecutorTest.buildNodeMeterStatistics();
        CentinelOpenFlowFlowStatisticsExtractor.factory = factory;
        CentinelOpenFlowFlowStatisticsExtractor.properties = properties;

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

    private DataObject buildFlowStatisticsData() {
        FlowStatisticsDataBuilder b = new FlowStatisticsDataBuilder();
        b.setFlowStatistics(buildFlowStatistics());
        return b.build();
    }

    private org.opendaylight.yang.gen.v1.urn.opendaylight.flow.statistics.rev130819.flow.statistics.FlowStatistics buildFlowStatistics() {
        FlowStatisticsBuilder b = new FlowStatisticsBuilder();
        b.setPacketCount(new Counter64(new BigInteger("1")));
        b.setByteCount(new Counter64(new BigInteger("2")));
        b.setDuration(new DurationBuilder().setNanosecond(new Counter32(new Long("2")))
                .setSecond(new Counter32(new Long("2"))).build());
        return b.build();
    }

    @Test
    public void sendToNewPersistenceServiceForMeterStatistics() {
        JsonObject jsonObj = null;

        jsonObject = centinelOpenFlowNodeStatExecutor
                .objectToJsonMapper(centinelOpenFlowNodeStatExecutorTest.buildMeterStatistics());
        centinelOpenFlowNodeStatExecutor.sendToNewPersistenceService(
                centinelOpenFlowNodeStatExecutorTest.buildNodeMeterStatistics(), jsonObject);
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

    @Test
    public void sendToNewPersistenceServiceForFlowStatistics() {

        jsonObject = CentinelOpenFlowFlowStatisticsExtractor.objectToJsonMapper(buildFlowStatistics());
        CentinelOpenFlowFlowStatisticsExtractor.sendToNewPersistenceService(buildFlowStatisticsData(), jsonObject);
        assertTrue((jsonObject.getJsonObject("stats").get("packet_count")).toString().equals("1"));
        assertTrue((jsonObject.getJsonObject("stats").get("byte_Count")).toString().equals("2"));
        assertTrue((jsonObject.getJsonObject("stats").get("duration_second")).toString().equals("2"));
        assertTrue((jsonObject.getJsonObject("stats").get("duration_nanosecond")).toString().equals("2"));
    }

}
