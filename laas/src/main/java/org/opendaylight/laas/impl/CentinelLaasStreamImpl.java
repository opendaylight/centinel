/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.laas.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.NotificationService;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.laas.rest.utilities.CentinelStreamRESTServices;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamRule;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.createstreaminput.StreamRules;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.streamrecord.StreamList;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

/**
 * @author Sunaina Khanna This class specifically provide onDataChange services
 *         that will commit configuration into GrayLog server and in case of
 *         successful commit update the MDSAL operational datastore for Stream.
 */
public class CentinelLaasStreamImpl implements AutoCloseable, DataChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(CentinelLaasStreamImpl.class);

    private NotificationService notificationProvider;
    private DataBroker dataProvider;
    private final ExecutorService executor;
    private CentinelStreamRESTServices restService;

    public CentinelLaasStreamImpl() {
        executor = Executors.newFixedThreadPool(1);
    }

    public void setNotificationProvider(final NotificationService salService) {
        LOG.info(" Entered to Notification ");
        this.notificationProvider = salService;
        LOG.info("notifictaion provider set");
    }

    public void setDataProvider(final DataBroker salDataProvider) {
        LOG.info(" Entered to Data Provider of streamlaas");
        this.dataProvider = salDataProvider;
        restService = CentinelStreamRESTServices.getInstance();
        LOG.info("data provider set");
    }

    public static final InstanceIdentifier<StreamRecord> streamRecordId = InstanceIdentifier
            .builder(StreamRecord.class).build();

    @Override
    public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change) {

        LOG.info("onDataChanged called ");
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        DataObject dataObject = change.getUpdatedSubtree();
        if (dataObject instanceof StreamRecord) {

            StreamRecord record = (StreamRecord) dataObject;
            Iterator<DataObject> iterator = change.getCreatedData().values().iterator();
            if (iterator.hasNext()) {
                createIntoOperational(tx, record, iterator);
            }

            else if (!change.getRemovedPaths().isEmpty()) {
                removeFromOperational(change, tx);
            }

            else if (change.getUpdatedSubtree() != null && change.getOriginalSubtree() != null) {
                updateOperational(change, tx);
            }
            LOG.info("onDataChanged - new Centinel - streamRecord config: {}", record);
        }
    }

    /**
     * @param tx
     * @param record
     * @param iterator
     */
    private void createIntoOperational(final ReadWriteTransaction tx, StreamRecord record, Iterator<DataObject> iterator) {
        StreamList tempStreamList = null;
        DataObject streamObject = iterator.next();
        if (!(streamObject instanceof StreamList) && !record.getStreamList().isEmpty()) {
            tempStreamList = (StreamList) record.getStreamList().get(0);
        } else if (streamObject instanceof StreamList) {
            tempStreamList = (StreamList) streamObject;
        }

        final SettableFuture<RpcResult<SetStreamOutput>> futureResult = SettableFuture.create();
        final StreamList newstreamList = tempStreamList;
        LOG.info("stream list: " + newstreamList);

        List<StreamList> streamList = new ArrayList<StreamList>();
        StreamList streamListObj = (StreamList) restService.createFromConfigToOperationalStream(newstreamList);
        streamList.add(streamListObj);

        try {
            if (streamListObj != null) {
                tx.merge(LogicalDatastoreType.OPERATIONAL, streamRecordId,
                        new StreamRecordBuilder().setStreamList(streamList).build(), true);
                tx.submit();
                LOG.info("Stream commited sucessfully to operational datastore on data change");
            }
        }

        catch (Exception e) {
            LOG.info("Failed to commit Stream");
            LOG.info("Failed to commit Stream", e);
            futureResult.set(RpcResultBuilder.<SetStreamOutput> failed()
                    .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
        }
    }

    /**
     * @param change
     * @param tx
     *            removes data from operational data store.
     */
    private void removeFromOperational(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change,
            final ReadWriteTransaction tx) {
        DataObject deletedDataObjectFromConfig = null;
        Entry<InstanceIdentifier<?>, DataObject> configRemovedEntrySet = null;

        Set<InstanceIdentifier<?>> configRemovedPath = change.getRemovedPaths();

        for (Entry<InstanceIdentifier<?>, DataObject> dataObject : change.getOriginalData().entrySet()) {
            if (configRemovedPath.contains(dataObject.getKey())) {
                configRemovedEntrySet = dataObject;
            }
        }

        deletedDataObjectFromConfig = configRemovedEntrySet.getValue();
        if (deletedDataObjectFromConfig instanceof StreamList && configRemovedEntrySet != null) {
            StreamList streamConfigObj = (StreamList) deletedDataObjectFromConfig;

            // Read operational data store
            ListenableFuture<Optional<StreamRecord>> readFutureFromOperational = tx.read(
                    LogicalDatastoreType.OPERATIONAL, streamRecordId);

            try {
                Optional<StreamRecord> streamRecord = readFutureFromOperational.get();
                List<StreamList> streamList = new ArrayList<StreamList>();

                if (streamRecord.isPresent()) {
                    streamList = streamRecord.get().getStreamList();
                }
                java.util.Iterator<StreamList> streamListIterator = streamList.iterator();

                while (streamListIterator.hasNext()) {
                    StreamList streamOperationalObj = streamListIterator.next();
                    if (streamOperationalObj.getConfigID().equals(streamConfigObj.getConfigID())) {
                        if (restService.deleteFromOperationalStream(streamOperationalObj)) {
                            LOG.info("Deleted succesfully from Graylog");
                            tx.delete(LogicalDatastoreType.OPERATIONAL,
                                    streamRecordId.child(StreamList.class, streamOperationalObj.getKey()));
                            tx.submit();
                        }
                    }
                }
            }

            catch (Exception e) {
                e.printStackTrace();
                LOG.error("Exception occured while getting record from operational data store", e.getMessage());
            }
        }
    }

    /**
     * @param change
     * @param tx
     *            updates data in operational data store.
     */
    private void updateOperational(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change,
            final ReadWriteTransaction tx) {
        DataObject configUpdatedData = change.getUpdatedSubtree();
        DataObject configOriginalData = change.getOriginalSubtree();
        if (configUpdatedData instanceof StreamRecord && configOriginalData instanceof StreamRecord) {
            StreamRecord configUpdatedStreamRecord = (StreamRecord) configUpdatedData;
            StreamRecord configOriginalStreamRecord = (StreamRecord) configOriginalData;
            List<StreamList> configUpdatedStreamList = configUpdatedStreamRecord.getStreamList();
            List<StreamList> configOriginalStreamList = configOriginalStreamRecord.getStreamList();
            StreamList streamList = null;
            Iterator<StreamList> streamListIterator = configUpdatedStreamList.iterator();
            while (streamListIterator.hasNext()) {
                StreamList configObj = null;
                configObj = streamListIterator.next();
                if (!configOriginalStreamList.contains(configObj)) {
                    streamList = configObj;
                    break;
                }
            }

            ListenableFuture<Optional<StreamRecord>> readFutureFromOperational = tx.read(
                    LogicalDatastoreType.OPERATIONAL, streamRecordId);

            try {
                Optional<StreamRecord> streamRecord = readFutureFromOperational.get();
                List<StreamList> streamListOperational = new ArrayList<StreamList>();

                if (streamRecord.isPresent()) {
                    streamListOperational = streamRecord.get().getStreamList();
                }

                java.util.Iterator<StreamList> iteratorStreamList = streamListOperational.iterator();

                while (iteratorStreamList.hasNext()) {
                    StreamList streamOperationalObj = iteratorStreamList.next();
                    LOG.info("Operational Data store .getConfigID()" + streamOperationalObj.getConfigID());
                    LOG.info("Config data store .getConfigID() " + streamList.getConfigID());
                    if (streamList.getConfigID().equalsIgnoreCase(streamOperationalObj.getConfigID())) {
                        if (streamList.getStreamRules().size() == streamOperationalObj.getStreamRules().size()
                                && streamOperationalObj.getDisabled().equalsIgnoreCase(streamList.getDisabled())
                                && restService.updateToOperationalStream(streamList)) {

                            tx.merge(LogicalDatastoreType.OPERATIONAL,
                                    streamRecordId.child(StreamList.class, streamOperationalObj.getKey()), streamList);
                            tx.submit();
                        } else {
                            if (!streamOperationalObj.getDisabled().equalsIgnoreCase(streamList.getDisabled())
                                    && restService.updateToOperationalStreamEnabler(streamList,
                                            streamOperationalObj.getStreamID())) {
                                tx.merge(LogicalDatastoreType.OPERATIONAL,
                                        streamRecordId.child(StreamList.class, streamOperationalObj.getKey()),
                                        streamList);
                                tx.submit();
                            }

                            if (streamList.getStreamRules().size() != streamOperationalObj.getStreamRules().size()) {
                                List<StreamRules> opStreamRuleList = streamOperationalObj.getStreamRules();
                                List<StreamRules> configStreamRuleList = streamList.getStreamRules();
                                StreamRule streamRule = null;
                                Iterator<StreamRules> iteratorStreamRule = configStreamRuleList.iterator();
                                while (iteratorStreamRule.hasNext()) {
                                    StreamRule streamRuleToBeUpdated = null;
                                    streamRuleToBeUpdated = iteratorStreamRule.next();
                                    if (!opStreamRuleList.contains(streamRuleToBeUpdated)) {
                                        streamRule = streamRuleToBeUpdated;
                                        LOG.debug("STREAM RULE" + streamRule);
                                        break;
                                    }
                                }

                                if (restService.updateToOperationalStreamRule(streamRule,
                                        streamOperationalObj.getStreamID()))
                                    tx.merge(LogicalDatastoreType.OPERATIONAL,
                                            streamRecordId.child(StreamList.class, streamOperationalObj.getKey()),
                                            streamList);
                                tx.submit();
                                LOG.info("Stream rule commited sucessfully to operational datastore");
                            }
                        }

                    }

                }
            }

            catch (Exception e) {
                LOG.error("Exception occured while getting stream rule record from operational data store",
                        e.getMessage());
                e.printStackTrace();
            }

        }
    }

    @Override
    public void close() throws Exception {

    }
}
