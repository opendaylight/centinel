/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.laas.rest.utilities;

import java.io.Closeable;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import kafka.admin.AdminUtils;
import kafka.common.TopicAndPartition;
import kafka.common.TopicExistsException;
import kafka.utils.ZKGroupTopicDirs;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @author Sumit kapoor This class will provide access point to the common
 *         services for handling KAFKA cluster
 */
public class KafkaService {

    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);
    private static Properties properties;

    private static final int ZOO_TIMEOUT_MSEC = (int) TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS);
    public static KafkaService singleton = null;
    private String zkServers;
    private String topics;
    private int partitions;
    private int repetitions;

    private KafkaService() {
        super();
    }

    /**
     * Provide single instance of KafkaService across the application throughout
     * its life cycle.
     */
    public static synchronized KafkaService getInstance() {

        if (singleton == null) {
            singleton = new KafkaService();
        }
        return singleton;

    }

    /**
     * This creates topics for collectors in centinel i.e. syslog, openflow,
     * ipfix andd rules. Number of kafka partitions and replication are
     * configured in data property file.
     */
    public void createTopic() {
        loadPropertiesFiles();
        loadPropertiesValues();
        String[] split = topics.split(":");
        for (String topic : split) {
            createTopic(zkServers, topic, partitions, repetitions);
        }
    }

    /**
     * @param zookeeperHosts
     *            Zookeeper hosts e.g. localhost:2181. If multiple zookeeper
     *            then host1:port1[,host2:port2,...]
     * @param topic
     *            topic to create (if not already existing)
     * @param partitions
     *            number of topic partitions
     * @param repetitions
     *            number of replications
     */

    public void createTopic(String zookeeperHosts, String topic, int partitions, int repetitions) {
        createTopic(zookeeperHosts, topic, partitions, repetitions, new Properties());
    }

    /**
     * @param zookeeperHosts
     *            Zookeeper hosts e.g. localhost:2181. If multiple zookeeper
     *            then host1:port1[,host2:port2,...]
     * @param topic
     *            topic to create (if not already existing)
     * @param partitions
     *            number of topic partitions
     * @param repetitions
     *            number of replications
     * @param topicProperties
     *            optional topic config properties
     */

    public void createTopic(String zookeeperHosts, String topic, int partitions, int repetitions,
            Properties topicProperties) {
        try (SuperZkClient zkClient = new SuperZkClient(zookeeperHosts)) {
            if (AdminUtils.topicExists(zkClient, topic)) {
                log.info("Topic {} already exists", topic);
            } else {
                log.info("Creating topic ", topic);
                try {
                    AdminUtils.createTopic(zkClient, topic, partitions, partitions, topicProperties);
                    log.info("Created Zookeeper topic {}", topic);
                } catch (TopicExistsException tee) {
                    log.info("Zookeeper topic {} already exists", topic);
                }
            }
        }
    }

    /**
     * @param zookeeperHosts
     *            Zookeeper hosts e.g. localhost:2181. If multiple zookeeper
     *            then host1:port1[,host2:port2,...]
     * @param topic
     *            topic to check for existence
     * @return true if and only if the given topic exists
     */
    public boolean topicExists(String zookeeperHosts, String topic) {
        try (SuperZkClient zkClient = new SuperZkClient(zookeeperHosts)) {
            return AdminUtils.topicExists(zkClient, topic);
        }
    }

    /**
     * This delete topics for collectors in centinel i.e. syslog, openflow,
     * ipfix andd rules. Number of kafka partitions and replication are
     * configured in data property file.
     */
    public void deleteTopic() {
        String[] split = topics.split(":");
        for (String topic : split) {
            deleteTopic(zkServers, topic);
        }
    }

    /**
     * @param zookeeperHosts
     *            Zookeeper hosts e.g. localhost:2181. If multiple zookeeper
     *            then host1:port1[,host2:port2,...]
     * @param topic
     *            topic to delete, if it exists
     */
    public void deleteTopic(String zookeeperHosts, String topic) {
        try (SuperZkClient zkClient = new SuperZkClient(zookeeperHosts)) {
            if (AdminUtils.topicExists(zkClient, topic)) {
                log.info("Deleting topic {}", topic);
                try {
                    AdminUtils.deleteTopic(zkClient, topic);
                    log.info("Deleted Zookeeper topic {}", topic);
                } catch (ZkNodeExistsException nee) {
                    log.info("Delete was already scheduled for Zookeeper topic {}", topic);
                }
            } else {
                log.info("No need to delete topic {} as it does not exist", topic);
            }
        }
    }

    /**
     * @param zookeeperHosts
     *            Zookeeper hosts e.g. localhost:2181. If multiple zookeeper
     *            then host1:port1[,host2:port2,...]
     * @param groupID
     *            consumer group to update
     * @param offsets
     *            mapping of (topic and) partition to offset to push to
     *            Zookeeper
     */
    public void createOffsets(String zookeeperHosts, String groupID, Map<TopicAndPartition, Long> offsets) {
        try (SuperZkClient zkClient = new SuperZkClient(zookeeperHosts)) {
            for (Map.Entry<TopicAndPartition, Long> entry : offsets.entrySet()) {
                TopicAndPartition topicAndPartition = entry.getKey();
                ZKGroupTopicDirs topicDirs = new ZKGroupTopicDirs(groupID, topicAndPartition.topic());
                int partition = topicAndPartition.partition();
                long offset = entry.getValue();
                String partitionOffsetPath = topicDirs.consumerOffsetDir() + "/" + partition;
                ZkUtils.updatePersistentPath(zkClient, partitionOffsetPath, Long.toString(offset));
            }
        }
    }

    /*
     * To load property file having kafka cluster configurations
     */
    private void loadPropertiesFiles() {
        properties = new Properties();
        try {
            InputStream inputStreamForData = getClass().getClassLoader().getResourceAsStream("data.properties");
            properties.load(inputStreamForData);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    /*
     * To load kafka cluster configurations
     */
    private void loadPropertiesValues() {
        zkServers = properties.getProperty("zkservers");
        topics = properties.getProperty("topics");
        partitions = Integer.parseInt(properties.getProperty("partitions"));
        repetitions = Integer.parseInt(properties.getProperty("repetitions"));
    }

    /*
     * To create closeaable class
     */
    private static final class SuperZkClient extends ZkClient implements Closeable {
        SuperZkClient(String zookeeperHosts) {
            super(zookeeperHosts, ZOO_TIMEOUT_MSEC, ZOO_TIMEOUT_MSEC, ZKStringSerializer$.MODULE$);
        }
    }

}