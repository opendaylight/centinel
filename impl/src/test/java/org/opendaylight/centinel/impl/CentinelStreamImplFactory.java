/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.DeleteStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.DeleteStreamInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.GetStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.GetStreamInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.PauseStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.PauseStreamInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.ResumeStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.ResumeStreamInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.UpdateStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.UpdateStreamInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.createstreaminput.StreamRules;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.createstreaminput.StreamRulesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.streamrecord.StreamList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.streamrecord.StreamListBuilder;

/**
 * @author Sunaina Khanna
 *
 *         This class provides the mock data for junit test cases of stream.
 */

public class CentinelStreamImplFactory {

    /**
     * Mock params for SetStream Rpc to create failure condition on invalid
     * input.
     */

    public SetStreamInput setInputForStreamWithInvalidInput() {
        SetStreamInputBuilder setStreamInputBuilder = new SetStreamInputBuilder();
        setStreamInputBuilder.setTitle(null);
        setStreamInputBuilder.setDescription(null);
        return setStreamInputBuilder.build();
    }

    /**
     * Mock params for SetStream Rpc
     */

    public SetStreamInput setInputForStreamWithValidInput() {
        SetStreamInputBuilder setStreamInputBuilder = new SetStreamInputBuilder();
        setStreamInputBuilder.setTitle("Str001");
        setStreamInputBuilder.setDescription("stream");
        return setStreamInputBuilder.build();
    }

    /**
     * Mock params for SetStream Rpc
     */

    public SetStreamOutput expectedStreamObject() {
        SetStreamOutputBuilder setStreamOutputBuilder = new SetStreamOutputBuilder();

        setStreamOutputBuilder.setTitle("Str001");
        setStreamOutputBuilder.setDescription("stream");
        setStreamOutputBuilder.setConfigID("1000");
        return setStreamOutputBuilder.build();
    }

    /**
     * Mock params for DeleteStream Rpc
     */

    public DeleteStreamInput deleteInputValidValuesForStream() {
        DeleteStreamInputBuilder deleteStreamInputBuilder = new DeleteStreamInputBuilder();
        deleteStreamInputBuilder.setStreamID("str001");
        return deleteStreamInputBuilder.build();
    }

    /**
     * Mock params for PauseStream Rpc
     */

    public PauseStreamInput pauseInputValidValuesForStream() {
        PauseStreamInputBuilder pauseStreamInputBuilder = new PauseStreamInputBuilder();
        pauseStreamInputBuilder.setStreamID("str001");
        return pauseStreamInputBuilder.build();
    }

    /**
     * Mock params for ResumeStream Rpc to create failure condition on invalid
     * input.
     */

    public ResumeStreamInput resumeInputValidValuesForStream() {
        ResumeStreamInputBuilder resumeStreamInputBuilder = new ResumeStreamInputBuilder();
        resumeStreamInputBuilder.setStreamID("str001");
        return resumeStreamInputBuilder.build();
    }

    /**
     * Mock object for stream in datastore
     */

    public StreamList mockStreamObjectBuilder() {
        StreamListBuilder streamListObj = new StreamListBuilder();
        List<StreamRules> streamRuleList = new ArrayList<StreamRules>();
        StreamRulesBuilder streamRuleBuilder = new StreamRulesBuilder();

        streamRuleBuilder.setField("field");
        streamRuleList.add(streamRuleBuilder.build());
        streamListObj.setTitle("Str001");
        streamListObj.setDescription("stream");
        streamListObj.setStreamID("naina");
        streamListObj.setConfigID("1000");
        streamListObj.setStreamRules(streamRuleList);
        return streamListObj.build();
    }

    /**
     * Mock params for DeleteStream Rpc to create failure condition on invalid
     * input.
     */

    public DeleteStreamInput deleteInputWithInvalidValuesForStream() {

        DeleteStreamInputBuilder deleteStreamInputBuilder = new DeleteStreamInputBuilder();
        return deleteStreamInputBuilder.build();
    }

    /**
     * Mock params for UpdateStream Rpc to create failure condition on invalid
     * input.
     */

    public UpdateStreamInput updateInvalidInputForStream() {
        UpdateStreamInputBuilder updateStreamInputBuilder = new UpdateStreamInputBuilder();
        updateStreamInputBuilder.setTitle("str001");
        updateStreamInputBuilder.setDescription("str002");
        return updateStreamInputBuilder.build();
    }

    /**
     * Mock params for UpdateStream Rpc
     */

    public UpdateStreamInput updateValidInputForStream() {
        UpdateStreamInputBuilder updateStreamInputBuilder = new UpdateStreamInputBuilder();
        updateStreamInputBuilder.setTitle("str001");
        updateStreamInputBuilder.setDescription("str002");
        updateStreamInputBuilder.setStreamID("sunaina");
        return updateStreamInputBuilder.build();
    }

    /**
     * Mock params for GetStream Rpc
     */

    public GetStreamInput validInputForGetStream() {
        GetStreamInputBuilder getStreamInputBuilder = new GetStreamInputBuilder();
        getStreamInputBuilder.setStreamID("sunaina");
        return getStreamInputBuilder.build();
    }

    /**
     * Mock params for GetStream Rpc to create failure condition on invalid
     * input.
     */

    public GetStreamInput invalidInputForGetStream() {
        GetStreamInputBuilder getStreamInputBuilder = new GetStreamInputBuilder();
        return getStreamInputBuilder.build();
    }

}
