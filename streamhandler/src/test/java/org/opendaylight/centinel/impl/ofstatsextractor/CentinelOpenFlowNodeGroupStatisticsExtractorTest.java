package org.opendaylight.centinel.impl.ofstatsextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.junit.Before;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.Counter32;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.Counter64;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.statistics.rev131111.NodeGroupStatisticsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.statistics.rev131111.group.statistics.GroupStatisticsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.types.rev131018.BucketId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.types.rev131018.GroupId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.types.rev131018.group.statistics.Buckets;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.types.rev131018.group.statistics.BucketsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.types.rev131018.group.statistics.DurationBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.types.rev131018.group.statistics.buckets.BucketCounter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.types.rev131018.group.statistics.buckets.BucketCounterBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.types.rev131018.group.statistics.buckets.BucketCounterKey;
import org.opendaylight.yangtools.yang.binding.DataObject;

public class CentinelOpenFlowNodeGroupStatisticsExtractorTest {

    DataObject dataObj;
    JsonBuilderFactory factory;
    Properties properties;
    CentinelOpenFlowNodeGroupStatisticsExtractor statExtractor;
    JsonObject jsonObject = null;

    @Before
    public void beforeTest() {
        dataObj = buildGroupStatistics();
        loadPropertiesFile();
        factory = Json.createBuilderFactory(null);
        statExtractor = new CentinelOpenFlowNodeGroupStatisticsExtractor();
        statExtractor.factory = factory;
        statExtractor.properties = properties;

    }

    private void loadPropertiesFile() {
        properties = new Properties();
        try {
            InputStream inputStreamForData = getClass().getClassLoader().getResourceAsStream("data.properties");
            properties.load(inputStreamForData);
        } catch (Exception e) {

        }
    }

    private org.opendaylight.yang.gen.v1.urn.opendaylight.group.statistics.rev131111.group.statistics.GroupStatistics buildGroupStatistics() {
        GroupStatisticsBuilder b = new GroupStatisticsBuilder();
        b.setByteCount(new Counter64(new BigInteger("1")));
        b.setPacketCount(new Counter64(new BigInteger("2")));
        b.setRefCount(new Counter32(1l));
        b.setDuration(new DurationBuilder().setNanosecond(new Counter32(new Long("2")))
                .setSecond(new Counter32(new Long("2"))).build());
        b.setGroupId(new GroupId(1l));
        b.setBuckets(createBuckets());
        return b.build();
    }

    private Buckets createBuckets() {
        BucketsBuilder builder = new BucketsBuilder();
        builder.setBucketCounter(createBucketsList());
        return builder.build();
    }

    private List<BucketCounter> createBucketsList() {
        List<BucketCounter> bucketsList = new ArrayList<BucketCounter>();

        BucketCounterBuilder bucketCounter = new BucketCounterBuilder();
        bucketCounter.setBucketId(new BucketId(1l));
        bucketCounter.setByteCount(new Counter64(new BigInteger("2564564")));
        bucketCounter.setKey(new BucketCounterKey(new BucketId(1l)));
        bucketCounter.setPacketCount(new Counter64(new BigInteger("25456")));
        bucketsList.add(bucketCounter.build());
        return bucketsList;

    }

    @Test
    public void objectToJsonMapperForNullValuesTest() {
        NodeGroupStatisticsBuilder nodeGroupStatBuilder = new NodeGroupStatisticsBuilder();
        GroupStatisticsBuilder groupStatBuilder = new GroupStatisticsBuilder();
        groupStatBuilder.setByteCount(new Counter64(new BigInteger("1")));
        groupStatBuilder.setRefCount(new Counter32(1l));
        groupStatBuilder.setPacketCount(new Counter64(new BigInteger("2")));
        groupStatBuilder.setDuration(new DurationBuilder().setNanosecond(new Counter32(new Long("2")))
                .setSecond(new Counter32(new Long("2"))).build());
        groupStatBuilder.setGroupId(new GroupId(1l));
        groupStatBuilder.build();
        nodeGroupStatBuilder.setGroupStatistics(groupStatBuilder.build());
        jsonObject = statExtractor.objectToJsonMapper(groupStatBuilder.build());
        assertEquals(null, jsonObject);

    }

    @Test
    public void objectToJsonMapperTestByteCount() {
        statExtractor.factory = factory;
        statExtractor.properties = properties;
        JsonObject jsonObj;
        jsonObj = statExtractor.objectToJsonMapper(dataObj);
        assertTrue((jsonObj.getJsonObject("stats").get("byte_Count")).toString().equals("1"));

    }

    @Test
    public void objectToJsonMapperTestRefCount() {
        statExtractor.factory = factory;
        statExtractor.properties = properties;
        JsonObject jsonObj;
        jsonObj = statExtractor.objectToJsonMapper(dataObj);
        assertTrue((jsonObj.getJsonObject("stats").get("ref_count")).toString().equals("1"));

    }

    @Test
    public void objectToJsonMapperTestDuration() {
        statExtractor.factory = factory;
        statExtractor.properties = properties;
        JsonObject jsonObj;
        jsonObj = statExtractor.objectToJsonMapper(dataObj);
        assertTrue((jsonObj.getJsonObject("stats").get("duration_second")).toString().equals("2"));
        assertTrue((jsonObj.getJsonObject("stats").get("duration_nanosecond")).toString().equals("2"));

    }

}
