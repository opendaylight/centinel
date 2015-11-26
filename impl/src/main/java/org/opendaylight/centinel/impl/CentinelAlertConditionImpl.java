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
import org.opendaylight.controller.md.sal.binding.api.NotificationService;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldContentRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldContentRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldValueRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldValueRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertMessageCountRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertMessageCountRuleRecordBuilder;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertmessagecountrulerecord.StreamAlertMessageCountRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertmessagecountrulerecord.StreamAlertMessageCountRuleListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.get.all.alert.rule.output.StreamAlertFieldContentRuleListSorted;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.get.all.alert.rule.output.StreamAlertFieldContentRuleListSortedBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.get.all.alert.rule.output.StreamAlertFieldValueRuleListSorted;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.get.all.alert.rule.output.StreamAlertFieldValueRuleListSortedBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.get.all.alert.rule.output.StreamAlertMessageCountRuleListSorted;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.get.all.alert.rule.output.StreamAlertMessageCountRuleListSortedBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorType;
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

public class CentinelAlertConditionImpl implements AlertruleService, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(CentinelAlertConditionImpl.class);

    public static final InstanceIdentifier<AlertMessageCountRuleRecord> alertMessageCountRuleRecordId = InstanceIdentifier
            .builder(AlertMessageCountRuleRecord.class).build();
    public static final InstanceIdentifier<AlertFieldValueRuleRecord> alertFieldValueRuleRecordId = InstanceIdentifier
            .builder(AlertFieldValueRuleRecord.class).build();
    public static final InstanceIdentifier<AlertFieldContentRuleRecord> alertFeildContentRuleRecordId = InstanceIdentifier
            .builder(AlertFieldContentRuleRecord.class).build();

    private NotificationService notificationProvider;
    private DataBroker dataProvider;
    private final ExecutorService executor;

    public CentinelAlertConditionImpl() {
        executor = Executors.newFixedThreadPool(1);
    }

    public void setNotificationProvider(final NotificationService salService) {
        LOG.info(" Entered to Notification ");
        this.notificationProvider = salService;
        LOG.info("notifictaion provider set");
    }

    StreamAlertMessageCountRuleList buildAlertMessageCountRuleRecord(final SetAlertMessageCountRuleInput input,
            String configId) {

        return new StreamAlertMessageCountRuleListBuilder().setAlertTypeClassifier(input.getAlertTypeClassifier())
                .setMessageCountBacklog(input.getMessageCountBacklog())
                .setMessageCountCount(input.getMessageCountCount()).setMessageCountGrace(input.getMessageCountGrace())
                .setMessageCountOperator(input.getMessageCountOperator()).setTimeStamp(input.getTimeStamp())
                .setNodeType(input.getNodeType()).setRuleID(input.getRuleID()).setConfigID(configId)
                .setAlertName(input.getAlertName()).setRuleTypeClassifier(input.getRuleTypeClassifier())
                .setStreamID(input.getStreamID()).build();

    }

    StreamAlertFieldValueRuleList buildAlertFieldValueRuleRecord(final SetAlertFieldValueRuleInput input,
            String configId) {

        return new StreamAlertFieldValueRuleListBuilder().setAlertTypeClassifier(input.getAlertTypeClassifier())
                .setFieldValueBacklog(input.getFieldValueBacklog()).setFieldValueField(input.getFieldValueField())
                .setFieldValueGrace(input.getFieldValueGrace())
                .setFieldValueThreshhold(input.getFieldValueThreshhold())
                .setFieldValueThreshholdType(input.getFieldValueThreshholdType())
                .setFieldValueType(input.getFieldValueType()).setTimeStamp(input.getTimeStamp())
                .setNodeType(input.getNodeType()).setRuleID(input.getRuleID()).setConfigID(configId)
                .setAlertName(input.getAlertName()).setRuleTypeClassifier(input.getRuleTypeClassifier())
                .setStreamID(input.getStreamID()).build();

    }

    StreamAlertFieldContentRuleList buildAlertFieldContentRuleRecord(final SetAlertFieldContentRuleInput input,
            String configId) {

        return new StreamAlertFieldContentRuleListBuilder().setAlertTypeClassifier(input.getAlertTypeClassifier())
                .setFieldContentBacklog(input.getFieldContentBacklog()).setNodeType(input.getNodeType())
                .setFieldContentCompareToValue(input.getFieldContentCompareToValue())
                .setFieldContentField(input.getFieldContentField()).setFieldContentGrace(input.getFieldContentGrace())
                .setTimeStamp(input.getTimeStamp()).setNodeType(input.getNodeType()).setRuleID(input.getRuleID())
                .setConfigID(configId).setAlertName(input.getAlertName())
                .setRuleTypeClassifier(input.getRuleTypeClassifier()).setStreamID(input.getStreamID()).build();

    }

    StreamAlertMessageCountRuleList buildUpdateAlertMessageCountRuleRecord(
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
            streamAlertMessageCountRuleListBuilder.setMessageCountGrace(input.getMessageCountGrace());
        else
            streamAlertMessageCountRuleListBuilder.setMessageCountGrace(obj.getMessageCountGrace());

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

        if (input.getAlertName() != null)
            streamAlertMessageCountRuleListBuilder.setAlertName(input.getAlertName());
        else
            streamAlertMessageCountRuleListBuilder.setAlertName(obj.getAlertName());

        if (input.getNodeType() != null)
            streamAlertMessageCountRuleListBuilder.setNodeType(input.getNodeType());
        else
            streamAlertMessageCountRuleListBuilder.setNodeType(obj.getNodeType());

        streamAlertMessageCountRuleListBuilder.setConfigID(obj.getConfigID());

        return streamAlertMessageCountRuleListBuilder.build();
    }

    StreamAlertFieldContentRuleList buildUpdateAlertFieldContentRuleRecord(
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

        if (input.getAlertName() != null)
            streamAlertFieldContentRuleListBuilder.setAlertName(input.getAlertName());
        else
            streamAlertFieldContentRuleListBuilder.setAlertName(obj.getAlertName());

        if (input.getNodeType() != null)
            streamAlertFieldContentRuleListBuilder.setNodeType(input.getNodeType());
        else
            streamAlertFieldContentRuleListBuilder.setNodeType(obj.getNodeType());

        return streamAlertFieldContentRuleListBuilder.build();
    }

    StreamAlertFieldValueRuleList buildUpdateAlertFieldValueRuleRecord(final UpdateAlertFieldValueRuleInput input,
            final StreamAlertFieldValueRuleList obj) {

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

        if (input.getAlertName() != null)
            streamAlertFieldValueRuleListBuilder.setAlertName(input.getAlertName());
        else
            streamAlertFieldValueRuleListBuilder.setAlertName(obj.getAlertName());

        if (input.getNodeType() != null)
            streamAlertFieldValueRuleListBuilder.setNodeType(input.getNodeType());
        else
            streamAlertFieldValueRuleListBuilder.setNodeType(obj.getNodeType());

        return streamAlertFieldValueRuleListBuilder.build();
    }

    public void setDataProvider(final DataBroker salDataProvider) {
        LOG.info(" Entered to Data Provider");
        this.dataProvider = salDataProvider;
        LOG.info("data provider set");
    }

    @Override
    public void close() throws Exception {
        // When we close this service we need to shutdown our executor!
        executor.shutdown();

        if (dataProvider != null) {
            WriteTransaction tx = dataProvider.newWriteOnlyTransaction();
            LOG.info(" Transaction written");
            tx.delete(LogicalDatastoreType.CONFIGURATION, alertMessageCountRuleRecordId);

        }

    }

    /*
     * sets alert message count rule in configurational data store
     */
    @Override
    public Future<RpcResult<SetAlertMessageCountRuleOutput>> setAlertMessageCountRule(
            final SetAlertMessageCountRuleInput input) {

        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<SetAlertMessageCountRuleOutput>> futureResult = SettableFuture.create();
        final String configId = generateConfigId();
        LOG.info("setAlertMessageCountRule: " + input);
        final SetAlertMessageCountRuleOutputBuilder setAlertMessageCountRuleOutputBuilder = new SetAlertMessageCountRuleOutputBuilder();
        setAlertMessageCountRuleOutputBuilder.setMessageCountOperator(input.getMessageCountOperator());
        setAlertMessageCountRuleOutputBuilder.setMessageCountCount(input.getMessageCountCount());
        setAlertMessageCountRuleOutputBuilder.setMessageCountGrace(input.getMessageCountGrace());
        setAlertMessageCountRuleOutputBuilder.setMessageCountBacklog(input.getMessageCountBacklog());
        setAlertMessageCountRuleOutputBuilder.setRuleID(input.getRuleID());
        setAlertMessageCountRuleOutputBuilder.setNodeType(input.getNodeType());
        setAlertMessageCountRuleOutputBuilder.setRuleTypeClassifier(input.getRuleTypeClassifier());
        setAlertMessageCountRuleOutputBuilder.setStreamID(input.getStreamID());
        setAlertMessageCountRuleOutputBuilder.setTimeStamp(input.getTimeStamp());
        setAlertMessageCountRuleOutputBuilder.setAlertTypeClassifier(input.getAlertTypeClassifier());
        setAlertMessageCountRuleOutputBuilder.setAlertName(input.getAlertName());
        setAlertMessageCountRuleOutputBuilder.setConfigID(configId);

        List<StreamAlertMessageCountRuleList> streamAlertRuleList = new ArrayList<StreamAlertMessageCountRuleList>();
        if (input.getAlertTypeClassifier() == null || input.getMessageCountCount() != null
                && input.getMessageCountCount() <= 0 || input.getMessageCountCount() == null
                || input.getMessageCountOperator() == null || input.getMessageCountOperator().isEmpty()
                || input.getMessageCountOperator().trim().isEmpty() || input.getTimeStamp() != null
                && input.getTimeStamp() <= 0 || input.getTimeStamp() == null || input.getMessageCountBacklog() == null
                || input.getMessageCountBacklog() != null && input.getMessageCountBacklog() <= 0
                || input.getMessageCountGrace() == null || input.getMessageCountGrace() != null
                && input.getMessageCountGrace() <= 0) {
            LOG.debug("Invalid Parameters for SetAlertMessageCountRule ");
            return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("inalid-input",
                    RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                            "StreamId,Classifier,Count,Operator,Grace,Backlog and Timestamp are mandatory fields")));

        }
        streamAlertRuleList.add(buildAlertMessageCountRuleRecord(input, configId));

        try {
            tx.merge(LogicalDatastoreType.CONFIGURATION, alertMessageCountRuleRecordId,
                    new AlertMessageCountRuleRecordBuilder().setStreamAlertMessageCountRuleList(streamAlertRuleList)
                            .build(), true);
            tx.submit();
            futureResult.set(RpcResultBuilder.<SetAlertMessageCountRuleOutput> success(
                    setAlertMessageCountRuleOutputBuilder.build()).build());
        }

        catch (Exception e) {
            LOG.info("Failed to commit Rule", e);
            futureResult.set(RpcResultBuilder.<SetAlertMessageCountRuleOutput> failed()
                    .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
        }

        return futureResult;
    }

    public String generateConfigId() {
        final String configId = String.format("%x", (int) (Math.random() * 10000));
        return configId;
    }

    /*
     * sets alert field content rule in configurational datastore
     */
    @Override
    public Future<RpcResult<SetAlertFieldContentRuleOutput>> setAlertFieldContentRule(
            final SetAlertFieldContentRuleInput input) {
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<SetAlertFieldContentRuleOutput>> futureResult = SettableFuture.create();
        final String configId = generateConfigId();
        final SetAlertFieldContentRuleOutputBuilder setAlertFieldContentRuleOutputBuilder = new SetAlertFieldContentRuleOutputBuilder();
        setAlertFieldContentRuleOutputBuilder.setAlertTypeClassifier(input.getAlertTypeClassifier());
        setAlertFieldContentRuleOutputBuilder.setFieldContentBacklog(input.getFieldContentBacklog());
        setAlertFieldContentRuleOutputBuilder.setFieldContentCompareToValue(input.getFieldContentCompareToValue());
        setAlertFieldContentRuleOutputBuilder.setFieldContentField(input.getFieldContentField());
        setAlertFieldContentRuleOutputBuilder.setFieldContentGrace(input.getFieldContentGrace());
        setAlertFieldContentRuleOutputBuilder.setNodeType(input.getNodeType());
        setAlertFieldContentRuleOutputBuilder.setRuleID(input.getRuleID());
        setAlertFieldContentRuleOutputBuilder.setStreamID(input.getStreamID());
        setAlertFieldContentRuleOutputBuilder.setRuleTypeClassifier(input.getRuleTypeClassifier());
        setAlertFieldContentRuleOutputBuilder.setTimeStamp(input.getTimeStamp());
        setAlertFieldContentRuleOutputBuilder.setAlertName(input.getAlertName());
        setAlertFieldContentRuleOutputBuilder.setConfigID(configId);

        List<StreamAlertFieldContentRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldContentRuleList>();
        if (input.getAlertTypeClassifier() == null || input.getFieldContentField() == null
                || input.getFieldContentField().isEmpty() || input.getFieldContentField().trim().isEmpty()
                || input.getFieldContentBacklog() == null || input.getFieldContentBacklog() != null
                && input.getFieldContentBacklog() <= 0 || input.getFieldContentGrace() == null
                || input.getFieldContentGrace() != null && input.getFieldContentGrace() <= 0
                || input.getFieldContentCompareToValue() == null || input.getFieldContentCompareToValue().isEmpty()
                || input.getFieldContentCompareToValue().trim().isEmpty()) {
            LOG.debug("Invalid Parameters for SetAlertFieldContentRule ");
            return Futures
                    .immediateFailedCheckedFuture(new TransactionCommitFailedException(
                            "inalid-input",
                            RpcResultBuilder
                                    .newError(
                                            ErrorType.APPLICATION,
                                            "invalid-input",
                                            "StreamId,AlertTypeClassifier,FieldContentField,FieldContentGrace,FieldContentBacklog and FieldContentValue are mandatory fields")));
        }
        streamAlertRuleList.add(buildAlertFieldContentRuleRecord(input, configId));

        try {
            tx.merge(LogicalDatastoreType.CONFIGURATION, alertFeildContentRuleRecordId,
                    new AlertFieldContentRuleRecordBuilder().setStreamAlertFieldContentRuleList(streamAlertRuleList)
                            .build(), true);
            tx.submit();
            futureResult.set(RpcResultBuilder.<SetAlertFieldContentRuleOutput> success(
                    setAlertFieldContentRuleOutputBuilder.build()).build());
        }

        catch (Exception e) {
            LOG.info("Failed to commit Rule", e);
            futureResult.set(RpcResultBuilder.<SetAlertFieldContentRuleOutput> failed()
                    .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
        }

        return futureResult;
    }

    /*
     * sets sets alertfield value rule in configurational data store.
     */
    @Override
    public Future<RpcResult<SetAlertFieldValueRuleOutput>> setAlertFieldValueRule(
            final SetAlertFieldValueRuleInput input) {

        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<SetAlertFieldValueRuleOutput>> futureResult = SettableFuture.create();
        final String configId = generateConfigId();
        final SetAlertFieldValueRuleOutputBuilder setAlertFieldValueRuleOutputBuilder = new SetAlertFieldValueRuleOutputBuilder();

        setAlertFieldValueRuleOutputBuilder.setAlertTypeClassifier(input.getAlertTypeClassifier());
        setAlertFieldValueRuleOutputBuilder.setFieldValueBacklog(input.getFieldValueBacklog());
        setAlertFieldValueRuleOutputBuilder.setFieldValueField(input.getFieldValueField());
        setAlertFieldValueRuleOutputBuilder.setFieldValueGrace(input.getFieldValueGrace());
        setAlertFieldValueRuleOutputBuilder.setFieldValueThreshhold(input.getFieldValueThreshhold());
        setAlertFieldValueRuleOutputBuilder.setFieldValueThreshholdType(input.getFieldValueThreshholdType());
        setAlertFieldValueRuleOutputBuilder.setFieldValueType(input.getFieldValueType());
        setAlertFieldValueRuleOutputBuilder.setNodeType(input.getNodeType());
        setAlertFieldValueRuleOutputBuilder.setRuleID(input.getRuleID());
        setAlertFieldValueRuleOutputBuilder.setRuleTypeClassifier(input.getRuleTypeClassifier());
        setAlertFieldValueRuleOutputBuilder.setStreamID(input.getStreamID());
        setAlertFieldValueRuleOutputBuilder.setTimeStamp(input.getTimeStamp());
        setAlertFieldValueRuleOutputBuilder.setAlertName(input.getAlertName());
        setAlertFieldValueRuleOutputBuilder.setConfigID(configId);

        List<StreamAlertFieldValueRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldValueRuleList>();
        if (input.getAlertTypeClassifier() == null || input.getFieldValueThreshhold() == null
                || input.getFieldValueThreshhold() != null && input.getFieldValueThreshhold() <= 0
                || input.getFieldValueThreshholdType() == null || input.getFieldValueThreshholdType().isEmpty()
                || input.getFieldValueThreshholdType().trim().isEmpty() || input.getTimeStamp() == null
                || input.getTimeStamp() != null && input.getTimeStamp() <= 0 || input.getFieldValueField() == null
                || input.getFieldValueField().isEmpty() || input.getFieldValueField().trim().isEmpty()
                || input.getFieldValueBacklog() == null || input.getFieldValueBacklog() != null
                && input.getFieldValueBacklog() <= 0 || input.getFieldValueGrace() == null
                || input.getFieldValueGrace() != null && input.getFieldValueGrace() <= 0
                || input.getFieldValueType().isEmpty() || input.getFieldValueType().trim().isEmpty()
                || input.getFieldValueType() == null) {
            LOG.debug("Invalid Parameters for SetAlertFieldValueRule");
            return Futures
                    .immediateFailedCheckedFuture(new TransactionCommitFailedException(
                            "inalid-input",
                            RpcResultBuilder
                                    .newError(
                                            ErrorType.APPLICATION,
                                            "invalid-input",
                                            "StreamId,AlertTypeClassifier,FieldValueField,FieldValueThreshholdType,FieldValueThreshhold,FieldValueGrace,FieldValueBacklog and FieldValueTimestamp ,FieldValueType are mandatory fields")));
        }
        streamAlertRuleList.add(buildAlertFieldValueRuleRecord(input, configId));

        try {
            tx.merge(LogicalDatastoreType.CONFIGURATION, alertFieldValueRuleRecordId,
                    new AlertFieldValueRuleRecordBuilder().setStreamAlertFieldValueRuleList(streamAlertRuleList)
                            .build(), true);
            tx.submit();
            futureResult.set(RpcResultBuilder.<SetAlertFieldValueRuleOutput> success(
                    setAlertFieldValueRuleOutputBuilder.build()).build());
        }

        catch (Exception e) {
            LOG.info("Failed to commit Rule", e);
            futureResult.set(RpcResultBuilder.<SetAlertFieldValueRuleOutput> failed()
                    .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
        }

        return futureResult;
    }

    /*
     * Update Alert Message Count Rule in configurational data store.
     */

    @Override
    public Future<RpcResult<UpdateAlertMessageCountRuleOutput>> updateAlertMessageCountRule(
            final UpdateAlertMessageCountRuleInput input) {
        boolean idMatches = false;
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<UpdateAlertMessageCountRuleOutput>> futureResult = SettableFuture.create();
        if (input.getStreamID() == null || input.getStreamID().isEmpty() || input.getStreamID().trim().isEmpty()
                || input.getRuleID() == null || input.getRuleID().isEmpty() || input.getRuleID().trim().isEmpty()) {
            LOG.debug("Invalid Parameters for UpdateAlertMessageCountRule");
            return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("inalid-input",
                    RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                            "StreamId and RuleId are mandatory fields")));
        }
        LOG.info("updateAlertMessageCountRule: " + input);
        final UpdateAlertMessageCountRuleOutputBuilder updateAlertMessageCountRuleOutputBuilder = new UpdateAlertMessageCountRuleOutputBuilder();
        updateAlertMessageCountRuleOutputBuilder.setMessageCountOperator(input.getMessageCountOperator());
        updateAlertMessageCountRuleOutputBuilder.setMessageCountCount(input.getMessageCountCount());
        updateAlertMessageCountRuleOutputBuilder.setMessageCountGrace(input.getMessageCountGrace());
        updateAlertMessageCountRuleOutputBuilder.setMessageCountBacklog(input.getMessageCountBacklog());
        updateAlertMessageCountRuleOutputBuilder.setRuleID(input.getRuleID());
        updateAlertMessageCountRuleOutputBuilder.setNodeType(input.getNodeType());
        updateAlertMessageCountRuleOutputBuilder.setRuleTypeClassifier(input.getRuleTypeClassifier());
        updateAlertMessageCountRuleOutputBuilder.setStreamID(input.getStreamID());
        updateAlertMessageCountRuleOutputBuilder.setTimeStamp(input.getTimeStamp());
        updateAlertMessageCountRuleOutputBuilder.setAlertTypeClassifier(input.getAlertTypeClassifier());
        updateAlertMessageCountRuleOutputBuilder.setAlertName(input.getAlertName());

        ListenableFuture<Optional<AlertMessageCountRuleRecord>> readFutureOperational = tx.read(
                LogicalDatastoreType.OPERATIONAL, alertMessageCountRuleRecordId);

        ListenableFuture<Optional<AlertMessageCountRuleRecord>> readFutureConfigure = tx.read(
                LogicalDatastoreType.CONFIGURATION, alertMessageCountRuleRecordId);

        String configId = null;

        try {

            Optional<AlertMessageCountRuleRecord> record = readFutureOperational.get();
            if (record.isPresent()) {
                AlertMessageCountRuleRecord operationalRecord = readFutureOperational.get().get();
                List<StreamAlertMessageCountRuleList> streamAlertRuleList = new ArrayList<StreamAlertMessageCountRuleList>();

                if (!operationalRecord.getStreamAlertMessageCountRuleList().isEmpty()) {

                    streamAlertRuleList = operationalRecord.getStreamAlertMessageCountRuleList();
                    Iterator<StreamAlertMessageCountRuleList> itearator = streamAlertRuleList.iterator();

                    StreamAlertMessageCountRuleList operationalObject = null;

                    while (itearator.hasNext()) {

                        operationalObject = itearator.next();
                        if (operationalObject.getRuleID().equalsIgnoreCase(input.getRuleID())
                                && operationalObject.getStreamID().equalsIgnoreCase(input.getStreamID())) {

                            configId = operationalObject.getConfigID();
                            idMatches = true;

                        }
                    }
                    if (!idMatches) {
                        return Futures
                                .immediateFailedCheckedFuture(new TransactionCommitFailedException(
                                        "invalid-input",
                                        RpcResultBuilder
                                                .newError(ErrorType.APPLICATION, "invalid-input",
                                                        "Invalid Stream/Rule id or The stream/Rule is not present in operational data store")));

                    }

                }

            } else {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                "Record is not present in operational data store")));
            }
        } catch (InterruptedException | ExecutionException e) {

            futureResult.set(RpcResultBuilder.<UpdateAlertMessageCountRuleOutput> failed()
                    .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
            return futureResult;

        }
        final String configID = configId;

        final ListenableFuture<Void> commitFuture = Futures.transform(readFutureConfigure,
                new AsyncFunction<Optional<AlertMessageCountRuleRecord>, Void>() {

                    @Override
                    public ListenableFuture<Void> apply(
                            final Optional<AlertMessageCountRuleRecord> alertMessageCountRuleRecord) throws Exception {

                        List<StreamAlertMessageCountRuleList> streamAlertRuleList = new ArrayList<StreamAlertMessageCountRuleList>();
                        List<StreamAlertMessageCountRuleList> updatedStreamAlertRuleList = new ArrayList<StreamAlertMessageCountRuleList>();

                        if (alertMessageCountRuleRecord.isPresent()) {

                            streamAlertRuleList = alertMessageCountRuleRecord.get()
                                    .getStreamAlertMessageCountRuleList();
                            StreamAlertMessageCountRuleList configObject = null;
                            Iterator<StreamAlertMessageCountRuleList> iterator = streamAlertRuleList.iterator();

                            while (iterator.hasNext()) {

                                configObject = iterator.next();

                                if (configObject.getConfigID().equalsIgnoreCase(configID)) {

                                    updatedStreamAlertRuleList.add(buildUpdateAlertMessageCountRuleRecord(input,
                                            configObject));
                                    tx.merge(LogicalDatastoreType.CONFIGURATION, alertMessageCountRuleRecordId,
                                            new AlertMessageCountRuleRecordBuilder()
                                                    .setStreamAlertMessageCountRuleList(updatedStreamAlertRuleList)
                                                    .build());

                                }
                            }

                        }

                        return tx.submit();
                    }

                });
        Futures.addCallback(commitFuture, new FutureCallback<Void>() {
            @Override
            public void onSuccess(final Void result) {

                futureResult.set(RpcResultBuilder.<UpdateAlertMessageCountRuleOutput> success(
                        updateAlertMessageCountRuleOutputBuilder.build()).build());
            }

            @Override
            public void onFailure(final Throwable ex) {

                LOG.info("Failed to commit Rule", ex);

                futureResult.set(RpcResultBuilder.<UpdateAlertMessageCountRuleOutput> failed()
                        .withRpcErrors(((TransactionCommitFailedException) ex).getErrorList()).build());

            }
        });

        return futureResult;
    }

    /*
     * Update Alert Field Content Rule in configurational data store.
     */

    @Override
    public Future<RpcResult<UpdateAlertFieldContentRuleOutput>> updateAlertFieldContentRule(
            final UpdateAlertFieldContentRuleInput input) {
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<UpdateAlertFieldContentRuleOutput>> futureResult = SettableFuture.create();
        boolean idMatches = false;
        if (input.getStreamID() == null || input.getStreamID().isEmpty() || input.getStreamID().trim().isEmpty()
                || input.getRuleID() == null || input.getRuleID().isEmpty() || input.getRuleID().trim().isEmpty()) {
            LOG.debug("Invalid Parameters for UpdateAlertFieldContentRule");
            return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("inalid-input",
                    RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                            "StreamId and RuleId are mandatory fields")));
        }
        LOG.info("updateAlertFieldContentRule: " + input);
        final UpdateAlertFieldContentRuleOutputBuilder updateAlertFieldContentRuleOutputBuilder = new UpdateAlertFieldContentRuleOutputBuilder();
        updateAlertFieldContentRuleOutputBuilder.setFieldContentBacklog(input.getFieldContentBacklog());
        updateAlertFieldContentRuleOutputBuilder.setFieldContentCompareToValue(input.getFieldContentCompareToValue());
        updateAlertFieldContentRuleOutputBuilder.setFieldContentField(input.getFieldContentField());
        updateAlertFieldContentRuleOutputBuilder.setFieldContentGrace(input.getFieldContentGrace());
        updateAlertFieldContentRuleOutputBuilder.setRuleID(input.getRuleID());
        updateAlertFieldContentRuleOutputBuilder.setNodeType(input.getNodeType());
        updateAlertFieldContentRuleOutputBuilder.setRuleTypeClassifier(input.getRuleTypeClassifier());
        updateAlertFieldContentRuleOutputBuilder.setStreamID(input.getStreamID());
        updateAlertFieldContentRuleOutputBuilder.setTimeStamp(input.getTimeStamp());
        updateAlertFieldContentRuleOutputBuilder.setAlertTypeClassifier(input.getAlertTypeClassifier());
        updateAlertFieldContentRuleOutputBuilder.setAlertName(input.getAlertName());

        ListenableFuture<Optional<AlertFieldContentRuleRecord>> readFutureOperational = tx.read(
                LogicalDatastoreType.OPERATIONAL, alertFeildContentRuleRecordId);

        ListenableFuture<Optional<AlertFieldContentRuleRecord>> readFutureConfigure = tx.read(
                LogicalDatastoreType.CONFIGURATION, alertFeildContentRuleRecordId);

        String configId = null;

        try {
            Optional<AlertFieldContentRuleRecord> record = readFutureOperational.get();
            if (record.isPresent()) {
                AlertFieldContentRuleRecord operationalRecord = readFutureOperational.get().get();
                List<StreamAlertFieldContentRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldContentRuleList>();

                if (!operationalRecord.getStreamAlertFieldContentRuleList().isEmpty()) {
                    streamAlertRuleList = operationalRecord.getStreamAlertFieldContentRuleList();
                    Iterator<StreamAlertFieldContentRuleList> itearator = streamAlertRuleList.iterator();
                    StreamAlertFieldContentRuleList operationalObject = null;

                    while (itearator.hasNext()) {
                        operationalObject = itearator.next();
                        if (operationalObject.getRuleID().equalsIgnoreCase(input.getRuleID())
                                && operationalObject.getStreamID().equalsIgnoreCase(input.getStreamID())) {
                            configId = operationalObject.getConfigID();
                            idMatches = true;

                        }
                    }

                    if (!idMatches) {
                        return Futures
                                .immediateFailedCheckedFuture(new TransactionCommitFailedException(
                                        "invalid-input",
                                        RpcResultBuilder
                                                .newError(ErrorType.APPLICATION, "invalid-input",
                                                        "Invalid Stream/Rule id or The stream/rule is not present in operational data store")));

                    }

                }

            } else {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                "Record is not present in operational data store")));
            }

        } catch (InterruptedException | ExecutionException e) {
            futureResult.set(RpcResultBuilder.<UpdateAlertFieldContentRuleOutput> failed()
                    .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
            return futureResult;

        }
        final String configID = configId;
        final ListenableFuture<Void> commitFuture = Futures.transform(readFutureConfigure,
                new AsyncFunction<Optional<AlertFieldContentRuleRecord>, Void>() {

                    @Override
                    public ListenableFuture<Void> apply(
                            final Optional<AlertFieldContentRuleRecord> alertFieldContentRuleRecord) throws Exception {

                        List<StreamAlertFieldContentRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldContentRuleList>();
                        List<StreamAlertFieldContentRuleList> updatedStreamAlertRuleList = new ArrayList<StreamAlertFieldContentRuleList>();
                        if (alertFieldContentRuleRecord.isPresent()) {
                            streamAlertRuleList = alertFieldContentRuleRecord.get()
                                    .getStreamAlertFieldContentRuleList();
                            StreamAlertFieldContentRuleList configObject = null;
                            Iterator<StreamAlertFieldContentRuleList> iterator = streamAlertRuleList.iterator();
                            while (iterator.hasNext()) {
                                configObject = iterator.next();

                                if (configObject.getConfigID().equalsIgnoreCase(configID)) {
                                    updatedStreamAlertRuleList.add(buildUpdateAlertFieldContentRuleRecord(input,
                                            configObject));
                                    tx.merge(LogicalDatastoreType.CONFIGURATION, alertFeildContentRuleRecordId,
                                            new AlertFieldContentRuleRecordBuilder()
                                                    .setStreamAlertFieldContentRuleList(updatedStreamAlertRuleList)
                                                    .build());

                                }

                            }

                        }

                        return tx.submit();
                    }

                });
        Futures.addCallback(commitFuture, new FutureCallback<Void>() {
            @Override
            public void onSuccess(final Void result) {

                futureResult.set(RpcResultBuilder.<UpdateAlertFieldContentRuleOutput> success(
                        updateAlertFieldContentRuleOutputBuilder.build()).build());
            }

            @Override
            public void onFailure(final Throwable ex) {

                LOG.info("Failed to commit Rule", ex);

                futureResult.set(RpcResultBuilder.<UpdateAlertFieldContentRuleOutput> failed()
                        .withRpcErrors(((TransactionCommitFailedException) ex).getErrorList()).build());

            }
        });

        return futureResult;
    }

    /*
     * Update Alert Field Value Rule in configurational data store.
     */

    @Override
    public Future<RpcResult<UpdateAlertFieldValueRuleOutput>> updateAlertFieldValueRule(
            final UpdateAlertFieldValueRuleInput input) {
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<UpdateAlertFieldValueRuleOutput>> futureResult = SettableFuture.create();
        boolean idMatches = false;
        if (input.getStreamID() == null || input.getStreamID().isEmpty() || input.getStreamID().trim().isEmpty()
                || input.getRuleID() == null || input.getRuleID().isEmpty() || input.getRuleID().trim().isEmpty()) {
            LOG.debug("Invalid Parameters for UpdateAlertFieldValueRule");
            return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("inalid-input",
                    RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                            "StreamId and RuleId are mandatory fields")));
        }
        LOG.info("updateAlertFieldValueRuleOutput: " + input);
        final UpdateAlertFieldValueRuleOutputBuilder updateAlertFieldValueRuleOutputBuilder = new UpdateAlertFieldValueRuleOutputBuilder();
        updateAlertFieldValueRuleOutputBuilder.setFieldValueBacklog(input.getFieldValueBacklog());
        updateAlertFieldValueRuleOutputBuilder.setFieldValueField(input.getFieldValueField());
        updateAlertFieldValueRuleOutputBuilder.setFieldValueGrace(input.getFieldValueGrace());
        updateAlertFieldValueRuleOutputBuilder.setFieldValueThreshhold(input.getFieldValueThreshhold());
        updateAlertFieldValueRuleOutputBuilder.setFieldValueThreshholdType(input.getFieldValueThreshholdType());
        updateAlertFieldValueRuleOutputBuilder.setFieldValueThreshholdType(input.getFieldValueThreshholdType());
        updateAlertFieldValueRuleOutputBuilder.setRuleID(input.getRuleID());
        updateAlertFieldValueRuleOutputBuilder.setNodeType(input.getNodeType());
        updateAlertFieldValueRuleOutputBuilder.setRuleTypeClassifier(input.getRuleTypeClassifier());
        updateAlertFieldValueRuleOutputBuilder.setStreamID(input.getStreamID());
        updateAlertFieldValueRuleOutputBuilder.setTimeStamp(input.getTimeStamp());
        updateAlertFieldValueRuleOutputBuilder.setAlertTypeClassifier(input.getAlertTypeClassifier());
        updateAlertFieldValueRuleOutputBuilder.setAlertName(input.getAlertName());

        ListenableFuture<Optional<AlertFieldValueRuleRecord>> readFutureOperational = tx.read(
                LogicalDatastoreType.OPERATIONAL, alertFieldValueRuleRecordId);

        ListenableFuture<Optional<AlertFieldValueRuleRecord>> readFutureConfigure = tx.read(
                LogicalDatastoreType.CONFIGURATION, alertFieldValueRuleRecordId);

        String configId = null;

        try {
            Optional<AlertFieldValueRuleRecord> record = readFutureOperational.get();
            if (record.isPresent()) {
                AlertFieldValueRuleRecord operationalRecord = readFutureOperational.get().get();
                List<StreamAlertFieldValueRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldValueRuleList>();

                if (!operationalRecord.getStreamAlertFieldValueRuleList().isEmpty()) {
                    streamAlertRuleList = operationalRecord.getStreamAlertFieldValueRuleList();
                    Iterator<StreamAlertFieldValueRuleList> itearator = streamAlertRuleList.iterator();
                    StreamAlertFieldValueRuleList operationalObject = null;

                    while (itearator.hasNext()) {
                        operationalObject = itearator.next();
                        if (operationalObject.getRuleID().equalsIgnoreCase(input.getRuleID())
                                && operationalObject.getStreamID().equalsIgnoreCase(input.getStreamID())) {
                            configId = operationalObject.getConfigID();
                            idMatches = true;

                        }
                    }
                    if (!idMatches) {
                        return Futures
                                .immediateFailedCheckedFuture(new TransactionCommitFailedException(
                                        "invalid-input",
                                        RpcResultBuilder
                                                .newError(ErrorType.APPLICATION, "invalid-input",
                                                        "Invalid Stream/Rule id or The stream/rule is not present in operational data store")));

                    }

                }

            } else {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                "Record is not present in operational data store")));
            }
        } catch (InterruptedException | ExecutionException e) {
            futureResult.set(RpcResultBuilder.<UpdateAlertFieldValueRuleOutput> failed()
                    .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
            return futureResult;

        }
        final String configID = configId;
        final ListenableFuture<Void> commitFuture = Futures.transform(readFutureConfigure,
                new AsyncFunction<Optional<AlertFieldValueRuleRecord>, Void>() {

                    @Override
                    public ListenableFuture<Void> apply(
                            final Optional<AlertFieldValueRuleRecord> alertFieldValueRuleRecord) throws Exception {

                        List<StreamAlertFieldValueRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldValueRuleList>();
                        List<StreamAlertFieldValueRuleList> updatedStreamAlertRuleList = new ArrayList<StreamAlertFieldValueRuleList>();
                        if (alertFieldValueRuleRecord.isPresent()) {
                            streamAlertRuleList = alertFieldValueRuleRecord.get().getStreamAlertFieldValueRuleList();
                            StreamAlertFieldValueRuleList configObject = null;
                            Iterator<StreamAlertFieldValueRuleList> iterator = streamAlertRuleList.iterator();
                            while (iterator.hasNext()) {
                                configObject = iterator.next();

                                if (configObject.getConfigID().equalsIgnoreCase(configID)) {
                                    updatedStreamAlertRuleList.add(buildUpdateAlertFieldValueRuleRecord(input,
                                            configObject));

                                    tx.merge(
                                            LogicalDatastoreType.CONFIGURATION,
                                            alertFieldValueRuleRecordId,
                                            new AlertFieldValueRuleRecordBuilder().setStreamAlertFieldValueRuleList(
                                                    updatedStreamAlertRuleList).build());

                                }
                            }

                        }

                        return tx.submit();
                    }

                });
        Futures.addCallback(commitFuture, new FutureCallback<Void>() {

            @Override
            public void onSuccess(final Void result) {

                futureResult.set(RpcResultBuilder.<UpdateAlertFieldValueRuleOutput> success(
                        updateAlertFieldValueRuleOutputBuilder.build()).build());
            }

            @Override
            public void onFailure(final Throwable ex) {

                LOG.info("Failed to commit Rule", ex);

                futureResult.set(RpcResultBuilder.<UpdateAlertFieldValueRuleOutput> failed()
                        .withRpcErrors(((TransactionCommitFailedException) ex).getErrorList()).build());

            }
        });

        return futureResult;
    }

    /*
     * delete Alert Message Count Rule in configurational data store.
     */

    @Override
    public Future<RpcResult<DeleteAlertMessageCountRuleOutput>> deleteAlertMessageCountRule(
            DeleteAlertMessageCountRuleInput input) {
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<DeleteAlertMessageCountRuleOutput>> futureResult = SettableFuture.create();
        boolean idMatches = false;
        if (input.getStreamID() == null || input.getStreamID().isEmpty() || input.getStreamID().trim().isEmpty()
                || input.getRuleID() == null || input.getRuleID().isEmpty() || input.getRuleID().trim().isEmpty()) {
            LOG.debug("Invalid Parameters for DeleteAlertMessageCountRule");
            return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("inalid-input",
                    RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                            "StreamId and RuleId are mandatory fields")));
        }
        LOG.info("DeleteAlertMessageCountRuleOutput: " + input);
        final DeleteAlertMessageCountRuleOutputBuilder deleteAlertMessageCountRuleOutputBuilder = new DeleteAlertMessageCountRuleOutputBuilder();
        deleteAlertMessageCountRuleOutputBuilder.setMessageCountOperator(input.getMessageCountOperator());
        deleteAlertMessageCountRuleOutputBuilder.setMessageCountCount(input.getMessageCountCount());
        deleteAlertMessageCountRuleOutputBuilder.setMessageCountGrace(input.getMessageCountGrace());
        deleteAlertMessageCountRuleOutputBuilder.setMessageCountBacklog(input.getMessageCountBacklog());
        deleteAlertMessageCountRuleOutputBuilder.setRuleID(input.getRuleID());
        deleteAlertMessageCountRuleOutputBuilder.setNodeType(input.getNodeType());
        deleteAlertMessageCountRuleOutputBuilder.setRuleTypeClassifier(input.getRuleTypeClassifier());
        deleteAlertMessageCountRuleOutputBuilder.setStreamID(input.getStreamID());
        deleteAlertMessageCountRuleOutputBuilder.setTimeStamp(input.getTimeStamp());
        deleteAlertMessageCountRuleOutputBuilder.setAlertTypeClassifier(input.getAlertTypeClassifier());
        deleteAlertMessageCountRuleOutputBuilder.setAlertName(input.getAlertName());
        deleteAlertMessageCountRuleOutputBuilder.setConfigID(input.getConfigID());

        ListenableFuture<Optional<AlertMessageCountRuleRecord>> readFutureOperational = tx.read(
                LogicalDatastoreType.OPERATIONAL, alertMessageCountRuleRecordId);

        ListenableFuture<Optional<AlertMessageCountRuleRecord>> readFutureConfigure = tx.read(
                LogicalDatastoreType.CONFIGURATION, alertMessageCountRuleRecordId);

        String configId = null;

        try {

            Optional<AlertMessageCountRuleRecord> record = readFutureOperational.get();
            if (record.isPresent()) {
                AlertMessageCountRuleRecord operationalRecord = readFutureOperational.get().get();

                List<StreamAlertMessageCountRuleList> streamAlertRuleList = new ArrayList<StreamAlertMessageCountRuleList>();

                if (!operationalRecord.getStreamAlertMessageCountRuleList().isEmpty()) {

                    streamAlertRuleList = operationalRecord.getStreamAlertMessageCountRuleList();
                    Iterator<StreamAlertMessageCountRuleList> itearator = streamAlertRuleList.iterator();

                    StreamAlertMessageCountRuleList operationalObject = null;

                    while (itearator.hasNext()) {

                        operationalObject = itearator.next();
                        if (operationalObject.getRuleID().equalsIgnoreCase(input.getRuleID())
                                && operationalObject.getStreamID().equalsIgnoreCase(input.getStreamID())) {

                            configId = operationalObject.getConfigID();
                            idMatches = true;

                        }
                    }
                    if (!idMatches) {
                        return Futures
                                .immediateFailedCheckedFuture(new TransactionCommitFailedException(
                                        "invalid-input",
                                        RpcResultBuilder
                                                .newError(ErrorType.APPLICATION, "invalid-input",
                                                        "Invalid Stream/Rule id or The stream/rule is not present in operational data store")));

                    }

                }

            } else {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                "Record is not present in operational data store")));
            }
        } catch (InterruptedException | ExecutionException e) {

            futureResult.set(RpcResultBuilder.<DeleteAlertMessageCountRuleOutput> failed()
                    .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
            return futureResult;

        }
        final String configID = configId;
        final ListenableFuture<Void> commitFuture = Futures.transform(readFutureConfigure,
                new AsyncFunction<Optional<AlertMessageCountRuleRecord>, Void>() {

                    @Override
                    public ListenableFuture<Void> apply(
                            final Optional<AlertMessageCountRuleRecord> alertMessageCountRuleRecord) throws Exception {

                        List<StreamAlertMessageCountRuleList> streamAlertRuleList = new ArrayList<StreamAlertMessageCountRuleList>();

                        if (alertMessageCountRuleRecord.isPresent()) {

                            streamAlertRuleList = alertMessageCountRuleRecord.get()
                                    .getStreamAlertMessageCountRuleList();
                            StreamAlertMessageCountRuleList configObject = null;
                            Iterator<StreamAlertMessageCountRuleList> iterator = streamAlertRuleList.iterator();

                            while (iterator.hasNext()) {

                                configObject = iterator.next();

                                if (configObject.getConfigID().equalsIgnoreCase(configID)) {

                                    tx.delete(LogicalDatastoreType.CONFIGURATION, alertMessageCountRuleRecordId.child(
                                            StreamAlertMessageCountRuleList.class, configObject.getKey()));

                                }
                            }

                        }

                        return tx.submit();
                    }

                });
        Futures.addCallback(commitFuture, new FutureCallback<Void>() {
            @Override
            public void onSuccess(final Void result) {

                futureResult.set(RpcResultBuilder.<DeleteAlertMessageCountRuleOutput> success(
                        deleteAlertMessageCountRuleOutputBuilder.build()).build());
            }

            @Override
            public void onFailure(final Throwable ex) {

                LOG.info("Failed to commit Rule", ex);

                futureResult.set(RpcResultBuilder.<DeleteAlertMessageCountRuleOutput> failed()
                        .withRpcErrors(((TransactionCommitFailedException) ex).getErrorList()).build());

            }
        });

        return futureResult;
    }

    /*
     * delete alert field Content rule in configurational data store.
     */

    @Override
    public Future<RpcResult<DeleteAlertFieldContentRuleOutput>> deleteAlertFieldContentRule(
            DeleteAlertFieldContentRuleInput input) {
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<DeleteAlertFieldContentRuleOutput>> futureResult = SettableFuture.create();
        boolean idMatches = false;
        if (input.getStreamID() == null || input.getStreamID().isEmpty() || input.getStreamID().trim().isEmpty()
                || input.getRuleID() == null || input.getRuleID().isEmpty() || input.getRuleID().trim().isEmpty()) {
            LOG.debug("Invalid Parameters for DeleteAlertFieldContentRule");
            return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("inalid-input",
                    RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                            "StreamId and RuleId are mandatory fields")));
        }
        LOG.info("DeleteAlertFieldContentRuleOutput: " + input);
        final DeleteAlertFieldContentRuleOutputBuilder deleteAlertFieldContentRuleOutputBuilder = new DeleteAlertFieldContentRuleOutputBuilder();
        deleteAlertFieldContentRuleOutputBuilder.setFieldContentBacklog(input.getFieldContentBacklog());
        deleteAlertFieldContentRuleOutputBuilder.setFieldContentCompareToValue(input.getFieldContentCompareToValue());
        deleteAlertFieldContentRuleOutputBuilder.setFieldContentField(input.getFieldContentField());
        deleteAlertFieldContentRuleOutputBuilder.setFieldContentGrace(input.getFieldContentGrace());
        deleteAlertFieldContentRuleOutputBuilder.setRuleTypeClassifier(input.getRuleTypeClassifier());
        deleteAlertFieldContentRuleOutputBuilder.setRuleID(input.getRuleID());
        deleteAlertFieldContentRuleOutputBuilder.setNodeType(input.getNodeType());
        deleteAlertFieldContentRuleOutputBuilder.setStreamID(input.getStreamID());
        deleteAlertFieldContentRuleOutputBuilder.setTimeStamp(input.getTimeStamp());
        deleteAlertFieldContentRuleOutputBuilder.setAlertTypeClassifier(input.getAlertTypeClassifier());
        deleteAlertFieldContentRuleOutputBuilder.setAlertName(input.getAlertName());
        deleteAlertFieldContentRuleOutputBuilder.setConfigID(input.getConfigID());

        ListenableFuture<Optional<AlertFieldContentRuleRecord>> readFutureOperational = tx.read(
                LogicalDatastoreType.OPERATIONAL, alertFeildContentRuleRecordId);

        ListenableFuture<Optional<AlertFieldContentRuleRecord>> readFutureConfigure = tx.read(
                LogicalDatastoreType.CONFIGURATION, alertFeildContentRuleRecordId);

        String configId = null;

        try {

            Optional<AlertFieldContentRuleRecord> record = readFutureOperational.get();
            if (record.isPresent()) {
                AlertFieldContentRuleRecord operationalRecord = readFutureOperational.get().get();
                List<StreamAlertFieldContentRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldContentRuleList>();

                if (!operationalRecord.getStreamAlertFieldContentRuleList().isEmpty()) {

                    streamAlertRuleList = operationalRecord.getStreamAlertFieldContentRuleList();
                    Iterator<StreamAlertFieldContentRuleList> itearator = streamAlertRuleList.iterator();

                    StreamAlertFieldContentRuleList operationalObject = null;

                    while (itearator.hasNext()) {

                        operationalObject = itearator.next();
                        if (operationalObject.getRuleID().equalsIgnoreCase(input.getRuleID())
                                && operationalObject.getStreamID().equalsIgnoreCase(input.getStreamID())) {

                            configId = operationalObject.getConfigID();
                            idMatches = true;

                        }
                    }
                    if (!idMatches) {
                        return Futures
                                .immediateFailedCheckedFuture(new TransactionCommitFailedException(
                                        "invalid-input",
                                        RpcResultBuilder
                                                .newError(ErrorType.APPLICATION, "invalid-input",
                                                        "Invalid Stream/Rule id or The stream/rule is not present in operational data store")));

                    }

                }

            } else {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                "Record is not present in operational data store")));
            }
        } catch (InterruptedException | ExecutionException e) {

            futureResult.set(RpcResultBuilder.<DeleteAlertFieldContentRuleOutput> failed()
                    .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
            return futureResult;

        }
        final String configID = configId;
        final ListenableFuture<Void> commitFuture = Futures.transform(readFutureConfigure,
                new AsyncFunction<Optional<AlertFieldContentRuleRecord>, Void>() {

                    @Override
                    public ListenableFuture<Void> apply(
                            final Optional<AlertFieldContentRuleRecord> alertFieldContentRuleRecord) throws Exception {

                        List<StreamAlertFieldContentRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldContentRuleList>();

                        if (alertFieldContentRuleRecord.isPresent()) {

                            streamAlertRuleList = alertFieldContentRuleRecord.get()
                                    .getStreamAlertFieldContentRuleList();
                            StreamAlertFieldContentRuleList configObject = null;
                            Iterator<StreamAlertFieldContentRuleList> iterator = streamAlertRuleList.iterator();

                            while (iterator.hasNext()) {

                                configObject = iterator.next();

                                if (configObject.getConfigID().equalsIgnoreCase(configID)) {

                                    tx.delete(LogicalDatastoreType.CONFIGURATION, alertFeildContentRuleRecordId.child(
                                            StreamAlertFieldContentRuleList.class, configObject.getKey()));

                                }
                            }

                        }

                        return tx.submit();
                    }

                });
        Futures.addCallback(commitFuture, new FutureCallback<Void>() {
            @Override
            public void onSuccess(final Void result) {

                futureResult.set(RpcResultBuilder.<DeleteAlertFieldContentRuleOutput> success(
                        deleteAlertFieldContentRuleOutputBuilder.build()).build());
            }

            @Override
            public void onFailure(final Throwable ex) {

                LOG.info("Failed to commit Rule", ex);

                futureResult.set(RpcResultBuilder.<DeleteAlertFieldContentRuleOutput> failed()
                        .withRpcErrors(((TransactionCommitFailedException) ex).getErrorList()).build());

            }
        });

        return futureResult;
    }

    /*
     * delete alertfield value rule in configurational data store.
     */

    @Override
    public Future<RpcResult<DeleteAlertFieldValueRuleOutput>> deleteAlertFieldValueRule(
            final DeleteAlertFieldValueRuleInput input) {
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<DeleteAlertFieldValueRuleOutput>> futureResult = SettableFuture.create();
        boolean idMatches = false;
        if (input.getStreamID() == null || input.getStreamID().isEmpty() || input.getStreamID().trim().isEmpty()
                || input.getRuleID() == null || input.getRuleID().isEmpty() || input.getRuleID().trim().isEmpty()) {
            LOG.debug("Invalid Parameters for DeleteAlertFieldValueRule");
            return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("inalid-input",
                    RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                            "StreamId and RuleId are mandatory fields")));
        }
        LOG.info("deleteAlertFieldValueRuleOutput: " + input);
        final DeleteAlertFieldValueRuleOutputBuilder deleteAlertFieldValueRuleOutputBuilder = new DeleteAlertFieldValueRuleOutputBuilder();
        deleteAlertFieldValueRuleOutputBuilder.setStreamID("The alert with this" + input.getStreamID() + "is deleted");

        ListenableFuture<Optional<AlertFieldValueRuleRecord>> readFutureOperational = tx.read(
                LogicalDatastoreType.OPERATIONAL, alertFieldValueRuleRecordId);

        ListenableFuture<Optional<AlertFieldValueRuleRecord>> readFutureConfigure = tx.read(
                LogicalDatastoreType.CONFIGURATION, alertFieldValueRuleRecordId);

        String configId = null;

        try {
            Optional<AlertFieldValueRuleRecord> record = readFutureOperational.get();
            if (record.isPresent()) {
                AlertFieldValueRuleRecord operationalRecord = readFutureOperational.get().get();
                List<StreamAlertFieldValueRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldValueRuleList>();

                if (!operationalRecord.getStreamAlertFieldValueRuleList().isEmpty()) {
                    streamAlertRuleList = operationalRecord.getStreamAlertFieldValueRuleList();
                    Iterator<StreamAlertFieldValueRuleList> itearator = streamAlertRuleList.iterator();
                    StreamAlertFieldValueRuleList operationalObject = null;

                    while (itearator.hasNext()) {
                        operationalObject = itearator.next();
                        if (operationalObject.getRuleID().equalsIgnoreCase(input.getRuleID())
                                && operationalObject.getStreamID().equalsIgnoreCase(input.getStreamID())) {
                            configId = operationalObject.getConfigID();
                            idMatches = true;

                        }
                    }
                    if (!idMatches) {
                        return Futures
                                .immediateFailedCheckedFuture(new TransactionCommitFailedException(
                                        "invalid-input",
                                        RpcResultBuilder
                                                .newError(ErrorType.APPLICATION, "invalid-input",
                                                        "Invalid Stream/Rule id or The stream/rule is not present in operational data store")));

                    }

                }

            } else {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                "Record is not present in operational data store")));
            }
        } catch (InterruptedException | ExecutionException e) {
            deleteAlertFieldValueRuleOutputBuilder.setStreamID("The alert with this" + input.getStreamID()
                    + "is not present");
            futureResult.set(RpcResultBuilder.<DeleteAlertFieldValueRuleOutput> failed()
                    .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
            return futureResult;

        }
        final String configID = configId;
        final ListenableFuture<Void> commitFuture = Futures.transform(readFutureConfigure,
                new AsyncFunction<Optional<AlertFieldValueRuleRecord>, Void>() {

                    @Override
                    public ListenableFuture<Void> apply(
                            final Optional<AlertFieldValueRuleRecord> alertFieldValueRuleRecord) throws Exception {

                        List<StreamAlertFieldValueRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldValueRuleList>();

                        if (alertFieldValueRuleRecord.isPresent()) {

                            streamAlertRuleList = alertFieldValueRuleRecord.get().getStreamAlertFieldValueRuleList();
                            StreamAlertFieldValueRuleList configObject = null;
                            Iterator<StreamAlertFieldValueRuleList> iterator = streamAlertRuleList.iterator();
                            while (iterator.hasNext()) {
                                configObject = iterator.next();

                                if (configObject.getConfigID().equalsIgnoreCase(configID)) {

                                    tx.delete(LogicalDatastoreType.CONFIGURATION, alertFieldValueRuleRecordId.child(
                                            StreamAlertFieldValueRuleList.class, configObject.getKey()));

                                }
                            }

                        }

                        return tx.submit();
                    }

                });
        Futures.addCallback(commitFuture, new FutureCallback<Void>() {

            @Override
            public void onSuccess(final Void result) {

                futureResult.set(RpcResultBuilder.<DeleteAlertFieldValueRuleOutput> success(
                        deleteAlertFieldValueRuleOutputBuilder.build()).build());
            }

            @Override
            public void onFailure(final Throwable ex) {

                LOG.info("Failed to commit Rule", ex);

                futureResult.set(RpcResultBuilder.<DeleteAlertFieldValueRuleOutput> failed()
                        .withRpcErrors(((TransactionCommitFailedException) ex).getErrorList()).build());

            }
        });

        return futureResult;
    }

    /*
     * returns the list of alerts according to the stream id entered by the
     * user.
     */
    @Override
    public Future<RpcResult<GetAllAlertRuleOutput>> getAllAlertRule(final GetAllAlertRuleInput input) {

        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<GetAllAlertRuleOutput>> futureResult = SettableFuture.create();
        boolean isMatches = false;
        if (input.getStreamID() == null || input.getStreamID().isEmpty() || input.getStreamID().trim().isEmpty()) {
            LOG.debug("Invalid Parameters for GetAllAlertRule");
            return Futures
                    .immediateFailedCheckedFuture(new TransactionCommitFailedException("inalid-input", RpcResultBuilder
                            .newError(ErrorType.APPLICATION, "invalid-input", "StreamId is a  mandatory field")));
        }
        LOG.info("GetAllAlertRuleOutput: " + input);
        final GetAllAlertRuleOutputBuilder allAlertRuleOutputBuilder = new GetAllAlertRuleOutputBuilder();

        ListenableFuture<Optional<AlertMessageCountRuleRecord>> alertMessageCountReadFuture = tx.read(
                LogicalDatastoreType.OPERATIONAL, alertMessageCountRuleRecordId);
        ListenableFuture<Optional<AlertFieldContentRuleRecord>> alertFieldContentReadFuture = tx.read(
                LogicalDatastoreType.OPERATIONAL, alertFeildContentRuleRecordId);
        ListenableFuture<Optional<AlertFieldValueRuleRecord>> alertFieldValueReadFuture = tx.read(
                LogicalDatastoreType.OPERATIONAL, alertFieldValueRuleRecordId);
        try {
            Optional<AlertMessageCountRuleRecord> alertMessageCountRuleRecord = alertMessageCountReadFuture.get();
            List<StreamAlertMessageCountRuleList> streamAlertRuleList = new ArrayList<StreamAlertMessageCountRuleList>();

            if (alertMessageCountRuleRecord.isPresent()) {
                streamAlertRuleList = alertMessageCountRuleRecord.get().getStreamAlertMessageCountRuleList();

            }

            else {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                "Record is not present in operational data store")));
            }

            java.util.Iterator<StreamAlertMessageCountRuleList> iterator = streamAlertRuleList.iterator();
            List<StreamAlertMessageCountRuleListSorted> streamAlertMessageCountRuleListSortedList = new ArrayList<StreamAlertMessageCountRuleListSorted>();
            StreamAlertMessageCountRuleListSortedBuilder streamAlertMessageCountRuleListSortedBuilder = new StreamAlertMessageCountRuleListSortedBuilder();

            while (iterator.hasNext()) {
                StreamAlertMessageCountRuleList streamAlertMessageObj = iterator.next();
                if (streamAlertMessageObj.getStreamID().equals(input.getStreamID())) {
                    isMatches = true;
                    streamAlertMessageCountRuleListSortedBuilder.setStreamID(streamAlertMessageObj.getStreamID());
                    streamAlertMessageCountRuleListSortedBuilder.setMessageCountOperator(streamAlertMessageObj
                            .getMessageCountOperator());
                    streamAlertMessageCountRuleListSortedBuilder.setNodeType(streamAlertMessageObj.getNodeType());
                    streamAlertMessageCountRuleListSortedBuilder.setConfigID(streamAlertMessageObj.getConfigID());
                    streamAlertMessageCountRuleListSortedBuilder.setRuleID(streamAlertMessageObj.getRuleID());
                    streamAlertMessageCountRuleListSortedBuilder.setAlertTypeClassifier(streamAlertMessageObj
                            .getAlertTypeClassifier());
                    streamAlertMessageCountRuleListSortedBuilder.setRuleTypeClassifier(streamAlertMessageObj
                            .getRuleTypeClassifier());
                    streamAlertMessageCountRuleListSortedBuilder.setMessageCountCount(streamAlertMessageObj
                            .getMessageCountCount());
                    streamAlertMessageCountRuleListSortedBuilder.setMessageCountGrace(streamAlertMessageObj
                            .getMessageCountGrace());
                    streamAlertMessageCountRuleListSortedBuilder.setTimeStamp(streamAlertMessageObj.getTimeStamp());
                    streamAlertMessageCountRuleListSortedBuilder.setMessageCountBacklog(streamAlertMessageObj
                            .getMessageCountBacklog());
                    streamAlertMessageCountRuleListSortedBuilder.setAlertName(streamAlertMessageObj.getAlertName());
                    streamAlertMessageCountRuleListSortedList
                            .add((StreamAlertMessageCountRuleListSorted) streamAlertMessageCountRuleListSortedBuilder
                                    .build());
                    allAlertRuleOutputBuilder
                            .setStreamAlertMessageCountRuleListSorted(streamAlertMessageCountRuleListSortedList);

                }
            }

        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        try {
            Optional<AlertFieldContentRuleRecord> alertFieldContentRuleRecord = alertFieldContentReadFuture.get();
            List<StreamAlertFieldContentRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldContentRuleList>();

            if (alertFieldContentRuleRecord.isPresent()) {
                streamAlertRuleList = alertFieldContentRuleRecord.get().getStreamAlertFieldContentRuleList();
            }
            java.util.Iterator<StreamAlertFieldContentRuleList> iterator = streamAlertRuleList.iterator();
            List<StreamAlertFieldContentRuleListSorted> streamAlertFieldContentRuleListSorted = new ArrayList<StreamAlertFieldContentRuleListSorted>();
            StreamAlertFieldContentRuleListSortedBuilder streamAlertFieldContentRuleListSortedBuilder = new StreamAlertFieldContentRuleListSortedBuilder();

            while (iterator.hasNext()) {
                StreamAlertFieldContentRuleList streamAlertFieldContentObj = iterator.next();

                if (streamAlertFieldContentObj.getStreamID().equals(input.getStreamID())) {
                    isMatches = true;
                    streamAlertFieldContentRuleListSortedBuilder.setConfigID(streamAlertFieldContentObj.getConfigID());
                    streamAlertFieldContentRuleListSortedBuilder
                            .setTimeStamp(streamAlertFieldContentObj.getTimeStamp());
                    streamAlertFieldContentRuleListSortedBuilder.setRuleID(streamAlertFieldContentObj.getRuleID());
                    streamAlertFieldContentRuleListSortedBuilder.setStreamID(streamAlertFieldContentObj.getStreamID());
                    streamAlertFieldContentRuleListSortedBuilder.setFieldContentBacklog(streamAlertFieldContentObj
                            .getFieldContentBacklog());
                    streamAlertFieldContentRuleListSortedBuilder.setRuleTypeClassifier(streamAlertFieldContentObj
                            .getRuleTypeClassifier());
                    streamAlertFieldContentRuleListSortedBuilder.setAlertTypeClassifier(streamAlertFieldContentObj
                            .getAlertTypeClassifier());
                    streamAlertFieldContentRuleListSortedBuilder.setNodeType(streamAlertFieldContentObj.getNodeType());
                    streamAlertFieldContentRuleListSortedBuilder
                            .setFieldContentCompareToValue(streamAlertFieldContentObj.getFieldContentCompareToValue());
                    streamAlertFieldContentRuleListSortedBuilder.setFieldContentGrace(streamAlertFieldContentObj
                            .getFieldContentGrace());
                    streamAlertFieldContentRuleListSortedBuilder.setFieldContentField(streamAlertFieldContentObj
                            .getFieldContentField());
                    streamAlertFieldContentRuleListSortedBuilder
                            .setAlertName(streamAlertFieldContentObj.getAlertName());
                    streamAlertFieldContentRuleListSorted
                            .add((StreamAlertFieldContentRuleListSorted) streamAlertFieldContentRuleListSortedBuilder
                                    .build());
                    allAlertRuleOutputBuilder
                            .setStreamAlertFieldContentRuleListSorted(streamAlertFieldContentRuleListSorted);

                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        try {
            Optional<AlertFieldValueRuleRecord> alertFieldValueRuleRecord = alertFieldValueReadFuture.get();
            List<StreamAlertFieldValueRuleList> streamAlertRuleList = new ArrayList<StreamAlertFieldValueRuleList>();

            if (alertFieldValueRuleRecord.isPresent()) {
                streamAlertRuleList = alertFieldValueRuleRecord.get().getStreamAlertFieldValueRuleList();

            }

            java.util.Iterator<StreamAlertFieldValueRuleList> iterator = streamAlertRuleList.iterator();
            List<StreamAlertFieldValueRuleListSorted> streamAlertFieldValueRuleListSorted = new ArrayList<StreamAlertFieldValueRuleListSorted>();
            StreamAlertFieldValueRuleListSortedBuilder streamAlertFieldValueRuleListSortedBuilder = new StreamAlertFieldValueRuleListSortedBuilder();

            while (iterator.hasNext()) {
                StreamAlertFieldValueRuleList streamAlertFieldValueRuleObj = iterator.next();
                if (streamAlertFieldValueRuleObj.getStreamID().equals(input.getStreamID())) {
                    isMatches = true;
                    streamAlertFieldValueRuleListSortedBuilder.setAlertTypeClassifier(streamAlertFieldValueRuleObj
                            .getAlertTypeClassifier());
                    streamAlertFieldValueRuleListSortedBuilder.setConfigID(streamAlertFieldValueRuleObj.getConfigID());
                    streamAlertFieldValueRuleListSortedBuilder.setRuleID(streamAlertFieldValueRuleObj.getRuleID());
                    streamAlertFieldValueRuleListSortedBuilder.setStreamID(streamAlertFieldValueRuleObj.getStreamID());
                    streamAlertFieldValueRuleListSortedBuilder.setFieldValueBacklog(streamAlertFieldValueRuleObj
                            .getFieldValueBacklog());
                    streamAlertFieldValueRuleListSortedBuilder.setRuleTypeClassifier(streamAlertFieldValueRuleObj
                            .getRuleTypeClassifier());
                    streamAlertFieldValueRuleListSortedBuilder.setFieldValueField(streamAlertFieldValueRuleObj
                            .getFieldValueField());
                    streamAlertFieldValueRuleListSortedBuilder.setNodeType(streamAlertFieldValueRuleObj.getNodeType());
                    streamAlertFieldValueRuleListSortedBuilder.setFieldValueThreshholdType(streamAlertFieldValueRuleObj
                            .getFieldValueThreshholdType());
                    streamAlertFieldValueRuleListSortedBuilder.setFieldValueThreshhold(streamAlertFieldValueRuleObj
                            .getFieldValueThreshhold());
                    streamAlertFieldValueRuleListSortedBuilder.setFieldValueType(streamAlertFieldValueRuleObj
                            .getFieldValueType());
                    streamAlertFieldValueRuleListSortedBuilder.setFieldValueGrace(streamAlertFieldValueRuleObj
                            .getFieldValueGrace());
                    streamAlertFieldValueRuleListSortedBuilder
                            .setTimeStamp(streamAlertFieldValueRuleObj.getTimeStamp());
                    streamAlertFieldValueRuleListSortedBuilder
                            .setAlertName(streamAlertFieldValueRuleObj.getAlertName());
                    streamAlertFieldValueRuleListSorted
                            .add((StreamAlertFieldValueRuleListSorted) streamAlertFieldValueRuleListSortedBuilder
                                    .build());
                    allAlertRuleOutputBuilder
                            .setStreamAlertFieldValueRuleListSorted(streamAlertFieldValueRuleListSorted);
                }
            }
            if (!isMatches) {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                "Invalid Stream id or The stream is not present in operational data store")));
            }
            futureResult.set(RpcResultBuilder.<GetAllAlertRuleOutput> success(allAlertRuleOutputBuilder.build())
                    .build());

        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }

        return futureResult;

    }

}
