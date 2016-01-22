/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.laas.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.createstreaminput.StreamRules;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.createstreaminput.StreamRulesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.streamrecord.StreamList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.streamrecord.StreamListBuilder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

/**
 * @author Monika Verma
 * 
 *         This class provides dummy StreamRecord object for different
 *         scenarios.
 * 
 */
public class ChangeEventObjectFactory {

    public static final InstanceIdentifier<StreamRecord> streamRecordId = InstanceIdentifier
            .builder(StreamRecord.class).build();

    public DataObject getUpdatedSubtreeStreamRecord() {
        StreamRecordBuilder streamRecordBuilder = new StreamRecordBuilder();
        List<StreamList> streamList = new ArrayList<StreamList>();
        streamList.add(getUpdatedStreamList());
        streamRecordBuilder.setStreamList(streamList);
        return streamRecordBuilder.build();
    }

    public DataObject getOriginalSubtreeStreamRecord() {
        StreamRecordBuilder streamRecordBuilder = new StreamRecordBuilder();
        List<StreamList> streamList = new ArrayList<StreamList>();
        streamList.add(getOriginalStreamList());
        streamRecordBuilder.setStreamList(streamList);
        return streamRecordBuilder.build();
    }

    public StreamList getOriginalStreamList() {
        StreamListBuilder streamListObj = new StreamListBuilder();
        List<StreamRules> streamRuleList = new ArrayList<StreamRules>();

        StreamRulesBuilder streamRuleBuilder = new StreamRulesBuilder();
        streamRuleBuilder.setField("field");
        streamRuleList.add(streamRuleBuilder.build());

        streamListObj.setTitle("Str001");
        streamListObj.setDescription("stream");
        streamListObj.setStreamID("testID");
        streamListObj.setConfigID("1000");
        streamListObj.setDisabled("false");
        streamListObj.setStreamRules(streamRuleList);
        return streamListObj.build();
    }

    public StreamList getUpdatedStreamList() {
        StreamListBuilder streamListObj = new StreamListBuilder();
        List<StreamRules> streamRuleList = new ArrayList<StreamRules>();

        StreamRulesBuilder streamRuleBuilder = new StreamRulesBuilder();
        streamRuleBuilder.setField("field");
        streamRuleList.add(streamRuleBuilder.build());

        streamListObj.setTitle("Updated Str001");
        streamListObj.setDescription("updated stream");
        streamListObj.setStreamID("testID");
        streamListObj.setConfigID("1000");
        streamListObj.setDisabled("false");
        streamListObj.setStreamRules(streamRuleList);
        return streamListObj.build();
    }

    public Map<InstanceIdentifier<?>, DataObject> getCreatedDataStreamRecord() {
        Map<InstanceIdentifier<?>, DataObject> aMap = new HashMap<InstanceIdentifier<?>, DataObject>();
        aMap.put(streamRecordId, getUpdatedSubtreeStreamRecord());
        return aMap;
    }

    public Set<InstanceIdentifier<?>> getRemovedPathsStreamRecord() {
        Set<InstanceIdentifier<?>> pathSet = new HashSet<InstanceIdentifier<?>>();
        pathSet.add(streamRecordId.child(StreamList.class));
        return pathSet;
    }

    public Map<InstanceIdentifier<?>, DataObject> getOriginalDataStreamList() {
        Map<InstanceIdentifier<?>, DataObject> aMap = new HashMap<InstanceIdentifier<?>, DataObject>();
        aMap.put(streamRecordId.child(StreamList.class), getOriginalStreamList());
        return aMap;
    }

}
