/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.las.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldContentRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldContentRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldValueRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertFieldValueRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertMessageCountRuleRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertMessageCountRuleRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldcontentrulerecord.StreamAlertFieldContentRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldcontentrulerecord.StreamAlertFieldContentRuleListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldvaluerulerecord.StreamAlertFieldValueRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldvaluerulerecord.StreamAlertFieldValueRuleListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertmessagecountrulerecord.StreamAlertMessageCountRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertmessagecountrulerecord.StreamAlertMessageCountRuleListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rule.rev150105.RuleType;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Abhishek Sharma 
 * 
 * This class builds objects for unit tests of CentinelLaasAlertConditionImpl.
 */
public class CentinelAlertConditionImplObjectFactory {

    final InstanceIdentifier<AlertMessageCountRuleRecord> alertMessageCountRuleRecordId = InstanceIdentifier.builder(
            AlertMessageCountRuleRecord.class).build();

    final InstanceIdentifier<AlertFieldContentRuleRecord> alertFieldContentRuleRecordId = InstanceIdentifier.builder(
            AlertFieldContentRuleRecord.class).build();

    final InstanceIdentifier<AlertFieldValueRuleRecord> alertFieldValuetRuleRecordId = InstanceIdentifier.builder(
            AlertFieldValueRuleRecord.class).build();

    public AlertMessageCountRuleRecord buildAlertMessageCountRuleRecord() {

        StreamAlertMessageCountRuleList streamAlertMessageCountRuleList;

        List<StreamAlertMessageCountRuleList> aList = new ArrayList<StreamAlertMessageCountRuleList>();

        streamAlertMessageCountRuleList = new StreamAlertMessageCountRuleListBuilder()
                .setAlertTypeClassifier(AlertType.AlertMessageCount).setMessageCountBacklog((short) 1)
                .setMessageCountCount((short) 1).setMessageCountGrace((short) 1).setMessageCountOperator("operator")
                .setTimeStamp((short) 1).setNodeType("getNodeType").setRuleID("setRuleID").setConfigID("setConfigID")
                .setAlertName("setAlertName").setRuleTypeClassifier(RuleType.Stream).setStreamID("setStreamID").build();

        aList.add(streamAlertMessageCountRuleList);

        return (AlertMessageCountRuleRecord) new AlertMessageCountRuleRecordBuilder()
                .setStreamAlertMessageCountRuleList(aList).build();

    }
    
    public AlertMessageCountRuleRecord buildAlertMessageCountRuleRecordForUpdate() {

        StreamAlertMessageCountRuleList streamAlertMessageCountRuleList;

        List<StreamAlertMessageCountRuleList> aList = new ArrayList<StreamAlertMessageCountRuleList>();

        streamAlertMessageCountRuleList = new StreamAlertMessageCountRuleListBuilder()
                .setAlertTypeClassifier(AlertType.AlertMessageCount).setMessageCountBacklog((short) 123)
                .setMessageCountCount((short) 123).setMessageCountGrace((short) 123).setMessageCountOperator("operator123")
                .setTimeStamp((short) 123).setNodeType("getNodeType123").setRuleID("setRuleID").setConfigID("setConfigID")
                .setAlertName("setAlertName").setRuleTypeClassifier(RuleType.Stream).setStreamID("setStreamID").build();

        aList.add(streamAlertMessageCountRuleList);

        return (AlertMessageCountRuleRecord) new AlertMessageCountRuleRecordBuilder()
                .setStreamAlertMessageCountRuleList(aList).build();

    }

    public AlertFieldContentRuleRecord buildAlertFieldContentRuleRecord() {

        StreamAlertFieldContentRuleList streamAlertFieldContentRuleList;

        List<StreamAlertFieldContentRuleList> aList = new ArrayList<StreamAlertFieldContentRuleList>();

        streamAlertFieldContentRuleList = new StreamAlertFieldContentRuleListBuilder()
                .setAlertTypeClassifier(AlertType.AlertFieldContent).setFieldContentBacklog((short) 1)
                .setNodeType("setNodeType").setFieldContentCompareToValue("setFieldContentCompareToValue")
                .setFieldContentField("setFieldContentField").setFieldContentGrace((short) 1).setTimeStamp((short) 1)
                .setNodeType("setNodeType").setRuleID("setRuleID").setConfigID("setConfigID")
                .setAlertName("setAlertName").setRuleTypeClassifier(RuleType.Stream).setStreamID("setStreamID").build();

        aList.add(streamAlertFieldContentRuleList);

        return (AlertFieldContentRuleRecord) new AlertFieldContentRuleRecordBuilder()
                .setStreamAlertFieldContentRuleList(aList).build();

    }
    
    public AlertFieldContentRuleRecord buildAlertFieldContentRuleRecordForUpdate() {

        StreamAlertFieldContentRuleList streamAlertFieldContentRuleList;

        List<StreamAlertFieldContentRuleList> aList = new ArrayList<StreamAlertFieldContentRuleList>();

        streamAlertFieldContentRuleList = new StreamAlertFieldContentRuleListBuilder()
                .setAlertTypeClassifier(AlertType.AlertFieldContent).setFieldContentBacklog((short) 123)
                .setNodeType("setNodeType123").setFieldContentCompareToValue("setFieldContentCompareToValue123")
                .setFieldContentField("setFieldContentField123").setFieldContentGrace((short) 1).setTimeStamp((short) 123)
                .setNodeType("setNodeType123").setRuleID("setRuleID").setConfigID("setConfigID")
                .setAlertName("setAlertName").setRuleTypeClassifier(RuleType.Stream).setStreamID("setStreamID").build();

        aList.add(streamAlertFieldContentRuleList);

        return (AlertFieldContentRuleRecord) new AlertFieldContentRuleRecordBuilder()
                .setStreamAlertFieldContentRuleList(aList).build();

    }

    public AlertFieldValueRuleRecord buildAlertFieldValueRuleRecord() {

        StreamAlertFieldValueRuleList streamAlertMessageCountRuleList;

        List<StreamAlertFieldValueRuleList> aList = new ArrayList<StreamAlertFieldValueRuleList>();

        streamAlertMessageCountRuleList = new StreamAlertFieldValueRuleListBuilder()
                .setAlertTypeClassifier(AlertType.AlertFieldValue).setFieldValueBacklog((short) 1)
                .setFieldValueField("setFieldValueField").setFieldValueGrace((short) 1)
                .setFieldValueThreshhold((short) 1).setFieldValueThreshholdType("setFieldValueThreshholdType")
                .setFieldValueType("setFieldValueType").setTimeStamp((short) 1).setNodeType("setNodeType")
                .setRuleID("setRuleID").setConfigID("setConfigID").setAlertName("setAlertName")
                .setRuleTypeClassifier(RuleType.Stream).setStreamID("setStreamID").build();

        aList.add(streamAlertMessageCountRuleList);

        return (AlertFieldValueRuleRecord) new AlertFieldValueRuleRecordBuilder().setStreamAlertFieldValueRuleList(
                aList).build();

    }
    
    public AlertFieldValueRuleRecord buildAlertFieldValueRuleRecordUpdate() {

        StreamAlertFieldValueRuleList streamAlertMessageCountRuleList;

        List<StreamAlertFieldValueRuleList> aList = new ArrayList<StreamAlertFieldValueRuleList>();

        streamAlertMessageCountRuleList = new StreamAlertFieldValueRuleListBuilder()
                .setAlertTypeClassifier(AlertType.AlertFieldValue).setFieldValueBacklog((short) 123)
                .setFieldValueField("setFieldValueField123").setFieldValueGrace((short) 123)
                .setFieldValueThreshhold((short) 123).setFieldValueThreshholdType("setFieldValueThreshholdType123")
                .setFieldValueType("setFieldValueType123").setTimeStamp((short) 123).setNodeType("setNodeType123")
                .setRuleID("setRuleID").setConfigID("setConfigID").setAlertName("setAlertName")
                .setRuleTypeClassifier(RuleType.Stream).setStreamID("setStreamID").build();

        aList.add(streamAlertMessageCountRuleList);

        return (AlertFieldValueRuleRecord) new AlertFieldValueRuleRecordBuilder().setStreamAlertFieldValueRuleList(
                aList).build();

    }
    
    public Map<InstanceIdentifier<?>, DataObject> buildAlertFieldContentRuleRecordMap() {
        Map<InstanceIdentifier<?>, DataObject> aMap = new HashMap<InstanceIdentifier<?>, DataObject>();
        aMap.put(alertFieldContentRuleRecordId, buildAlertFieldContentRuleRecord());
        return aMap;

    }
    
    public Map<InstanceIdentifier<?>, DataObject> buildAlertFieldValueRuleRecordMap() {
        Map<InstanceIdentifier<?>, DataObject> aMap = new HashMap<InstanceIdentifier<?>, DataObject>();
        aMap.put(alertFieldValuetRuleRecordId, buildAlertFieldValueRuleRecord());
        return aMap;

    }
    
    public Map<InstanceIdentifier<?>, DataObject> buildAlertMessageCountRuleRecordMap() {
        Map<InstanceIdentifier<?>, DataObject> aMap = new HashMap<InstanceIdentifier<?>, DataObject>();
        aMap.put(alertMessageCountRuleRecordId, buildAlertMessageCountRuleRecord());
        return aMap;

    }
    
    public Map<InstanceIdentifier<?>, DataObject> buildAlertMessageCountRuleListMap() {
        Map<InstanceIdentifier<?>, DataObject> aMap = new HashMap<InstanceIdentifier<?>, DataObject>();
        aMap.put(alertMessageCountRuleRecordId, buildAlertMessageCountRuleList());
        return aMap;

    }

    public Map<InstanceIdentifier<?>, DataObject> buildAlertFieldValueRuleListMap() {
        Map<InstanceIdentifier<?>, DataObject> aMap = new HashMap<InstanceIdentifier<?>, DataObject>();
        aMap.put(alertFieldValuetRuleRecordId, buildAlertFieldValueRuleList());
        return aMap;

    }

    public Map<InstanceIdentifier<?>, DataObject> buildAlertFieldContentRuleListMap() {
        Map<InstanceIdentifier<?>, DataObject> aMap = new HashMap<InstanceIdentifier<?>, DataObject>();
        aMap.put(alertFieldContentRuleRecordId, buildAlertFieldContentRuleList());
        return aMap;

    }

    public Set<InstanceIdentifier<?>> buildAlertMessageCountRuleRecordSet() {
        Set<InstanceIdentifier<?>> aSet = new HashSet<InstanceIdentifier<?>>();
        aSet.add(alertMessageCountRuleRecordId);
        return aSet;

    }

    public Set<InstanceIdentifier<?>> buildAlertFieldValueRuleRecordSet() {
        Set<InstanceIdentifier<?>> aSet = new HashSet<InstanceIdentifier<?>>();
        aSet.add(alertFieldValuetRuleRecordId);
        return aSet;

    }

    public Set<InstanceIdentifier<?>> buildAlertFieldContentRuleRecordSet() {
        Set<InstanceIdentifier<?>> aSet = new HashSet<InstanceIdentifier<?>>();
        aSet.add(alertFieldContentRuleRecordId);
        return aSet;

    }

    public StreamAlertMessageCountRuleList buildAlertMessageCountRuleList() {

        return new StreamAlertMessageCountRuleListBuilder().setAlertTypeClassifier(AlertType.AlertMessageCount)
                .setMessageCountBacklog((short) 1).setMessageCountCount((short) 1).setMessageCountGrace((short) 1)
                .setMessageCountOperator("operator").setTimeStamp((short) 1).setNodeType("getNodeType")
                .setRuleID("setRuleID").setConfigID("setConfigID").setAlertName("setAlertName")
                .setRuleTypeClassifier(RuleType.Stream).setStreamID("setStreamID").build();

    }

    public StreamAlertFieldValueRuleList buildAlertFieldValueRuleList() {

        return new StreamAlertFieldValueRuleListBuilder().setAlertTypeClassifier(AlertType.AlertFieldValue)
                .setFieldValueBacklog((short) 1).setFieldValueField("setFieldValueField").setFieldValueGrace((short) 1)
                .setFieldValueThreshhold((short) 1).setFieldValueThreshholdType("setFieldValueThreshholdType")
                .setFieldValueType("setFieldValueType").setTimeStamp((short) 1).setNodeType("setNodeType")
                .setRuleID("setRuleID").setConfigID("setConfigID").setAlertName("setAlertName")
                .setRuleTypeClassifier(RuleType.Stream).setStreamID("setStreamID").build();

    }

    public StreamAlertFieldContentRuleList buildAlertFieldContentRuleList() {

        return new StreamAlertFieldContentRuleListBuilder().setAlertTypeClassifier(AlertType.AlertFieldContent)
                .setFieldContentBacklog((short) 1).setNodeType("setNodeType")
                .setFieldContentCompareToValue("setFieldContentCompareToValue")
                .setFieldContentField("setFieldContentField").setFieldContentGrace((short) 1).setTimeStamp((short) 1)
                .setNodeType("setNodeType").setRuleID("setRuleID").setConfigID("setConfigID")
                .setAlertName("setAlertName").setRuleTypeClassifier(RuleType.Stream).setStreamID("setStreamID").build();

    }
    
}
