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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetRuleInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.UpdateStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.UpdateStreamInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.createstreaminput.StreamRules;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.createstreaminput.StreamRulesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.streamrecord.StreamList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.streamrecord.StreamListBuilder;
import org.opendaylight.yangtools.yang.binding.DataObject;

/**
 * @author Sunaina Khanna
 * 
 *         This class provides the mock data for junit test cases of stream.
 */

public class CentinelStreamImplFactory {

	/* mock streamRecord */
	public static StreamRecord getStreamRecord() {
		StreamRecordBuilder streamRecordBuilder = new StreamRecordBuilder();
		List<StreamList> streamList = new ArrayList<StreamList>();
		streamList.add(getStreamList());
		streamRecordBuilder.setStreamList(streamList);
		return streamRecordBuilder.build();
	}

	public static StreamList getStreamList() {
		StreamListBuilder streamListObj = new StreamListBuilder();
		List<StreamRules> streamRuleList = new ArrayList<StreamRules>();

		StreamRulesBuilder streamRuleBuilder = new StreamRulesBuilder();
		streamRuleBuilder.setField("field");
		streamRuleList.add(streamRuleBuilder.build());

		streamListObj.setTitle("Str001");
		streamListObj.setDescription("stream");
		streamListObj.setStreamID("str001");
		streamListObj.setConfigID("1000");
		streamListObj.setDisabled("false");
		streamListObj.setStreamRules(streamRuleList);
		return streamListObj.build();
	}

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
	 * Mock params for SetStreamRule Rpc
	 */

	public SetRuleInput setInputForStreamRuleWithInvalidInput() {
		SetRuleInputBuilder setRuleInputBuilder = new SetRuleInputBuilder();
		setRuleInputBuilder.setField("field");
		setRuleInputBuilder.setInverted(true);
		setRuleInputBuilder.setStreamRuleID("rule001");
		setRuleInputBuilder.setType(StreamType.GreatorThan);
		setRuleInputBuilder.setValue("value");
		return setRuleInputBuilder.build();
	}

	public ResumeStreamInput resumeInputValidValuesForStreamNull() {
		ResumeStreamInputBuilder resumeStreamInputBuilder = new ResumeStreamInputBuilder();
		resumeStreamInputBuilder.setStreamID(" ");
		return resumeStreamInputBuilder.build();
	}

	public PauseStreamInput pauseInputValidValuesForStreamNull() {
		PauseStreamInputBuilder pauseStreamInputBuilder = new PauseStreamInputBuilder();
		pauseStreamInputBuilder.setStreamID(" ");
		return pauseStreamInputBuilder.build();
	}

	/**
	 * Mock params for DeleteStream Rpc to create failure condition on invalid
	 * input.
	 */

	public DeleteStreamInput deleteInputWithInvalidIdForStream() {
		DeleteStreamInputBuilder deleteStreamInputBuilder = new DeleteStreamInputBuilder();
		deleteStreamInputBuilder.setStreamID("deede");
		return deleteStreamInputBuilder.build();
	}

	/**
	 * Mock params for SetStream Rpc to create failure condition on invalid
	 * input.
	 */

	public StreamList setInputForStreamWithValidInputForRule() {
		StreamListBuilder setStreamList = new StreamListBuilder();
		setStreamList.setTitle("Str001");
		setStreamList.setDescription("stream");
		setStreamList.setStreamID("1000");
		return setStreamList.build();
	}

	/**
	 * Mock params for SetStreamRule Rpc
	 */

	public SetRuleInput setInputForStreamRuleWithInValidStreamId() {
		SetRuleInputBuilder setRuleInputBuilder = new SetRuleInputBuilder();
		setRuleInputBuilder.setField("field");
		setRuleInputBuilder.setInverted(true);
		setRuleInputBuilder.setStreamID("str001");
		setRuleInputBuilder.setStreamRuleID("rule001");
		setRuleInputBuilder.setType(StreamType.GreatorThan);
		setRuleInputBuilder.setValue("value");
		return setRuleInputBuilder.build();
	}

	public SetRuleInput setInputForStreamRuleWithInvalidInputWithType() {
		SetRuleInputBuilder setRuleInputBuilder = new SetRuleInputBuilder();
		setRuleInputBuilder.setField("field");
		setRuleInputBuilder.setInverted(true);
		setRuleInputBuilder.setStreamRuleID("rule001");
		setRuleInputBuilder.setType(StreamType.FieldPresence);
		setRuleInputBuilder.setValue("value");
		return setRuleInputBuilder.build();
	}

	/**
	 * Mock params for SetStreamRule Rpc
	 */

	public SetRuleOutput expectedStreamRuleObject() {
		SetRuleOutputBuilder setRuleOutputBuilder = new SetRuleOutputBuilder();
		setRuleOutputBuilder.setField("field");
		setRuleOutputBuilder.setInverted(true);
		setRuleOutputBuilder.setStreamRuleID("rule001");
		setRuleOutputBuilder.setType(StreamType.GreatorThan);
		setRuleOutputBuilder.setValue("value");
		return setRuleOutputBuilder.build();
	}

	/**
	 * Mock params for SetStreamRule Rpc
	 */

	public SetRuleInput setInputForStreamRuleWithValidInput() {
		SetRuleInputBuilder setRuleInputBuilder = new SetRuleInputBuilder();
		setRuleInputBuilder.setField("field");
		setRuleInputBuilder.setInverted(true);
		setRuleInputBuilder.setStreamID("1000");
		setRuleInputBuilder.setStreamRuleID("rule001");
		setRuleInputBuilder.setType(StreamType.GreatorThan);
		setRuleInputBuilder.setValue("value");
		return setRuleInputBuilder.build();
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
		updateStreamInputBuilder.setStreamID("str001");
		updateStreamInputBuilder.setConfigID("1000");
		updateStreamInputBuilder.setContentPack("content pack");
		updateStreamInputBuilder.setNodeType("node type");
		updateStreamInputBuilder.setRuleID("1");
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
