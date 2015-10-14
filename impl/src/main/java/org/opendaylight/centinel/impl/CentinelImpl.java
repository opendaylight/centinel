/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.NotificationService;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldContentRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldContentRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldValueRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldValueRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertruleService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertFieldContentRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertFieldContentRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertFieldContentRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertFieldValueRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertFieldValueRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertFieldValueRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertMessageCountRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertMessageCountRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertMessageCountRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.GetAllAlertRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.GetAllAlertRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.GetAllAlertRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldContentRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldContentRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldContentRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldValueRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldValueRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldValueRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertMessageCountRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertMessageCountRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertMessageCountRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertMessageCountRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertMessageCountRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertFieldContentRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertFieldContentRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertFieldContentRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertFieldValueRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertFieldValueRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertFieldValueRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertMessageCountRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertMessageCountRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertMessageCountRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldcontentrulerecord.StreamAlertFieldContentRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldcontentrulerecord.StreamAlertFieldContentRuleListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldvaluerulerecord.StreamAlertFieldValueRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldvaluerulerecord.StreamAlertFieldValueRuleListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertmessagecountrulerecord.StreamAlertMessageCountRuleListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertmessagecountrulerecord.StreamAlertMessageCountRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.get.all.alert.rule.output.StreamAlertFieldContentRuleListSorted;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.get.all.alert.rule.output.StreamAlertFieldContentRuleListSortedBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.get.all.alert.rule.output.StreamAlertFieldValueRuleListSorted;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.get.all.alert.rule.output.StreamAlertFieldValueRuleListSortedBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.get.all.alert.rule.output.StreamAlertMessageCountRuleListSorted;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.get.all.alert.rule.output.StreamAlertMessageCountRuleListSortedBuilder;

import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

public class CentinelImpl implements AutoCloseable, DataChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(CentinelImpl.class);

    public static final InstanceIdentifier<AlertMessageCountRuleRecord> alertMessageCountRuleRecordId = InstanceIdentifier
            .builder(AlertMessageCountRuleRecord.class).build();
    public static final InstanceIdentifier<AlertFieldValueRuleRecord> alertFieldValueRuleRecordId = InstanceIdentifier
            .builder(AlertFieldValueRuleRecord.class).build();
    public static final InstanceIdentifier<AlertFieldContentRuleRecord> alertFeildContentRuleRecordId = InstanceIdentifier
            .builder(AlertFieldContentRuleRecord.class).build();

    private NotificationService notificationProvider;
    private DataBroker dataProvider;
    private final ExecutorService executor;

    public CentinelImpl() {
        executor = Executors.newFixedThreadPool(1);
    }

    public void setNotificationProvider(final NotificationService salService) {
        System.out.println(" Entered to Notification ");
        this.notificationProvider = salService;
        LOG.info("notifictaion provider set");
    }

    private StreamAlertMessageCountRuleList buildAlertMessageCountRuleRecord(final SetAlertMessageCountRuleInput input) {

        return new StreamAlertMessageCountRuleListBuilder().setAlertTypeClassifier(input.getAlertTypeClassifier())
                .setMessageCountBacklog(input.getMessageCountBacklog())
                .setMessageCountCount(input.getMessageCountCount()).setMessageCountGrace(input.getMessageCountGrace())
                .setMessageCountOperator(input.getMessageCountOperator()).setTimeStamp(input.getTimeStamp())
                .setNodeType(input.getNodeType()).setRuleID(input.getRuleID())
                .setConfigID(String.format("%x", (int) (Math.random() * 10000)))
                .setRuleTypeClassifier(input.getRuleTypeClassifier()).setStreamID(input.getStreamID()).build();

    }

    private StreamAlertFieldValueRuleList buildAlertFieldValueRuleRecord(final SetAlertFieldValueRuleInput input) {

        return new StreamAlertFieldValueRuleListBuilder().setAlertTypeClassifier(input.getAlertTypeClassifier())
                .setFieldValueBacklog(input.getFieldValueBacklog()).setFieldValueField(input.getFieldValueField())
                .setFieldValueGrace(input.getFieldValueGrace())
                .setFieldValueThreshhold(input.getFieldValueThreshhold())
                .setFieldValueThreshholdType(input.getFieldValueThreshholdType())
                .setFieldValueType(input.getFieldValueType()).setTimeStamp(input.getTimeStamp())
                .setNodeType(input.getNodeType()).setRuleID(input.getRuleID())
                .setConfigID(String.format("%x", (int) (Math.random() * 10000)))
                .setRuleTypeClassifier(input.getRuleTypeClassifier()).setStreamID(input.getStreamID()).build();

    }

    private StreamAlertFieldContentRuleList buildAlertFieldContentRuleRecord(final SetAlertFieldContentRuleInput input) {

        return new StreamAlertFieldContentRuleListBuilder().setAlertTypeClassifier(input.getAlertTypeClassifier())
                .setFieldContentBacklog(input.getFieldContentBacklog())
                .setFieldContentCompareToValue(input.getFieldContentCompareToValue())
                .setFieldContentField(input.getFieldContentField()).setFieldContentGrace(input.getFieldContentGrace())
                .setTimeStamp(input.getTimeStamp()).setNodeType(input.getNodeType()).setRuleID(input.getRuleID())
                .setConfigID(String.format("%x", (int) (Math.random() * 10000)))
                .setRuleTypeClassifier(input.getRuleTypeClassifier()).setStreamID(input.getStreamID()).build();

    }

    private StreamAlertMessageCountRuleList buildUpdateAlertMessageCountRuleRecord(
            final UpdateAlertMessageCountRuleInput input, final StreamAlertMessageCountRuleList obj) {

        StreamAlertMessageCountRuleListBuilder streamAlertMessageCountRuleListBuilder = new StreamAlertMessageCountRuleListBuilder();

        if (input.getAlertTypeClassifier() != null)
            streamAlertMessageCountRuleListBuilder.setAlertTypeClassifier(input.getAlertTypeClassifier());
        else
            streamAlertMessageCountRuleListBuilder.setAlertTypeClassifier(obj.getAlertTypeClassifier());

        if (input.getMessageCountBacklog() != null)
            streamAlertMessageCountRuleListBuilder.setMessageCountBacklog(input.getMessageCountBacklog());
        else
            streamAlertMessageCountRuleListBuilder.setMessageCountBacklog(obj.getMessageCountBacklog());

        if (input.getMessageCountCount() != null)
            streamAlertMessageCountRuleListBuilder.setMessageCountCount(input.getMessageCountCount());
        else
            streamAlertMessageCountRuleListBuilder.setMessageCountCount(obj.getMessageCountCount());

        if (input.getMessageCountGrace() != null)
            streamAlertMessageCountRuleListBuilder.setMessageCountCount(input.getMessageCountGrace());
        else
            streamAlertMessageCountRuleListBuilder.setMessageCountCount(obj.getMessageCountGrace());

        if (input.getMessageCountOperator() != null)
            streamAlertMessageCountRuleListBuilder.setMessageCountOperator(input.getMessageCountOperator());
        else
            streamAlertMessageCountRuleListBuilder.setMessageCountOperator(obj.getMessageCountOperator());

        if (input.getNodeType() != null)
            streamAlertMessageCountRuleListBuilder.setNodeType(input.getNodeType());
        else
            streamAlertMessageCountRuleListBuilder.setNodeType(obj.getNodeType());

        if (input.getRuleID() != null)
            streamAlertMessageCountRuleListBuilder.setRuleID(input.getRuleID());
        else
            streamAlertMessageCountRuleListBuilder.setRuleID(obj.getRuleID());

        if (input.getRuleTypeClassifier() != null)
            streamAlertMessageCountRuleListBuilder.setRuleTypeClassifier(input.getRuleTypeClassifier());
        else
            streamAlertMessageCountRuleListBuilder.setRuleTypeClassifier(obj.getRuleTypeClassifier());

        if (input.getTimeStamp() != null)
            streamAlertMessageCountRuleListBuilder.setTimeStamp(input.getTimeStamp());
        else
            streamAlertMessageCountRuleListBuilder.setTimeStamp(obj.getTimeStamp());

        if (input.getStreamID() != null)
            streamAlertMessageCountRuleListBuilder.setStreamID(input.getStreamID());

        streamAlertMessageCountRuleListBuilder.setConfigID(obj.getConfigID());

        return streamAlertMessageCountRuleListBuilder.build();
    }

    private StreamAlertFieldContentRuleList buildUpdateAlertFieldContentRuleRecord(
            final UpdateAlertFieldContentRuleInput input, final StreamAlertFieldContentRuleList obj) {

        StreamAlertFieldContentRuleListBuilder streamAlertFieldContentRuleListBuilder = new StreamAlertFieldContentRuleListBuilder();

        if (input.getAlertTypeClassifier() != null)
            streamAlertFieldContentRuleListBuilder.setAlertTypeClassifier(input.getAlertTypeClassifier());
        else
            streamAlertFieldContentRuleListBuilder.setAlertTypeClassifier(obj.getAlertTypeClassifier());

        if (input.getFieldContentField() != null)
            streamAlertFieldContentRuleListBuilder.setFieldContentField(input.getFieldContentField());
        else
            streamAlertFieldContentRuleListBuilder.setFieldContentField(obj.getFieldContentField());

        if (input.getFieldContentBacklog() != null)
            streamAlertFieldContentRuleListBuilder.setFieldContentBacklog(input.getFieldContentBacklog());
        else
            streamAlertFieldContentRuleListBuilder.setFieldContentBacklog(obj.getFieldContentBacklog());

        if (input.getFieldContentCompareToValue() != null)
            streamAlertFieldContentRuleListBuilder.setFieldContentCompareToValue(input.getFieldContentCompareToValue());
        else
            streamAlertFieldContentRuleListBuilder.setFieldContentCompareToValue(obj.getFieldContentCompareToValue());

        if (input.getFieldContentGrace() != null)
            streamAlertFieldContentRuleListBuilder.setFieldContentGrace(input.getFieldContentGrace());
        else
            streamAlertFieldContentRuleListBuilder.setFieldContentGrace(obj.getFieldContentGrace());

        if (input.getNodeType() != null)
            streamAlertFieldContentRuleListBuilder.setNodeType(input.getNodeType());
        else
            streamAlertFieldContentRuleListBuilder.setNodeType(obj.getNodeType());

        if (input.getRuleID() != null)
            streamAlertFieldContentRuleListBuilder.setRuleID(input.getRuleID());
        else
            streamAlertFieldContentRuleListBuilder.setRuleID(obj.getRuleID());

        if (input.getRuleTypeClassifier() != null)
            streamAlertFieldContentRuleListBuilder.setRuleTypeClassifier(input.getRuleTypeClassifier());
        else
            streamAlertFieldContentRuleListBuilder.setRuleTypeClassifier(obj.getRuleTypeClassifier());

        if (input.getTimeStamp() != null)
            streamAlertFieldContentRuleListBuilder.setTimeStamp(input.getTimeStamp());
        else
            streamAlertFieldContentRuleListBuilder.setTimeStamp(obj.getTimeStamp());

        if (input.getStreamID() != null)
            streamAlertFieldContentRuleListBuilder.setStreamID(input.getStreamID());

        streamAlertFieldContentRuleListBuilder.setConfigID(obj.getConfigID());

        return streamAlertFieldContentRuleListBuilder.build();
    }

    private StreamAlertFieldValueRuleList buildUpdateAlertFieldValueRuleRecord(
            final UpdateAlertFieldValueRuleInput input, final StreamAlertFieldValueRuleList obj) {

        StreamAlertFieldValueRuleListBuilder streamAlertFieldValueRuleListBuilder = new StreamAlertFieldValueRuleListBuilder();

        if (input.getAlertTypeClassifier() != null)
            streamAlertFieldValueRuleListBuilder.setAlertTypeClassifier(input.getAlertTypeClassifier());
        else
            streamAlertFieldValueRuleListBuilder.setAlertTypeClassifier(obj.getAlertTypeClassifier());

        if (input.getFieldValueBacklog() != null)
            streamAlertFieldValueRuleListBuilder.setFieldValueBacklog(input.getFieldValueBacklog());
        else
            streamAlertFieldValueRuleListBuilder.setFieldValueBacklog(obj.getFieldValueBacklog());

        if (input.getFieldValueField() != null)
            streamAlertFieldValueRuleListBuilder.setFieldValueField(input.getFieldValueField());
        else
            streamAlertFieldValueRuleListBuilder.setFieldValueField(obj.getFieldValueField());

        if (input.getFieldValueGrace() != null)
            streamAlertFieldValueRuleListBuilder.setFieldValueGrace(input.getFieldValueGrace());
        else
            streamAlertFieldValueRuleListBuilder.setFieldValueGrace(obj.getFieldValueGrace());

        if (input.getFieldValueThreshhold() != null)
            streamAlertFieldValueRuleListBuilder.setFieldValueThreshhold(input.getFieldValueThreshhold());
        else
            streamAlertFieldValueRuleListBuilder.setFieldValueThreshhold(obj.getFieldValueThreshhold());

        if (input.getFieldValueThreshholdType() != null)
            streamAlertFieldValueRuleListBuilder.setFieldValueThreshholdType(input.getFieldValueThreshholdType());
        else
            streamAlertFieldValueRuleListBuilder.setFieldValueThreshholdType(obj.getFieldValueThreshholdType());

        if (input.getFieldValueType() != null)
            streamAlertFieldValueRuleListBuilder.setFieldValueType(input.getFieldValueType());
        else
            streamAlertFieldValueRuleListBuilder.setFieldValueType(obj.getFieldValueType());

        if (input.getNodeType() != null)
            streamAlertFieldValueRuleListBuilder.setNodeType(input.getNodeType());
        else
            streamAlertFieldValueRuleListBuilder.setNodeType(obj.getNodeType());

        if (input.getRuleID() != null)
            streamAlertFieldValueRuleListBuilder.setRuleID(input.getRuleID());
        else
            streamAlertFieldValueRuleListBuilder.setRuleID(obj.getRuleID());

        if (input.getRuleTypeClassifier() != null)
            streamAlertFieldValueRuleListBuilder.setRuleTypeClassifier(input.getRuleTypeClassifier());
        else
            streamAlertFieldValueRuleListBuilder.setRuleTypeClassifier(obj.getRuleTypeClassifier());

        if (input.getTimeStamp() != null)
            streamAlertFieldValueRuleListBuilder.setTimeStamp(input.getTimeStamp());
        else
            streamAlertFieldValueRuleListBuilder.setTimeStamp(obj.getTimeStamp());

        if (input.getStreamID() != null)
            streamAlertFieldValueRuleListBuilder.setStreamID(input.getStreamID());

        streamAlertFieldValueRuleListBuilder.setConfigID(obj.getConfigID());
        return streamAlertFieldValueRuleListBuilder.build();
    }

    public void setDataProvider(final DataBroker salDataProvider) {
        System.out.println(" Entered to Data Provider");
        this.dataProvider = salDataProvider;
        LOG.info("data provider set");
    }

    @Override
    public void close() throws Exception {
        // When we close this service we need to shutdown our executor!
        executor.shutdown();

        if (dataProvider != null) {
            WriteTransaction tx = dataProvider.newWriteOnlyTransaction();
            System.out.println(" Transaction written");
            tx.delete(LogicalDatastoreType.CONFIGURATION, alertMessageCountRuleRecordId);

        }

    }

    @Override
    public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change) {

        LOG.info("onDataChanged called ");
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();

        DataObject dataObject = change.getUpdatedSubtree();

        if (dataObject instanceof AlertMessageCountRuleRecord) {

            AlertMessageCountRuleRecord record = (AlertMessageCountRuleRecord) dataObject;
            Iterator<DataObject> iterator = change.getCreatedData().values().iterator();
            if (iterator.hasNext()) {
                StreamAlertMessageCountRuleList tempStreamAlertMessageCountRuleList = null;
                DataObject messageCountObject = iterator.next();
                if (!(messageCountObject instanceof StreamAlertMessageCountRuleList)) {
                    tempStreamAlertMessageCountRuleList = (StreamAlertMessageCountRuleList) record
                            .getStreamAlertMessageCountRuleList().get(0);
                } else if (messageCountObject instanceof StreamAlertMessageCountRuleList) {
                    tempStreamAlertMessageCountRuleList = (StreamAlertMessageCountRuleList) messageCountObject;
                }

                final StreamAlertMessageCountRuleList streamAlertMessageCountRuleList = tempStreamAlertMessageCountRuleList;

                LOG.info("Alert message count rule list: " + streamAlertMessageCountRuleList);
                ListenableFuture<Optional<AlertMessageCountRuleRecord>> readFuture = tx.read(
                        LogicalDatastoreType.OPERATIONAL, alertMessageCountRuleRecordId);

                final ListenableFuture<Void> commitFuture = Futures.transform(readFuture,
                        new AsyncFunction<Optional<AlertMessageCountRuleRecord>, Void>() {

                            @Override
                            public ListenableFuture<Void> apply(
                                    final Optional<AlertMessageCountRuleRecord> alertMessageCountRuleRecord)
                                    throws Exception {

                                List<StreamAlertMessageCountRuleList> streamAlertRuleList = new ArrayList<StreamAlertMessageCountRuleList>();

                                if (alertMessageCountRuleRecord.isPresent()) {
                                    streamAlertRuleList = alertMessageCountRuleRecord.get()
                                            .getStreamAlertMessageCountRuleList();

                                }

                                streamAlertRuleList.add(streamAlertMessageCountRuleList);
                                tx.put(LogicalDatastoreType.OPERATIONAL,
                                        alertMessageCountRuleRecordId,
                                        new AlertMessageCountRuleRecordBuilder().setStreamAlertMessageCountRuleList(
                                                streamAlertRuleList).build());
                                return tx.submit();

                            }
                        });
                Futures.addCallback(commitFuture, new FutureCallback<Void>() {
                    @Override
                    public void onSuccess(final Void result) {
                        LOG.debug("Rule commited sucessfully to operational datastore on data change");

                    }

                    @Override
                    public void onFailure(final Throwable ex) {

                        LOG.debug("Failed to commit Rule to operational datastore on data change", ex);

                    }
                });

            }

            LOG.info("onDataChanged - new Centinel config: {}", record);
        }

        else if (dataObject instanceof AlertFieldValueRuleRecord) {

            AlertFieldValueRuleRecord record = (AlertFieldValueRuleRecord) dataObject;
            Iterator<DataObject> iterator = change.getCreatedData().values().iterator();
            if (iterator.hasNext()) {

                StreamAlertFieldValueRuleList tempStreamAlertFieldValueRuleList = null;
                DataObject fieldValueObject = iterator.next();
                if (!(fieldValueObject instanceof StreamAlertFieldValueRuleList)) {
                    tempStreamAlertFieldValueRuleList = (StreamAlertFieldValueRuleList) record
                            .getStreamAlertFieldValueRuleList().get(0);
                } else if (fieldValueObject instanceof StreamAlertFieldValueRuleList) {
                    tempStreamAlertFieldValueRuleList = (StreamAlertFieldValueRuleList) fieldValueObject;
                }

                final StreamAlertFieldValueRuleList streamAlertFieldValueRuleList = tempStreamAlertFieldValueRuleList;
                LOG.info("Alert Field value rule list: " + streamAlertFieldValueRuleList);
                ListenableFuture<Optional<AlertFieldValueRuleRecord>> readFuture = tx.read(
                        LogicalDatastoreType.OPERATIONAL, alertFieldValueRuleRecordId);

                final ListenableFuture<Void> commitFuture = Futures.transform(readFuture,
                        new AsyncFunction<Optional<AlertFieldValueRuleRecord>, Void>() {

                            @Override
                            public ListenableFuture<Void> apply(
                                    final Optional<AlertFieldValueRuleRecord> alertFieldValueRuleRecord)
                                    throws Exception {

                                List<StreamAlertFieldValueRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldValueRuleList>();

                                if (alertFieldValueRuleRecord.isPresent()) {
                                    streamAlertRuleList = alertFieldValueRuleRecord.get()
                                            .getStreamAlertFieldValueRuleList();

                                }
                                streamAlertRuleList.add(streamAlertFieldValueRuleList);
                                tx.put(LogicalDatastoreType.OPERATIONAL,
                                        alertFieldValueRuleRecordId,
                                        new AlertFieldValueRuleRecordBuilder().setStreamAlertFieldValueRuleList(
                                                streamAlertRuleList).build());
                                return tx.submit();

                            }
                        });
                Futures.addCallback(commitFuture, new FutureCallback<Void>() {
                    @Override
                    public void onSuccess(final Void result) {
                        LOG.debug("Rule commited sucessfully to operational datastore on data change");

                    }

                    @Override
                    public void onFailure(final Throwable ex) {

                        LOG.debug("Failed to commit Rule to operational datastore on data change", ex);

                    }
                });

            }

        }

        else if (dataObject instanceof AlertFieldContentRuleRecord) {
            AlertFieldContentRuleRecord record = (AlertFieldContentRuleRecord) dataObject;
            Iterator<DataObject> iterator = change.getCreatedData().values().iterator();
            if (iterator.hasNext()) {

                StreamAlertFieldContentRuleList tempStreamAlertFieldContentRuleList = null;
                DataObject fieldContentObject = iterator.next();
                if (!(fieldContentObject instanceof StreamAlertFieldContentRuleList)) {
                    tempStreamAlertFieldContentRuleList = (StreamAlertFieldContentRuleList) record
                            .getStreamAlertFieldContentRuleList().get(0);
                } else if (fieldContentObject instanceof StreamAlertFieldContentRuleList) {
                    tempStreamAlertFieldContentRuleList = (StreamAlertFieldContentRuleList) fieldContentObject;
                }

                final StreamAlertFieldContentRuleList streamAlertFieldContentRuleList = tempStreamAlertFieldContentRuleList;
                LOG.info("Alert Field content rule list: " + streamAlertFieldContentRuleList);
                ListenableFuture<Optional<AlertFieldContentRuleRecord>> readFuture = tx.read(
                        LogicalDatastoreType.OPERATIONAL, alertFeildContentRuleRecordId);

                final ListenableFuture<Void> commitFuture = Futures.transform(readFuture,
                        new AsyncFunction<Optional<AlertFieldContentRuleRecord>, Void>() {

                            @Override
                            public ListenableFuture<Void> apply(
                                    final Optional<AlertFieldContentRuleRecord> alertFieldContentRuleRecord)
                                    throws Exception {

                                List<StreamAlertFieldContentRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldContentRuleList>();

                                if (alertFieldContentRuleRecord.isPresent()) {
                                    streamAlertRuleList = alertFieldContentRuleRecord.get()
                                            .getStreamAlertFieldContentRuleList();

                                }
                                streamAlertRuleList.add(streamAlertFieldContentRuleList);

                                tx.put(LogicalDatastoreType.OPERATIONAL,
                                        alertFeildContentRuleRecordId,
                                        new AlertFieldContentRuleRecordBuilder().setStreamAlertFieldContentRuleList(
                                                streamAlertRuleList).build());
                                return tx.submit();

                            }
                        });
                Futures.addCallback(commitFuture, new FutureCallback<Void>() {
                    @Override
                    public void onSuccess(final Void result) {
                        LOG.debug("Rule commited sucessfully to operational datastore on data change");

                    }

                    @Override
                    public void onFailure(final Throwable ex) {

                        LOG.debug("Failed to commit Rule to operational datastore on data change", ex);

                    }
                });

            }
        }

    }

}
