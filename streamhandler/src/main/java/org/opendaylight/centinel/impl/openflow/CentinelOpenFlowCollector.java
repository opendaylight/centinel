/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.impl.openflow;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.json.Json;
import javax.json.JsonBuilderFactory;

import org.opendaylight.centinel.impl.ofstatsextractor.CentinelOpenFlowAbstractStatsExtractor;
import org.opendaylight.centinel.impl.ofstatsextractor.CentinelOpenFlowFlowStatisticsExtractor;
import org.opendaylight.centinel.impl.ofstatsextractor.CentinelOpenFlowFlowTableStatisticsExtractor;
import org.opendaylight.centinel.impl.ofstatsextractor.CentinelOpenFlowNodeGroupStatisticsExtractor;
import org.opendaylight.centinel.impl.ofstatsextractor.CentinelOpenFlowNodeMeterStatisticsExtractor;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.meters.Meter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.statistics.rev130819.FlowStatisticsData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.table.statistics.rev131215.FlowTableStatisticsData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.statistics.rev131111.NodeGroupStatistics;
import org.opendaylight.yang.gen.v1.urn.opendaylight.group.types.rev131018.groups.Group;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.statistics.rev131111.NodeMeterStatistics;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Abhishek Sharma
 * 
 *         CentinelOpenFlowCollector is responsible for collecting OpenFlow
 *         specific statistics from data store's Nodes and it is dependent on
 *         l2switch feature for the collection.
 *
 */
public class CentinelOpenFlowCollector implements AutoCloseable, DataTreeChangeListener<Node> {
    private DataBroker dataBroker = null;

    public static final InstanceIdentifier<Nodes> NODERECORDID = InstanceIdentifier.builder(Nodes.class).build();
    private static final Logger logger = LoggerFactory.getLogger(CentinelOpenFlowCollector.class);
    private final ListenerRegistration<CentinelOpenFlowCollector> centinelOpenFlowCollectorReg;
    private Map<Class<? extends DataObject>, CentinelOpenFlowAbstractStatsExtractor> extracter = new ConcurrentHashMap<>();
    JsonBuilderFactory factory = null;
    private final ExecutorService executor;
    Properties properties;

    /**
     * @param dataBroker
     *            Constructor to register the listener and specifying the
     *            identifying for data store operations
     */
    public CentinelOpenFlowCollector(DataBroker dataBroker) {
        executor = Executors.newFixedThreadPool(1);
        this.dataBroker = dataBroker;
        factory = Json.createBuilderFactory(null);
        loadPropertiesFile();

        final InstanceIdentifier<Node> nodePath = getNodePath();
        final DataTreeIdentifier<Node> dataTreeIid = new DataTreeIdentifier<>(LogicalDatastoreType.OPERATIONAL,
                nodePath);
        centinelOpenFlowCollectorReg = dataBroker.registerDataTreeChangeListener(dataTreeIid, this);
        logger.info("CentinelOpenFlowCollectorListener created and registered");

        // Load the available extractor classes into a map. Also add here any
        // future OFStat types
        loadExtractorMap();

    }

    /**
     * @return InstanceIdentifier
     */
    private InstanceIdentifier<Node> getNodePath() {
        final InstanceIdentifier<Node> nodePath = InstanceIdentifier.create(Nodes.class).child(Node.class);
        return nodePath;
    }

    /**
     * @return DataBroker
     */
    public DataBroker getDataBroker() {
        return this.dataBroker;
    }

    @Override
    public void onDataTreeChanged(final Collection<DataTreeModification<Node>> changes) {
        logger.info("onDataTreeChanged called");
        for (final DataTreeModification<Node> change : changes) {
            final DataObjectModification<Node> changeDiff = change.getRootNode();
            if (changeDiff.getModificationType().equals(
                    org.opendaylight.controller.md.sal.binding.api.DataObjectModification.ModificationType.SUBTREE_MODIFIED)) {
                logger.info("There is a sub-tree modified operation performed");
                extractOpenFlowStatistics(changeDiff.getDataAfter());
            }
        }
    }

    /**
     * Load all required Executors
     * 
     */
    private void loadExtractorMap() {
        extracter.put(FlowStatisticsData.class, new CentinelOpenFlowFlowStatisticsExtractor());
        extracter.put(NodeMeterStatistics.class, new CentinelOpenFlowNodeMeterStatisticsExtractor());
        extracter.put(FlowTableStatisticsData.class, new CentinelOpenFlowFlowTableStatisticsExtractor());
        extracter.put(NodeGroupStatistics.class, new CentinelOpenFlowNodeGroupStatisticsExtractor());
    }

    /**
     * @param node
     *            extracts OF statistics from node's information
     */
    public void extractOpenFlowStatistics(Node node) {
        try {

            if (node != null) {
                FlowCapableNode flowCapableNode = node.getAugmentation(FlowCapableNode.class);
                if (flowCapableNode != null) {
                    List<Meter> meters = flowCapableNode.getMeter();
                    if (meters != null) {
                        for (Meter meter : meters) {
                            NodeMeterStatistics nodeMeterStats = meter.getAugmentation(NodeMeterStatistics.class);
                            if (nodeMeterStats != null) {
                                manageOpenFlowStatistics(nodeMeterStats, NodeMeterStatistics.class);
                            }
                        }
                    }
                    // Extracting Node Flow Statistics
                    List<Table> flowTables = flowCapableNode.getTable();
                    if (flowTables != null) {
                        for (Table table : flowTables) {
                            FlowTableStatisticsData flowTableData = table
                                    .getAugmentation(FlowTableStatisticsData.class);
                            if (flowTableData != null) {
                                manageOpenFlowStatistics(flowTableData, FlowTableStatisticsData.class);
                            }
                            // Extracting Flow Statistics
                            if (table.getFlow() != null) {
                                for (Flow flow : table.getFlow()) {
                                    FlowStatisticsData flowStatisticsData = flow
                                            .getAugmentation(FlowStatisticsData.class);
                                    if (flowStatisticsData != null) {
                                        manageOpenFlowStatistics(flowStatisticsData, FlowStatisticsData.class);
                                    }
                                }
                            }
                        }
                    }
                    // Extracting Node Group Statistics
                    List<Group> groups = flowCapableNode.getGroup();
                    if (groups != null) {
                        for (Group group : groups) {
                            NodeGroupStatistics nodeGroupStatistics = group.getAugmentation(NodeGroupStatistics.class);
                            manageOpenFlowStatistics(nodeGroupStatistics, NodeGroupStatistics.class);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * @param dataObject
     * @param cls
     */
    public void manageOpenFlowStatistics(DataObject dataObject, Class<? extends DataObject> cls) {
        if (dataObject == null) {
            return;
        }

        CentinelOpenFlowAbstractStatsExtractor extractor = extracter.get(cls);
        if (extractor == null) {
            logger.error("Error, can't find extractor for " + cls.getSimpleName());
            return;
        }
        extractor.processStats(dataObject, factory, properties);
    }

    @Override
    public void close() throws Exception {
        centinelOpenFlowCollectorReg.close();
        executor.shutdown();
        logger.info("Listener registration for CentinelOpenFlowCollector closed");
    }

    /**
     * Load property files related to openflow collector
     */
    public void loadPropertiesFile() {
        properties = new Properties();
        try {
            InputStream inputStreamForData = getClass().getClassLoader().getResourceAsStream("data.properties");
            properties.load(inputStreamForData);
            logger.info("Properties files for Openflow collector loaded successfully");
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

}
