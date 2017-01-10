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
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.CreateStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.DeleteOutputInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.DeleteOutputOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.DeleteStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.DeleteStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.DeleteStreamOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.GetStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.GetStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.GetStreamOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.GetThroughputInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.GetThroughputOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.PauseStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.PauseStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.PauseStreamOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.ResumeStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.ResumeStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.ResumeStreamOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetOutputInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetOutputOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.UpdateStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.UpdateStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.UpdateStreamOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.createstreaminput.StreamRules;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.createstreaminput.StreamRulesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.streamrecord.StreamList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.streamrecord.StreamListBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcError;
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

/**
 * @author Sunaina Khanna
 * 
 *         This class provides the implementation of stream RPCs and saves the
 *         data in config data store.
 * 
 */
public class CentinelStreamImpl implements StreamService, AutoCloseable {

    public static final InstanceIdentifier<StreamRecord> streamRecordId = InstanceIdentifier
            .builder(StreamRecord.class).build();

    private static final Logger LOG = LoggerFactory.getLogger(CentinelStreamImpl.class);
    private DataBroker dataProvider;

    public void setDataProvider(final DataBroker salDataProvider) {
        LOG.info(" Entered to Data Provider of stream:" + salDataProvider);
        this.dataProvider = salDataProvider;
    }

    @Override
    public void close() throws Exception {
    }

    /**
     * @param StreamListobj
     * @param strmID
     * @return streamRulesList This function builds the stream object along with
     *         the stream rule.
     */
    private StreamList buildStreamRule(final StreamList obj, String strmID) {

        StreamListBuilder streamRulesListBuilder = new StreamListBuilder();

        streamRulesListBuilder.setContentPack(obj.getContentPack());
        streamRulesListBuilder.setDescription(obj.getDescription());
        streamRulesListBuilder.setNodeType(obj.getNodeType());
        streamRulesListBuilder.setRuleID(obj.getRuleID());
        streamRulesListBuilder.setRuleTypeClassifier(obj.getRuleTypeClassifier());
        streamRulesListBuilder.setStreamID(strmID);
        if (!obj.getStreamRules().isEmpty()) {
            Iterator<StreamRules> it = obj.getStreamRules().iterator();
            List<StreamRules> streamRuleList = new ArrayList<StreamRules>();
            StreamRules streamRuleListObj = null;
            while (it.hasNext()) {
                streamRuleListObj = it.next();
                streamRuleList.add(streamRuleListObj);
            }
            streamRulesListBuilder.setStreamRules(streamRuleList);
        }
        streamRulesListBuilder.setTimeStamp(obj.getTimeStamp());
        streamRulesListBuilder.setTitle(obj.getTitle());
        streamRulesListBuilder.setConfigID(obj.getConfigID());
        streamRulesListBuilder.setDisabled(obj.getDisabled());
        return streamRulesListBuilder.build();
    }

    /**
     * @param configId
     * @param userinput
     * @return StreamList This function builds the StreamList
     */
    private StreamList buildstreamRecord(final CreateStreamInput input, String configId) {

        List<StreamRules> streamRuleList = new ArrayList<StreamRules>();

        if (input.getStreamRules() != null) {
            Iterator<StreamRules> it = input.getStreamRules().iterator();
            StreamRules streamRuleListObj = null;
            while (it.hasNext()) {
                streamRuleListObj = it.next();
                streamRuleList.add(streamRuleListObj);
            }
        }
        return new StreamListBuilder().setContentPack(input.getContentPack()).setDescription(input.getDescription())
                .setNodeType(input.getNodeType()).setRuleID(input.getRuleID())
                .setRuleTypeClassifier(input.getRuleTypeClassifier()).setStreamRules(streamRuleList)
                .setConfigID(configId).setTimeStamp(input.getTimeStamp()).setTitle(input.getTitle())
                .setStreamID(String.format("%x", (int) (Math.random() * 10000))).setDisabled("true").build();

    }

    /**
     * @param input
     * @param obj
     * @return UpdatedStreamListRecord This function builds the updated stream
     *         list according to the user input
     */
    private StreamList buildUpdateStreamListRecord(final UpdateStreamInput input, final StreamList obj) {
        StreamListBuilder streamRulesListBuilder = new StreamListBuilder();

        if (input.getContentPack() != null) {
            streamRulesListBuilder.setContentPack(input.getContentPack());
        } else {

            streamRulesListBuilder.setContentPack(obj.getContentPack());
        }

        if (input.getDescription() != null) {
            streamRulesListBuilder.setDescription(input.getDescription());
        }

        else {
            streamRulesListBuilder.setDescription(obj.getDescription());
        }

        if (input.getNodeType() != null) {
            streamRulesListBuilder.setNodeType(input.getNodeType());
        }

        else {

            streamRulesListBuilder.setNodeType(obj.getNodeType());
        }

        if (input.getRuleID() != null) {
            streamRulesListBuilder.setRuleID(input.getRuleID());
        }

        else {

            streamRulesListBuilder.setRuleID(obj.getRuleID());
        }

        if (input.getRuleTypeClassifier() != null) {
            streamRulesListBuilder.setRuleTypeClassifier(input.getRuleTypeClassifier());
        }

        else {

            streamRulesListBuilder.setRuleTypeClassifier(obj.getRuleTypeClassifier());
        }

        if (input.getStreamID() != null) {
            streamRulesListBuilder.setStreamID(input.getStreamID());
        }

        else {

            streamRulesListBuilder.setStreamID(obj.getStreamID());
        }

        if (input.getStreamRules() != null) {
            Iterator<StreamRules> it = input.getStreamRules().iterator();
            List<StreamRules> streamRuleList = new ArrayList<StreamRules>();
            while (it.hasNext()) {
                StreamRules streamRuleListObj = it.next();
                streamRuleList.add(streamRuleListObj);
            }

        }

        else {
            Iterator<StreamRules> it = obj.getStreamRules().iterator();
            List<StreamRules> streamRuleList = new ArrayList<StreamRules>();
            StreamRules streamRuleListObj = null;
            while (it.hasNext()) {
                streamRuleListObj = it.next();
                streamRuleList.add(streamRuleListObj);
            }
            streamRuleList.add(streamRuleListObj);
        }

        if (input.getTimeStamp() != null) {
            streamRulesListBuilder.setTimeStamp(input.getTimeStamp());
        }

        else {

            streamRulesListBuilder.setTimeStamp(obj.getTimeStamp());
        }

        if (input.getTitle() != null) {
            streamRulesListBuilder.setTitle(input.getTitle());
        }

        else {

            streamRulesListBuilder.setTitle(obj.getTitle());
        }

        streamRulesListBuilder.setConfigID(obj.getConfigID());

        return streamRulesListBuilder.build();
    }

    /**
     * @param StreamListobj
     * @param opStreamId
     * @param input
     * @return ResumedStreamListRecord This function builds the stream list
     *         record when resume operation is done.
     */
    private StreamList buildResumedStreamListRecord(final StreamList obj, String opStreamId, ResumeStreamInput input) {

        StreamListBuilder streamRulesListBuilder = new StreamListBuilder();

        streamRulesListBuilder.setDescription(obj.getDescription());
        streamRulesListBuilder.setNodeType(obj.getNodeType());
        streamRulesListBuilder.setRuleID(obj.getRuleID());
        streamRulesListBuilder.setStreamID(opStreamId);
        streamRulesListBuilder.setRuleTypeClassifier(obj.getRuleTypeClassifier());

        if (obj.getStreamRules() != null) {
            Iterator<StreamRules> it = obj.getStreamRules().iterator();
            List<StreamRules> streamRuleList = new ArrayList<StreamRules>();
            StreamRules streamRuleListObj = null;
            while (it.hasNext()) {
                streamRuleListObj = it.next();
                streamRuleList.add(streamRuleListObj);

            }
            streamRuleList.add(streamRuleListObj);

        }

        streamRulesListBuilder.setTimeStamp(obj.getTimeStamp());
        streamRulesListBuilder.setTitle(obj.getTitle());

        streamRulesListBuilder.setConfigID(obj.getConfigID());
        streamRulesListBuilder.setDisabled("false");

        return streamRulesListBuilder.build();
    }

    /**
     * @param obj
     * @param opStrmId
     * @param input
     * @return StreamList This function builds the stream list record when pause
     *         operation is performed.
     */
    private StreamList buildPausedStreamListRecord(final StreamList obj, String opStrmId, PauseStreamInput input) {

        StreamListBuilder streamRulesListBuilder = new StreamListBuilder();

        streamRulesListBuilder.setDescription(obj.getDescription());
        streamRulesListBuilder.setNodeType(obj.getNodeType());
        streamRulesListBuilder.setRuleID(obj.getRuleID());
        streamRulesListBuilder.setRuleTypeClassifier(obj.getRuleTypeClassifier());
        streamRulesListBuilder.setStreamID(opStrmId);

        if (obj.getStreamRules() != null) {
            Iterator<StreamRules> it = obj.getStreamRules().iterator();
            List<StreamRules> streamRuleList = new ArrayList<StreamRules>();
            StreamRules streamRuleListObj = null;
            while (it.hasNext()) {
                streamRuleListObj = it.next();
                streamRuleList.add(streamRuleListObj);
            }
            streamRuleList.add(streamRuleListObj);
        }

        streamRulesListBuilder.setTimeStamp(obj.getTimeStamp());
        streamRulesListBuilder.setTitle(obj.getTitle());
        streamRulesListBuilder.setConfigID(obj.getConfigID());
        streamRulesListBuilder.setDisabled("true");

        return streamRulesListBuilder.build();
    }

    private RpcError streamIdcannotbenullError() {

        return RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input", "Stream ID cannot be null");
    }

    @Override
    public Future<RpcResult<DeleteStreamOutput>> deleteStream(final DeleteStreamInput input) {

        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<DeleteStreamOutput>> futureResult = SettableFuture.create();
        boolean idMatches = false;
        if (input.getStreamID() == null || input.getStreamID().isEmpty() || input.getStreamID().trim().isEmpty()) {
            LOG.debug("STREAM ID CANNOT BE NULL");
            return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("inalid-input",
                    streamIdcannotbenullError()));
        }
        final DeleteStreamOutputBuilder deleteStreamRuleOutputBuilder = new DeleteStreamOutputBuilder();
        deleteStreamRuleOutputBuilder.setMessage(input.getStreamID());

        ListenableFuture<Optional<StreamRecord>> readFutureOperational = tx.read(LogicalDatastoreType.OPERATIONAL,
                streamRecordId);
        ListenableFuture<Optional<StreamRecord>> readFutureConfigure = tx.read(LogicalDatastoreType.CONFIGURATION,
                streamRecordId);

        String configId = null;

        try {

            Optional<StreamRecord> record = readFutureOperational.get();
            if (record.isPresent()) {
                StreamRecord operationalRecord = readFutureOperational.get().get();
                List<StreamList> streamList = new ArrayList<StreamList>();
                if (!operationalRecord.getStreamList().isEmpty()) {
                    streamList = operationalRecord.getStreamList();
                    Iterator<StreamList> iterator = streamList.iterator();

                    while (iterator.hasNext()) {
                        StreamList operationalObject = iterator.next();
                        if (operationalObject.getStreamID().equals(input.getStreamID())) {
                            configId = operationalObject.getConfigID();
                            idMatches = true;
                        }
                    }
                    if (!idMatches) {
                        return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException(
                                "invalid-input", RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                        "Invalid Stream id or The stream is not present in operational data store")));
                    }
                }
            } else {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                "Record is not present in operational data store")));
            }
        }

        catch (Exception ex) {

            deleteStreamRuleOutputBuilder.setMessage("Stream with stream id" + input.getStreamID() + "does not exists");
            return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                    RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                            "Invalid Stream id or The stream is not present in operational data store")));

        }
        final String confID = configId;
        final ListenableFuture<Void> commitFuture = Futures.transformAsync(readFutureConfigure,
                new AsyncFunction<Optional<StreamRecord>, Void>() {

                    @Override
                    public ListenableFuture<Void> apply(final Optional<StreamRecord> streamRulesRecord)
                            throws Exception {

                        List<StreamList> streamRulesLists = new ArrayList<StreamList>();
                        if (streamRulesRecord.isPresent()) {
                            streamRulesLists = streamRulesRecord.get().getStreamList();
                        }
                        Iterator<StreamList> iterator = streamRulesLists.iterator();

                        while (iterator.hasNext()) {
                            StreamList configObject = iterator.next();
                            if (configObject.getConfigID().equalsIgnoreCase(confID)) {
                                tx.delete(LogicalDatastoreType.CONFIGURATION,
                                        streamRecordId.child(StreamList.class, configObject.getKey()));
                            }

                        }
                        return tx.submit();
                    }
                });
        Futures.addCallback(commitFuture, new FutureCallback<Void>() {
            @Override
            public void onSuccess(final Void result) {

                futureResult.set(RpcResultBuilder.<DeleteStreamOutput> success(deleteStreamRuleOutputBuilder.build())
                        .build());
            }

            @Override
            public void onFailure(final Throwable ex) {

                LOG.debug("Failed to commit Rule", ex);

                futureResult.set(RpcResultBuilder.<DeleteStreamOutput> failed()
                        .withRpcErrors(((TransactionCommitFailedException) ex).getErrorList()).build());
            }
        });
        return futureResult;
    }

    @Override
    public Future<RpcResult<GetStreamOutput>> getStream(GetStreamInput input) {
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<GetStreamOutput>> futureResult = SettableFuture.create();
        boolean idMatches = false;
        if (input.getStreamID() == null || input.getStreamID().isEmpty() || input.getStreamID().trim().isEmpty()) {
            LOG.debug("STREAM ID CANNOT BE NULL");
            return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("inalid-input",
                    streamIdcannotbenullError()));
        }
        final GetStreamOutputBuilder getStreamOutputBuilder = new GetStreamOutputBuilder();
        ListenableFuture<Optional<StreamRecord>> streamRuleReadFuture = tx.read(LogicalDatastoreType.OPERATIONAL,
                streamRecordId);
        try {
            Optional<StreamRecord> streamRecord = streamRuleReadFuture.get();
            List<StreamList> streamList = new ArrayList<StreamList>();
            if (streamRecord.isPresent()) {
                streamList = streamRecord.get().getStreamList();
            } else {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                "Record is not present in operational data store")));
            }
            if (streamList.isEmpty()) {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("inalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input", "NO stream in datastore")));
            } else {
                java.util.Iterator<StreamList> iterator = streamList.iterator();

                while (iterator.hasNext()) {
                    StreamList streamListObj = iterator.next();
                    if (streamListObj.getStreamID().equals(input.getStreamID())) {
                        idMatches = true;
                        getStreamOutputBuilder.setConfigID(streamListObj.getConfigID())
                                .setContentPack(streamListObj.getContentPack())
                                .setDescription(streamListObj.getDescription())
                                .setNodeType(streamListObj.getNodeType()).setRuleID(streamListObj.getRuleID())
                                .setRuleTypeClassifier(streamListObj.getRuleTypeClassifier())
                                .setStreamID(streamListObj.getStreamID()).setTimeStamp(streamListObj.getTimeStamp())
                                .setTitle(streamListObj.getTitle());

                        if (!streamListObj.getStreamRules().isEmpty()) {

                            Iterator<StreamRules> it = streamListObj.getStreamRules().iterator();
                            List<StreamRules> streamRule = new ArrayList<StreamRules>();
                            StreamRules streamRuleListObj = null;
                            while (it.hasNext()) {
                                streamRuleListObj = it.next();
                                streamRule.add(streamRuleListObj);
                            }
                            getStreamOutputBuilder.setStreamRules(streamRule);
                        }
                    }
                }
                if (!idMatches) {
                    return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                            RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                    "Invalid Stream id or The stream is not present in operational data store")));
                }
                futureResult.set(RpcResultBuilder.<GetStreamOutput> success(getStreamOutputBuilder.build()).build());
            }
        }

        catch (Exception ex) {
            LOG.error("Exception occured while getting record from operational data store", ex);
        }
        return futureResult;
    }

    @Override
    public Future<RpcResult<UpdateStreamOutput>> updateStream(final UpdateStreamInput input) {
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<UpdateStreamOutput>> futureResult = SettableFuture.create();
        boolean idMatches = false;
        if (input.getStreamID() == null || input.getStreamID().isEmpty() || input.getStreamID().trim().isEmpty()) {
            LOG.debug("STREAM ID CANNOT BE NULL");
            return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("inalid-input",
                    streamIdcannotbenullError()));
        }
        final UpdateStreamOutputBuilder updateStreamRuleOutputBuilder = new UpdateStreamOutputBuilder();
        updateStreamRuleOutputBuilder.setTitle(input.getTitle());
        updateStreamRuleOutputBuilder.setDescription(input.getDescription());
        ListenableFuture<Optional<StreamRecord>> readFutureOperational = tx.read(LogicalDatastoreType.OPERATIONAL,
                streamRecordId);

        ListenableFuture<Optional<StreamRecord>> readFutureConfigure = tx.read(LogicalDatastoreType.CONFIGURATION,
                streamRecordId);

        String configId = null;

        try {
            Optional<StreamRecord> record = readFutureOperational.get();
            if (record.isPresent()) {
                StreamRecord operationalRecord = readFutureOperational.get().get();
                List<StreamList> streamRulesList = new ArrayList<StreamList>();
                if (!operationalRecord.getStreamList().isEmpty()) {
                    streamRulesList = operationalRecord.getStreamList();
                    Iterator<StreamList> iterator = streamRulesList.iterator();
                    while (iterator.hasNext()) {
                        StreamList operationalObject = iterator.next();
                        if (operationalObject.getStreamID().equals(input.getStreamID())) {
                            configId = operationalObject.getConfigID();
                            idMatches = true;
                        }
                    }
                    if (!idMatches) {

                        return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException(
                                "invalid-input", RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                        "Invalid Stream id or The stream is not present in operational data store")));
                    }
                }
            } else {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                "Record is not present in operational data store")));
            }
        } catch (InterruptedException | ExecutionException e) {

            futureResult.set(RpcResultBuilder.<UpdateStreamOutput> failed()
                    .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
            return futureResult;
        }
        final String confId = configId;

        final ListenableFuture<Void> commitFuture = Futures.transformAsync(readFutureConfigure,
                new AsyncFunction<Optional<StreamRecord>, Void>() {

                    @Override
                    public ListenableFuture<Void> apply(final Optional<StreamRecord> streamRulesRecord)
                            throws Exception {

                        List<StreamList> streamRuleList = new ArrayList<StreamList>();
                        List<StreamList> updatedStreamRuleList = new ArrayList<StreamList>();

                        if (streamRulesRecord.isPresent()) {

                            streamRuleList = streamRulesRecord.get().getStreamList();
                            StreamList configObject = null;
                            Iterator<StreamList> iterator = streamRuleList.iterator();

                            while (iterator.hasNext()) {

                                configObject = iterator.next();

                                if (configObject.getConfigID().equalsIgnoreCase(confId)) {

                                    updatedStreamRuleList.add(buildUpdateStreamListRecord(input, configObject));

                                    tx.merge(LogicalDatastoreType.CONFIGURATION, streamRecordId,
                                            new StreamRecordBuilder().setStreamList(updatedStreamRuleList).build());
                                }
                            }
                        }
                        return tx.submit();
                    }

                });
        Futures.addCallback(commitFuture, new FutureCallback<Void>() {
            @Override
            public void onSuccess(final Void result) {

                futureResult.set(RpcResultBuilder.<UpdateStreamOutput> success(updateStreamRuleOutputBuilder.build())
                        .build());
            }

            @Override
            public void onFailure(final Throwable ex) {

                LOG.debug("Failed to commit Rule", ex);

                futureResult.set(RpcResultBuilder.<UpdateStreamOutput> failed()
                        .withRpcErrors(((TransactionCommitFailedException) ex).getErrorList()).build());
            }
        });
        return futureResult;
    }

    @Override
    public Future<RpcResult<SetStreamOutput>> setStream(final SetStreamInput input) {

        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<SetStreamOutput>> futureResult = SettableFuture.create();
        final String configId = generateRandomId();
        if (input.getDescription() == null || input.getTitle() == null || input.getDescription().isEmpty()
                || input.getTitle().isEmpty() || input.getDescription().trim().isEmpty()
                || input.getTitle().trim().isEmpty()) {
            LOG.debug("Title and Description cannot be null");
            return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                    RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                            "Title and Description cannot be null")));
        }
        final SetStreamOutputBuilder setStreamRuleOutputBuilder = new SetStreamOutputBuilder();

        setStreamRuleOutputBuilder.setDescription(input.getDescription());
        setStreamRuleOutputBuilder.setTitle(input.getTitle());
        setStreamRuleOutputBuilder.setConfigID(configId);
        List<StreamList> streamList = new ArrayList<StreamList>();
        streamList.add(buildstreamRecord(input, configId));
        try {
            tx.merge(LogicalDatastoreType.CONFIGURATION, streamRecordId,
                    new StreamRecordBuilder().setStreamList(streamList).build(), true);
            tx.submit();

            futureResult.set(RpcResultBuilder.<SetStreamOutput> success(setStreamRuleOutputBuilder.build()).build());
        }

        catch (Exception e) {
            LOG.info("Failed to commit Rule", e);

            futureResult.set(RpcResultBuilder.<SetStreamOutput> failed()
                    .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
        }

        return futureResult;
    }

    public String generateRandomId() {
        return String.format("%x", (int) (Math.random() * 10000));
    }

    @Override
    public Future<RpcResult<DeleteOutputOutput>> deleteOutput(DeleteOutputInput input) {
        return null;
    }

    @Override
    public Future<RpcResult<PauseStreamOutput>> pauseStream(final PauseStreamInput input) {
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<PauseStreamOutput>> futureResult = SettableFuture.create();
        boolean idMatches = false;
        if (input.getStreamID() == null || input.getStreamID().isEmpty() || input.getStreamID().trim().isEmpty()) {
            LOG.debug("STREAM ID CANNOT BE NULL");
            return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                    streamIdcannotbenullError()));
        }
        final PauseStreamOutputBuilder pauseStreamOutputBuilder = new PauseStreamOutputBuilder();
        pauseStreamOutputBuilder.setDisabled("true");
        ListenableFuture<Optional<StreamRecord>> readFutureOperational = tx.read(LogicalDatastoreType.OPERATIONAL,
                streamRecordId);

        ListenableFuture<Optional<StreamRecord>> readFutureConfigure = tx.read(LogicalDatastoreType.CONFIGURATION,
                streamRecordId);

        String configId = null;
        String opStreamId = null;

        try {
            Optional<StreamRecord> record = readFutureOperational.get();
            if (record.isPresent()) {
                StreamRecord operationalRecord = readFutureOperational.get().get();
                List<StreamList> streamRulesList = new ArrayList<StreamList>();

                if (!operationalRecord.getStreamList().isEmpty()) {

                    streamRulesList = operationalRecord.getStreamList();
                    Iterator<StreamList> iterator = streamRulesList.iterator();

                    while (iterator.hasNext()) {

                        StreamList operationalObject = iterator.next();

                        if (operationalObject.getStreamID().equals(input.getStreamID())) {
                            configId = operationalObject.getConfigID();
                            opStreamId = operationalObject.getStreamID();
                            idMatches = true;

                        }
                    }
                    if (!idMatches) {
                        return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException(
                                "invalid-input", RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                        "Invalid Stream id or The stream is not present in operational data store")));

                    }

                }
            } else {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                "Record is not present in operational data store")));
            }
        }

        catch (InterruptedException | ExecutionException e) {

            futureResult.set(RpcResultBuilder.<PauseStreamOutput> failed()
                    .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
            return futureResult;

        }

        final String confID = configId;
        final String opStrmId = opStreamId;

        final ListenableFuture<Void> commitFuture = Futures.transformAsync(readFutureConfigure,
                new AsyncFunction<Optional<StreamRecord>, Void>() {

                    @Override
                    public ListenableFuture<Void> apply(final Optional<StreamRecord> streamRulesRecord)
                            throws Exception {

                        List<StreamList> streamRuleList = new ArrayList<StreamList>();
                        List<StreamList> updatedStreamRuleList = new ArrayList<StreamList>();

                        if (streamRulesRecord.isPresent()) {

                            streamRuleList = streamRulesRecord.get().getStreamList();
                            StreamList configObject = null;
                            Iterator<StreamList> iterator = streamRuleList.iterator();

                            while (iterator.hasNext()) {

                                configObject = iterator.next();

                                if (configObject.getConfigID().equalsIgnoreCase(confID)) {

                                    updatedStreamRuleList
                                            .add(buildPausedStreamListRecord(configObject, opStrmId, input));

                                    tx.merge(LogicalDatastoreType.CONFIGURATION, streamRecordId,
                                            new StreamRecordBuilder().setStreamList(updatedStreamRuleList).build());

                                }
                            }

                        }

                        return tx.submit();
                    }

                });
        Futures.addCallback(commitFuture, new FutureCallback<Void>() {
            @Override
            public void onSuccess(final Void result) {

                futureResult.set(RpcResultBuilder.<PauseStreamOutput> success(pauseStreamOutputBuilder.build()).build());

            }

            @Override
            public void onFailure(final Throwable ex) {

                LOG.debug("Failed to commit Rule", ex);

                futureResult.set(RpcResultBuilder.<PauseStreamOutput> failed()
                        .withRpcErrors(((TransactionCommitFailedException) ex).getErrorList()).build());

            }
        });

        return futureResult;
    }

    @Override
    public Future<RpcResult<GetThroughputOutput>> getThroughput(GetThroughputInput input) {
        return null;
    }

    @Override
    public Future<RpcResult<ResumeStreamOutput>> resumeStream(final ResumeStreamInput input) {
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<ResumeStreamOutput>> futureResult = SettableFuture.create();
        boolean idMatches = false;
        if (input.getStreamID() == null || input.getStreamID().isEmpty() || input.getStreamID().trim().isEmpty()) {
            LOG.debug("STREAM ID CANNOT BE NULL");
            return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                    streamIdcannotbenullError()));
        }
        final ResumeStreamOutputBuilder resumeStreamOutputBuilder = new ResumeStreamOutputBuilder();
        resumeStreamOutputBuilder.setDisabled("false");
        ListenableFuture<Optional<StreamRecord>> readFutureOperational = tx.read(LogicalDatastoreType.OPERATIONAL,
                streamRecordId);

        ListenableFuture<Optional<StreamRecord>> readFutureConfigure = tx.read(LogicalDatastoreType.CONFIGURATION,
                streamRecordId);

        String configId = null;
        String opStrmId = null;

        try {
            Optional<StreamRecord> record = readFutureOperational.get();
            if (record.isPresent()) {
                StreamRecord operationalRecord = readFutureOperational.get().get();
                List<StreamList> streamRulesList = new ArrayList<StreamList>();

                if (!operationalRecord.getStreamList().isEmpty()) {

                    streamRulesList = operationalRecord.getStreamList();
                    Iterator<StreamList> iterator = streamRulesList.iterator();

                    while (iterator.hasNext()) {

                        StreamList operationalObject = iterator.next();

                        if (operationalObject.getStreamID().equals(input.getStreamID())) {
                            configId = operationalObject.getConfigID();
                            opStrmId = operationalObject.getStreamID();
                            idMatches = true;

                        }
                    }
                    if (!idMatches) {
                        return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException(
                                "invalid-input", RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                        "Invalid Stream id or The stream is not present in operational data store")));

                    }
                }
            } else {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                "Record is not present in operational data store")));
            }

        } catch (InterruptedException | ExecutionException e) {

            futureResult.set(RpcResultBuilder.<ResumeStreamOutput> failed()
                    .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
            return futureResult;
        }
        final String confID = configId;
        final String opStreamId = opStrmId;

        final ListenableFuture<Void> commitFuture = Futures.transformAsync(readFutureConfigure,
                new AsyncFunction<Optional<StreamRecord>, Void>() {

                    @Override
                    public ListenableFuture<Void> apply(final Optional<StreamRecord> streamRulesRecord)
                            throws Exception {

                        List<StreamList> streamRuleList = new ArrayList<StreamList>();
                        List<StreamList> updatedStreamRuleList = new ArrayList<StreamList>();

                        if (streamRulesRecord.isPresent()) {

                            streamRuleList = streamRulesRecord.get().getStreamList();
                            StreamList configObject = null;
                            Iterator<StreamList> iterator = streamRuleList.iterator();

                            while (iterator.hasNext()) {

                                configObject = iterator.next();

                                if (configObject.getConfigID().equalsIgnoreCase(confID)) {
                                    StreamList updatedConfigObj = buildResumedStreamListRecord(configObject, opStreamId,
                                            input);

                                    updatedStreamRuleList.add(updatedConfigObj);
                                    tx.merge(LogicalDatastoreType.CONFIGURATION, streamRecordId,
                                            new StreamRecordBuilder().setStreamList(updatedStreamRuleList).build());

                                }
                            }

                        }

                        return tx.submit();
                    }

                });
        Futures.addCallback(commitFuture, new FutureCallback<Void>() {
            @Override
            public void onSuccess(final Void result) {

                futureResult.set(RpcResultBuilder.<ResumeStreamOutput> success(resumeStreamOutputBuilder.build())
                        .build());
            }

            @Override
            public void onFailure(final Throwable ex) {

                LOG.debug("Failed to commit Rule", ex);

                futureResult.set(RpcResultBuilder.<ResumeStreamOutput> failed()
                        .withRpcErrors(((TransactionCommitFailedException) ex).getErrorList()).build());

            }
        });

        return futureResult;
    }

    @Override
    public Future<RpcResult<SetOutputOutput>> setOutput(SetOutputInput input) {
        return null;
    }

    @Override
    public Future<RpcResult<SetRuleOutput>> setRule(final SetRuleInput input) {
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<SetRuleOutput>> futureResult = SettableFuture.create();
        boolean idMatches = false;
        final String streamRuleId = generateRandomId();
        if (input.getType() == StreamType.FieldPresence) {
            if (input.getStreamID() == null || input.getStreamID().isEmpty() || input.getStreamID().trim().isEmpty()
                    || input.getField() == null || input.getField().isEmpty() || input.getField().trim().isEmpty()) {
                return Futures.immediateFailedCheckedFuture(
                        new TransactionCommitFailedException("inalid-input", RpcResultBuilder.newError(
                                ErrorType.APPLICATION, "invalid-input", "StreamId,Value are mandatory parameters")));
            }
        } else {
            if (input.getStreamID() == null || input.getStreamID().isEmpty() || input.getStreamID().trim().isEmpty()
                    || input.getField() == null || input.getField().isEmpty() || input.getField().trim().isEmpty()
                    || input.getValue() == null || input.getValue().isEmpty() || input.getValue().trim().isEmpty()) {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("inalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                "StreamId,Value and Field are mandatory parameters")));
            }
        }
        final SetRuleOutputBuilder setRuleOutputBuilder = new SetRuleOutputBuilder();
        setRuleOutputBuilder.setStreamRuleID(streamRuleId);
        ListenableFuture<Optional<StreamRecord>> readFutureOperational = tx.read(LogicalDatastoreType.OPERATIONAL,
                streamRecordId);

        ListenableFuture<Optional<StreamRecord>> readFutureConfigure = tx.read(LogicalDatastoreType.CONFIGURATION,
                streamRecordId);

        String configIdOp = null;
        String streamIdOp = null;

        try {
            Optional<StreamRecord> record = readFutureOperational.get();
            if (record.isPresent()) {

                StreamRecord operationalRecord = record.get();

                List<StreamList> streamRulesList = new ArrayList<StreamList>();

                if (!operationalRecord.getStreamList().isEmpty()) {

                    streamRulesList = operationalRecord.getStreamList();
                    Iterator<StreamList> iterator = streamRulesList.iterator();

                    while (iterator.hasNext()) {

                        StreamList operationalObject = iterator.next();

                        if (operationalObject.getStreamID().equals(input.getStreamID())) {
                            configIdOp = operationalObject.getConfigID();
                            streamIdOp = operationalObject.getStreamID();
                            idMatches = true;

                        }
                    }
                    if (!idMatches) {
                        return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException(
                                "invalid-input", RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                        "Invalid Stream id or The stream is not present in operational data store")));
                    }

                }
            } else {
                return Futures.immediateFailedCheckedFuture(new TransactionCommitFailedException("invalid-input",
                        RpcResultBuilder.newError(ErrorType.APPLICATION, "invalid-input",
                                "Record is not present in operational data store")));
            }

        } catch (InterruptedException | ExecutionException e) {

            futureResult.set(RpcResultBuilder.<SetRuleOutput> failed()
                    .withRpcErrors(((TransactionCommitFailedException) e).getErrorList()).build());
            return futureResult;

        }
        final String confID = configIdOp;
        final String strmID = streamIdOp;
        final ListenableFuture<Void> commitFuture = Futures.transformAsync(readFutureConfigure,
                new AsyncFunction<Optional<StreamRecord>, Void>() {

                    @Override
                    public ListenableFuture<Void> apply(final Optional<StreamRecord> streamRulesRecord)
                            throws Exception {

                        List<StreamList> streamRuleList = new ArrayList<StreamList>();
                        List<StreamList> updatedStreamRuleList = new ArrayList<StreamList>();

                        if (streamRulesRecord.isPresent()) {

                            streamRuleList = streamRulesRecord.get().getStreamList();
                            StreamList configObject = null;
                            Iterator<StreamList> iterator = streamRuleList.iterator();

                            while (iterator.hasNext()) {

                                configObject = iterator.next();
                                if (configObject.getConfigID().equalsIgnoreCase(confID)) {
                                    StreamRulesBuilder streamRuleBuilder = new StreamRulesBuilder();
                                    streamRuleBuilder.setField(input.getField());
                                    streamRuleBuilder.setType(input.getType());
                                    streamRuleBuilder.setValue(input.getValue());
                                    streamRuleBuilder.setInverted(input.isInverted());
                                    streamRuleBuilder.setStreamRuleID(streamRuleId);
                                    SetRuleOutputBuilder setRuleOutputBuilder = new SetRuleOutputBuilder();
                                    setRuleOutputBuilder.setField(input.getField());
                                    setRuleOutputBuilder.setInverted(input.isInverted());
                                    setRuleOutputBuilder.setType(input.getType());
                                    setRuleOutputBuilder.setValue(input.getValue());
                                    StreamRules streamrule = streamRuleBuilder.build();
                                    configObject.getStreamRules().add(streamrule);
                                    updatedStreamRuleList.add(buildStreamRule(configObject, strmID));
                                    tx.merge(LogicalDatastoreType.CONFIGURATION, streamRecordId,
                                            new StreamRecordBuilder().setStreamList(updatedStreamRuleList).build());
                                }
                            }

                        }

                        return tx.submit();
                    }

                });
        Futures.addCallback(commitFuture, new FutureCallback<Void>() {
            @Override
            public void onSuccess(final Void result) {

                futureResult.set(RpcResultBuilder.<SetRuleOutput> success(setRuleOutputBuilder.build()).build());
            }

            @Override
            public void onFailure(final Throwable ex) {

                LOG.debug("Failed to commit stream Rule", ex);

                futureResult.set(RpcResultBuilder.<SetRuleOutput> failed()
                        .withRpcErrors(((TransactionCommitFailedException) ex).getErrorList()).build());

            }
        });

        return futureResult;
    }

}
