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
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.laas.rest.utilities.CentinelAlertConditionRESTServices;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldContentRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldContentRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldValueRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldValueRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertMessageCountRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertMessageCountRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldContentRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldValueRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertMessageCountRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldcontentrulerecord.StreamAlertFieldContentRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldvaluerulerecord.StreamAlertFieldValueRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertmessagecountrulerecord.StreamAlertMessageCountRuleList;
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
 * @author Abhishek Sharma 
 * 		   This class specifically provide onDataChange services
 *         that will commit configuration into GrayLog server and in case of
 *         successful commit update the MDSAL operational datastore
 */
public class CentinelLaasAlertConditionImpl implements AutoCloseable, DataChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(CentinelLaasAlertConditionImpl.class);

    public static final InstanceIdentifier<AlertMessageCountRuleRecord> alertMessageCountRuleRecordId = InstanceIdentifier
            .builder(AlertMessageCountRuleRecord.class).build();
    public static final InstanceIdentifier<AlertFieldValueRuleRecord> alertFieldValueRuleRecordId = InstanceIdentifier
            .builder(AlertFieldValueRuleRecord.class).build();
    public static final InstanceIdentifier<AlertFieldContentRuleRecord> alertFeildContentRuleRecordId = InstanceIdentifier
            .builder(AlertFieldContentRuleRecord.class).build();

    private DataBroker dataProvider;
    private final ExecutorService executor;
    private CentinelAlertConditionRESTServices restService;

    public CentinelLaasAlertConditionImpl() {
        executor = Executors.newFixedThreadPool(1);
    }

    public void setDataProvider(final DataBroker salDataProvider) {
        LOG.info(" Entered to Data Provider from CentinelAlertConditionImpl");
        this.dataProvider = salDataProvider;
        restService = CentinelAlertConditionRESTServices.getInstance();
        LOG.info("data provider set");
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
    }

    @Override
    public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change) {

        LOG.info("onDataChanged called ");
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();

        DataObject dataObject = change.getUpdatedSubtree();
        Iterator<DataObject> iterator = change.getCreatedData().values().iterator();
        if (iterator.hasNext()) {
            createIntoOperational(tx, dataObject, iterator);

        } else if (!change.getRemovedPaths().isEmpty()) {
            removeFromOperational(change, tx);

        } else if (change.getUpdatedSubtree() != null && change.getOriginalSubtree() != null) {
            updateOperational(change, tx);
        }

    }

    /**
     * @param tx
     * @param record
     * @param iterator
     *            Creates data into operational data store if successfully
     *            created in Log analyzer
     */
    private void createIntoOperational(final ReadWriteTransaction tx, DataObject record, Iterator<DataObject> iterator) {

        if (record instanceof AlertMessageCountRuleRecord) {
            final SettableFuture<RpcResult<SetAlertMessageCountRuleOutput>> futureResult = SettableFuture.create();
            DataObject messageCountObject = iterator.next();

            StreamAlertMessageCountRuleList tempStreamAlertMessageCountRuleList = null;
            if (!(messageCountObject instanceof StreamAlertMessageCountRuleList)) {
                tempStreamAlertMessageCountRuleList = (StreamAlertMessageCountRuleList) ((AlertMessageCountRuleRecord) record)
                        .getStreamAlertMessageCountRuleList().get(0);
            } else if (messageCountObject instanceof StreamAlertMessageCountRuleList) {
                tempStreamAlertMessageCountRuleList = (StreamAlertMessageCountRuleList) messageCountObject;
            }

            final StreamAlertMessageCountRuleList streamAlertMessageCountRuleList = tempStreamAlertMessageCountRuleList;

            List<StreamAlertMessageCountRuleList> streamAlertRuleList = new ArrayList<StreamAlertMessageCountRuleList>();
            StreamAlertMessageCountRuleList streamAlertMessageCountRuleListFromAnalyzer = (StreamAlertMessageCountRuleList) restService
                    .createFromConfigToOperational(streamAlertMessageCountRuleList);
            streamAlertRuleList.add(streamAlertMessageCountRuleListFromAnalyzer);

            try {
                if (streamAlertMessageCountRuleListFromAnalyzer != null) {
                    tx.merge(
                            LogicalDatastoreType.OPERATIONAL,
                            alertMessageCountRuleRecordId,
                            new AlertMessageCountRuleRecordBuilder().setStreamAlertMessageCountRuleList(
                                    streamAlertRuleList).build(), true);
                    tx.submit();
                    LOG.info("Rule commited sucessfully to operational datastore on data change for MessageCountRule");
                }
            }

            catch (Exception e) {
                LOG.info("Failed to commit Rule", e);
                futureResult.set(RpcResultBuilder.<SetAlertMessageCountRuleOutput> failed()
                        .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
            }

        } else if (record instanceof AlertFieldValueRuleRecord) {

            final SettableFuture<RpcResult<SetAlertFieldValueRuleOutput>> futureResult = SettableFuture.create();

            StreamAlertFieldValueRuleList tempStreamAlertFieldValueRuleList = null;
            DataObject fieldValueObject = iterator.next();
            if (!(fieldValueObject instanceof StreamAlertFieldValueRuleList)) {
                tempStreamAlertFieldValueRuleList = (StreamAlertFieldValueRuleList) ((AlertFieldValueRuleRecord) record)
                        .getStreamAlertFieldValueRuleList().get(0);
            } else if (fieldValueObject instanceof StreamAlertFieldValueRuleList) {
                tempStreamAlertFieldValueRuleList = (StreamAlertFieldValueRuleList) fieldValueObject;
            }

            final StreamAlertFieldValueRuleList streamAlertFieldValueRuleList = tempStreamAlertFieldValueRuleList;

            List<StreamAlertFieldValueRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldValueRuleList>();
            StreamAlertFieldValueRuleList streamAlertFieldValueRuleListFromAnalyzer = (StreamAlertFieldValueRuleList) restService
                    .createFromConfigToOperational(streamAlertFieldValueRuleList);
            streamAlertRuleList.add(streamAlertFieldValueRuleListFromAnalyzer);

            try {
                if (streamAlertFieldValueRuleListFromAnalyzer != null) {
                    tx.merge(LogicalDatastoreType.OPERATIONAL, alertFieldValueRuleRecordId,
                            new AlertFieldValueRuleRecordBuilder()
                                    .setStreamAlertFieldValueRuleList(streamAlertRuleList).build(), true);
                    tx.submit();
                    LOG.info("Rule commited sucessfully to operational datastore on data change for FieldValueRule");
                }
            }

            catch (Exception e) {
                LOG.info("Failed to commit Rule", e);
                futureResult.set(RpcResultBuilder.<SetAlertFieldValueRuleOutput> failed()
                        .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
            }

        } else if (record instanceof AlertFieldContentRuleRecord) {

            final SettableFuture<RpcResult<SetAlertFieldContentRuleOutput>> futureResult = SettableFuture.create();
            StreamAlertFieldContentRuleList tempStreamAlertFieldContentRuleList = null;
            DataObject fieldContentObject = iterator.next();
            if (!(fieldContentObject instanceof StreamAlertFieldContentRuleList)) {
                tempStreamAlertFieldContentRuleList = (StreamAlertFieldContentRuleList) ((AlertFieldContentRuleRecord) record)
                        .getStreamAlertFieldContentRuleList().get(0);
            } else if (fieldContentObject instanceof StreamAlertFieldContentRuleList) {
                tempStreamAlertFieldContentRuleList = (StreamAlertFieldContentRuleList) fieldContentObject;
            }

            final StreamAlertFieldContentRuleList streamAlertFieldContentRuleList = tempStreamAlertFieldContentRuleList;

            List<StreamAlertFieldContentRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldContentRuleList>();
            StreamAlertFieldContentRuleList streamAlertFieldContentRuleListFromAnalyzer = (StreamAlertFieldContentRuleList) restService
                    .createFromConfigToOperational(streamAlertFieldContentRuleList);
            streamAlertRuleList.add(streamAlertFieldContentRuleListFromAnalyzer);

            try {
                if (streamAlertFieldContentRuleListFromAnalyzer != null) {
                    tx.merge(
                            LogicalDatastoreType.OPERATIONAL,
                            alertFeildContentRuleRecordId,
                            new AlertFieldContentRuleRecordBuilder().setStreamAlertFieldContentRuleList(
                                    streamAlertRuleList).build(), true);
                    tx.submit();
                    LOG.info("Rule commited sucessfully to operational datastore on data change for FieldValueRule");
                }
            }

            catch (Exception e) {
                LOG.info("Failed to commit Rule", e);
                futureResult.set(RpcResultBuilder.<SetAlertFieldContentRuleOutput> failed()
                        .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
            }

        }
    }

    /**
     * @param change
     * @param tx
     *            Updates data into operational data store if successfully
     *            updated in Log analyzer
     */
    private void updateOperational(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change,
            final ReadWriteTransaction tx) {
        DataObject configUpdatedData = change.getUpdatedSubtree();
        DataObject configOriginalData = change.getOriginalSubtree();
        try {

            if (configUpdatedData instanceof AlertMessageCountRuleRecord
                    && configOriginalData instanceof AlertMessageCountRuleRecord) {
                AlertMessageCountRuleRecord configUpdatedAlertMessageCountRuleRecord = (AlertMessageCountRuleRecord) configUpdatedData;
                AlertMessageCountRuleRecord configOriginalAlertMessageCountRuleRecord = (AlertMessageCountRuleRecord) configOriginalData;
                List<StreamAlertMessageCountRuleList> configUpdatedAlertMessageCountRuleList = configUpdatedAlertMessageCountRuleRecord
                        .getStreamAlertMessageCountRuleList();
                List<StreamAlertMessageCountRuleList> configOriginalAlertMessageCountRuleList = configOriginalAlertMessageCountRuleRecord
                        .getStreamAlertMessageCountRuleList();
                StreamAlertMessageCountRuleList alertMessageCountRuleList = null;
                Iterator<StreamAlertMessageCountRuleList> iteratorz = configUpdatedAlertMessageCountRuleList.iterator();
                while (iteratorz.hasNext()) {
                    StreamAlertMessageCountRuleList anObj = null;
                    anObj = iteratorz.next();
                    if (!configOriginalAlertMessageCountRuleList.contains(anObj)) {
                        alertMessageCountRuleList = anObj;
                        break;
                    }
                }

                // Read operational data store
                ListenableFuture<Optional<AlertMessageCountRuleRecord>> readFutureFromOperational = tx.read(
                        LogicalDatastoreType.OPERATIONAL, alertMessageCountRuleRecordId);

                Optional<AlertMessageCountRuleRecord> alertMessageCountRuleRecord = readFutureFromOperational.get();

                List<StreamAlertMessageCountRuleList> streamAlertRuleList = new ArrayList<StreamAlertMessageCountRuleList>();

                if (alertMessageCountRuleRecord.isPresent()) {
                    streamAlertRuleList = alertMessageCountRuleRecord.get().getStreamAlertMessageCountRuleList();
                }

                java.util.Iterator<StreamAlertMessageCountRuleList> iterator1 = streamAlertRuleList.iterator();

                while (iterator1.hasNext()) {
                    StreamAlertMessageCountRuleList streamAlertMessageOperationalObj = iterator1.next();
                    LOG.info("Operational Data store .getConfigID()" + streamAlertMessageOperationalObj.getConfigID());
                    LOG.info("Config data store .getConfigID() " + alertMessageCountRuleList.getConfigID());
                    if (streamAlertMessageOperationalObj.getConfigID().equals(alertMessageCountRuleList.getConfigID())) {
                        if (restService.updateToOperational(alertMessageCountRuleList)) {
                            tx.merge(LogicalDatastoreType.OPERATIONAL, alertMessageCountRuleRecordId.child(
                                    StreamAlertMessageCountRuleList.class, streamAlertMessageOperationalObj.getKey()),
                                    alertMessageCountRuleList);
                            tx.submit();
                        }
                    }
                }
            } else if (configUpdatedData instanceof AlertFieldContentRuleRecord
                    && configOriginalData instanceof AlertFieldContentRuleRecord) {
                AlertFieldContentRuleRecord configUpdatedAlertFieldContentRuleRecord = (AlertFieldContentRuleRecord) configUpdatedData;
                AlertFieldContentRuleRecord configOriginalAlertFieldContentRuleRecord = (AlertFieldContentRuleRecord) configOriginalData;
                List<StreamAlertFieldContentRuleList> configUpdatedAlertFieldContentRuleList = configUpdatedAlertFieldContentRuleRecord
                        .getStreamAlertFieldContentRuleList();
                List<StreamAlertFieldContentRuleList> configOriginalAlertFieldContentRuleList = configOriginalAlertFieldContentRuleRecord
                        .getStreamAlertFieldContentRuleList();
                StreamAlertFieldContentRuleList alertFieldContentRuleList = null;
                Iterator<StreamAlertFieldContentRuleList> iteratorz = configUpdatedAlertFieldContentRuleList.iterator();
                while (iteratorz.hasNext()) {
                    StreamAlertFieldContentRuleList anObj = null;
                    anObj = iteratorz.next();
                    if (!configOriginalAlertFieldContentRuleList.contains(anObj)) {
                        alertFieldContentRuleList = anObj;
                        break;
                    }
                }

                // Read operational data store
                ListenableFuture<Optional<AlertFieldContentRuleRecord>> readFutureFromOperational = tx.read(
                        LogicalDatastoreType.OPERATIONAL, alertFeildContentRuleRecordId);

                Optional<AlertFieldContentRuleRecord> alertFieldContentRuleRecord = readFutureFromOperational.get();

                List<StreamAlertFieldContentRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldContentRuleList>();

                if (alertFieldContentRuleRecord.isPresent()) {
                    streamAlertRuleList = alertFieldContentRuleRecord.get().getStreamAlertFieldContentRuleList();
                }

                java.util.Iterator<StreamAlertFieldContentRuleList> iterator1 = streamAlertRuleList.iterator();

                while (iterator1.hasNext()) {
                    StreamAlertFieldContentRuleList streamAlertFieldContentOperationalObj = iterator1.next();
                    LOG.info("Operational Data store .getConfigID()"
                            + streamAlertFieldContentOperationalObj.getConfigID());
                    LOG.info("Config data store .getConfigID() " + alertFieldContentRuleList.getConfigID());
                    if (streamAlertFieldContentOperationalObj.getConfigID().equals(
                            alertFieldContentRuleList.getConfigID())) {
                        if (restService.updateToOperational(alertFieldContentRuleList)) {
                            tx.merge(LogicalDatastoreType.OPERATIONAL, alertFeildContentRuleRecordId.child(
                                    StreamAlertFieldContentRuleList.class,
                                    streamAlertFieldContentOperationalObj.getKey()), alertFieldContentRuleList);
                            tx.submit();
                        }
                    }
                }
            } else if (configUpdatedData instanceof AlertFieldValueRuleRecord
                    && configOriginalData instanceof AlertFieldValueRuleRecord) {
                AlertFieldValueRuleRecord configUpdatedAlertFieldValueRuleRecord = (AlertFieldValueRuleRecord) configUpdatedData;
                AlertFieldValueRuleRecord configOriginalAlertFieldValueRuleRecord = (AlertFieldValueRuleRecord) configOriginalData;
                List<StreamAlertFieldValueRuleList> configUpdatedAlertFieldValueRuleList = configUpdatedAlertFieldValueRuleRecord
                        .getStreamAlertFieldValueRuleList();
                List<StreamAlertFieldValueRuleList> configOriginalAlertFieldValueRuleList = configOriginalAlertFieldValueRuleRecord
                        .getStreamAlertFieldValueRuleList();
                StreamAlertFieldValueRuleList alertFieldValueRuleList = null;
                Iterator<StreamAlertFieldValueRuleList> iteratorz = configUpdatedAlertFieldValueRuleList.iterator();
                while (iteratorz.hasNext()) {
                    StreamAlertFieldValueRuleList anObj = null;
                    anObj = iteratorz.next();
                    if (!configOriginalAlertFieldValueRuleList.contains(anObj)) {
                        alertFieldValueRuleList = anObj;
                        break;
                    }
                }

                // Read operational data store
                ListenableFuture<Optional<AlertFieldValueRuleRecord>> readFutureFromOperational = tx.read(
                        LogicalDatastoreType.OPERATIONAL, alertFieldValueRuleRecordId);

                Optional<AlertFieldValueRuleRecord> alertFieldValueRuleRecord = readFutureFromOperational.get();

                List<StreamAlertFieldValueRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldValueRuleList>();

                if (alertFieldValueRuleRecord.isPresent()) {
                    streamAlertRuleList = alertFieldValueRuleRecord.get().getStreamAlertFieldValueRuleList();
                }

                java.util.Iterator<StreamAlertFieldValueRuleList> iterator1 = streamAlertRuleList.iterator();

                while (iterator1.hasNext()) {
                    StreamAlertFieldValueRuleList streamAlertFieldValueOperationalObj = iterator1.next();
                    LOG.info("Operational Data store .getConfigID()"
                            + streamAlertFieldValueOperationalObj.getConfigID());
                    LOG.info("Config data store .getConfigID() " + alertFieldValueRuleList.getConfigID());
                    if (streamAlertFieldValueOperationalObj.getConfigID().equals(alertFieldValueRuleList.getConfigID())) {
                        if (restService.updateToOperational(alertFieldValueRuleList)) {
                            tx.merge(LogicalDatastoreType.OPERATIONAL, alertFieldValueRuleRecordId.child(
                                    StreamAlertFieldValueRuleList.class, streamAlertFieldValueOperationalObj.getKey()),
                                    alertFieldValueRuleList);
                            tx.submit();
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Exception occured while getting record from operational data store", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param change
     * @param tx
     *            Deletes data from operational data store if successfully
     *            deleted from Log analyzer
     */
    private void removeFromOperational(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change,
            final ReadWriteTransaction tx) {
        DataObject deletedDataObjectFromConfig = null;
        Entry<InstanceIdentifier<?>, DataObject> configRemovedEntrySet = null;

        Set<InstanceIdentifier<?>> configRemovedPath = change.getRemovedPaths();

        for (Entry<InstanceIdentifier<?>, DataObject> obj : change.getOriginalData().entrySet()) {
            if (configRemovedPath.contains(obj.getKey())) {
                configRemovedEntrySet = obj;
            }
        }

        try {

            deletedDataObjectFromConfig = configRemovedEntrySet.getValue();
            if (deletedDataObjectFromConfig instanceof StreamAlertMessageCountRuleList && configRemovedEntrySet != null) {
                StreamAlertMessageCountRuleList alertMessageCountConfigObj = (StreamAlertMessageCountRuleList) deletedDataObjectFromConfig;

                // Read operational data store
                ListenableFuture<Optional<AlertMessageCountRuleRecord>> readFutureFromOperational = tx.read(
                        LogicalDatastoreType.OPERATIONAL, alertMessageCountRuleRecordId);

                Optional<AlertMessageCountRuleRecord> alertMessageCountRuleRecord = readFutureFromOperational.get();

                List<StreamAlertMessageCountRuleList> streamAlertRuleList = new ArrayList<StreamAlertMessageCountRuleList>();

                if (alertMessageCountRuleRecord.isPresent()) {
                    streamAlertRuleList = alertMessageCountRuleRecord.get().getStreamAlertMessageCountRuleList();

                }
                java.util.Iterator<StreamAlertMessageCountRuleList> iterator1 = streamAlertRuleList.iterator();

                while (iterator1.hasNext()) {
                    StreamAlertMessageCountRuleList streamAlertMessageOperationalObj = iterator1.next();
                    if (streamAlertMessageOperationalObj.getConfigID().equals(alertMessageCountConfigObj.getConfigID())) {
                        // if following statement returns true then remove from
                        // operational data store
                        if (restService.deleteFromOperational(streamAlertMessageOperationalObj)) {
                            LOG.info("Deleted succesfully from Graylog");
                            tx.delete(LogicalDatastoreType.OPERATIONAL, alertMessageCountRuleRecordId.child(
                                    StreamAlertMessageCountRuleList.class, streamAlertMessageOperationalObj.getKey()));
                            tx.submit();
                        }
                    }
                }

            }

            else if (deletedDataObjectFromConfig instanceof StreamAlertFieldContentRuleList
                    && configRemovedEntrySet != null) {
                StreamAlertFieldContentRuleList alertFieldContentConfigObj = (StreamAlertFieldContentRuleList) deletedDataObjectFromConfig;

                // Read operational data store
                ListenableFuture<Optional<AlertFieldContentRuleRecord>> readFutureFromOperational = tx.read(
                        LogicalDatastoreType.OPERATIONAL, alertFeildContentRuleRecordId);

                Optional<AlertFieldContentRuleRecord> alertFieldContentRuleRecord = readFutureFromOperational.get();

                List<StreamAlertFieldContentRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldContentRuleList>();

                if (alertFieldContentRuleRecord.isPresent()) {
                    streamAlertRuleList = alertFieldContentRuleRecord.get().getStreamAlertFieldContentRuleList();

                }
                java.util.Iterator<StreamAlertFieldContentRuleList> iterator1 = streamAlertRuleList.iterator();

                while (iterator1.hasNext()) {
                    StreamAlertFieldContentRuleList streamAlertFieldContentOperationalObj = iterator1.next();
                    if (streamAlertFieldContentOperationalObj.getConfigID().equals(
                            alertFieldContentConfigObj.getConfigID())) {
                        // if following statement returns true then remove from
                        // operational data store
                        if (restService.deleteFromOperational(streamAlertFieldContentOperationalObj)) {
                            LOG.info("Deleted succesfully from Graylog");
                            tx.delete(LogicalDatastoreType.OPERATIONAL, alertFeildContentRuleRecordId.child(
                                    StreamAlertFieldContentRuleList.class,
                                    streamAlertFieldContentOperationalObj.getKey()));
                            tx.submit();
                        }
                    }
                }

            } else if (deletedDataObjectFromConfig instanceof StreamAlertFieldValueRuleList
                    && configRemovedEntrySet != null) {
                StreamAlertFieldValueRuleList alertFieldValueConfigObj = (StreamAlertFieldValueRuleList) deletedDataObjectFromConfig;

                // Read operational data store
                ListenableFuture<Optional<AlertFieldValueRuleRecord>> readFutureFromOperational = tx.read(
                        LogicalDatastoreType.OPERATIONAL, alertFieldValueRuleRecordId);

                Optional<AlertFieldValueRuleRecord> alertFieldValueRuleRecord = readFutureFromOperational.get();

                List<StreamAlertFieldValueRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldValueRuleList>();

                if (alertFieldValueRuleRecord.isPresent()) {
                    streamAlertRuleList = alertFieldValueRuleRecord.get().getStreamAlertFieldValueRuleList();

                }
                java.util.Iterator<StreamAlertFieldValueRuleList> iterator1 = streamAlertRuleList.iterator();

                while (iterator1.hasNext()) {
                    StreamAlertFieldValueRuleList streamAlertFieldValueOperationalObj = iterator1.next();
                    if (streamAlertFieldValueOperationalObj.getConfigID()
                            .equals(alertFieldValueConfigObj.getConfigID())) {
                        if (restService.deleteFromOperational(streamAlertFieldValueOperationalObj)) {
                            LOG.info("Deleted succesfully from Graylog");
                            tx.delete(LogicalDatastoreType.OPERATIONAL, alertFieldValueRuleRecordId.child(
                                    StreamAlertFieldValueRuleList.class, streamAlertFieldValueOperationalObj.getKey()));
                            tx.submit();
                        }
                    }
                }

            }
        } catch (Exception e) {
            LOG.error("Exception occured while getting record from operational data store", e.getMessage());
            e.printStackTrace();
        }
    }

}
