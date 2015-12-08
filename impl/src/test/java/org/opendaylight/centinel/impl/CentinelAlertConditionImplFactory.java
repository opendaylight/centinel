/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.AlertType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertFieldContentRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertFieldContentRuleInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertFieldValueRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertFieldValueRuleInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertMessageCountRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.DeleteAlertMessageCountRuleInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.GetAllAlertRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.GetAllAlertRuleInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.GetAllAlertRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.GetAllAlertRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldContentRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldContentRuleInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldContentRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldContentRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldValueRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldValueRuleInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldValueRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertFieldValueRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertMessageCountRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertMessageCountRuleInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertMessageCountRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.SetAlertMessageCountRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertFieldContentRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertFieldContentRuleInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertFieldValueRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertFieldValueRuleInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertMessageCountRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertMessageCountRuleInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertMessageCountRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.UpdateAlertMessageCountRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldcontentrulerecord.StreamAlertFieldContentRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldcontentrulerecord.StreamAlertFieldContentRuleListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldvaluerulerecord.StreamAlertFieldValueRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertfieldvaluerulerecord.StreamAlertFieldValueRuleListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertmessagecountrulerecord.StreamAlertMessageCountRuleList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.alertrule.rev150105.alertmessagecountrulerecord.StreamAlertMessageCountRuleListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rule.rev150105.RuleType;

/**
 * @author Amandeep Singh Rattenpal
 *
 *         This class provides the dummy params for the different scenarios.
 *
 */

public class CentinelAlertConditionImplFactory {

    /**
     * Expected dummy params for SetAlertMessaegCount
     */

    public SetAlertMessageCountRuleOutput expectedAlertMessageCountRuleObject() {
        SetAlertMessageCountRuleOutputBuilder setAlertMessageCountRuleOutputBuilder = new SetAlertMessageCountRuleOutputBuilder();

        setAlertMessageCountRuleOutputBuilder.setConfigID("1000");
        setAlertMessageCountRuleOutputBuilder.setMessageCountOperator("hello");
        setAlertMessageCountRuleOutputBuilder.setNodeType("more");
        setAlertMessageCountRuleOutputBuilder.setRuleID("rule1");
        setAlertMessageCountRuleOutputBuilder.setStreamID("stream1");
        setAlertMessageCountRuleOutputBuilder.setAlertTypeClassifier(AlertType.AlertMessageCount);
        setAlertMessageCountRuleOutputBuilder.setMessageCountBacklog((short) 18);
        setAlertMessageCountRuleOutputBuilder.setMessageCountCount((short) 15);
        setAlertMessageCountRuleOutputBuilder.setMessageCountGrace((short) 45);
        setAlertMessageCountRuleOutputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        setAlertMessageCountRuleOutputBuilder.setTimeStamp((short) 4);

        return setAlertMessageCountRuleOutputBuilder.build();
    }

    /**
     * Invalid dummy params for SetAlertMessaegCount
     */

    public SetAlertMessageCountRuleInput setInputBuilderWithMissingInput() {
        SetAlertMessageCountRuleInputBuilder setAlertMessageCountRuleInputBuilder = new SetAlertMessageCountRuleInputBuilder();
        setAlertMessageCountRuleInputBuilder.setConfigID(null);
        setAlertMessageCountRuleInputBuilder.setMessageCountOperator(null);
        setAlertMessageCountRuleInputBuilder.setNodeType(null);
        setAlertMessageCountRuleInputBuilder.setRuleID(null);
        setAlertMessageCountRuleInputBuilder.setStreamID(null);
        setAlertMessageCountRuleInputBuilder.setAlertTypeClassifier(null);
        setAlertMessageCountRuleInputBuilder.setMessageCountBacklog((short) 18);
        setAlertMessageCountRuleInputBuilder.setMessageCountCount((short) 15);
        setAlertMessageCountRuleInputBuilder.setMessageCountGrace((short) 45);
        setAlertMessageCountRuleInputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        setAlertMessageCountRuleInputBuilder.setTimeStamp((short) 4);
        return setAlertMessageCountRuleInputBuilder.build();
    }

    /**
     * Provided dummy params for SetAlertMessaegCount
     */

    public SetAlertMessageCountRuleInput setInputForAlertMessageCount() {
        SetAlertMessageCountRuleInputBuilder setAlertMessageCountRuleInputBuilder = new SetAlertMessageCountRuleInputBuilder();
        setAlertMessageCountRuleInputBuilder.setConfigID("1000");
        setAlertMessageCountRuleInputBuilder.setMessageCountOperator("hello");
        setAlertMessageCountRuleInputBuilder.setNodeType("more");
        setAlertMessageCountRuleInputBuilder.setRuleID("rule1");
        setAlertMessageCountRuleInputBuilder.setStreamID("stream1");
        setAlertMessageCountRuleInputBuilder.setAlertTypeClassifier(AlertType.AlertMessageCount);
        setAlertMessageCountRuleInputBuilder.setMessageCountBacklog((short) 18);
        setAlertMessageCountRuleInputBuilder.setMessageCountCount((short) 15);
        setAlertMessageCountRuleInputBuilder.setMessageCountGrace((short) 45);
        setAlertMessageCountRuleInputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        setAlertMessageCountRuleInputBuilder.setTimeStamp((short) 4);
        return setAlertMessageCountRuleInputBuilder.build();
    }

    /**
     * Expected dummy params for SetAlertFieldvalue
     */

    public SetAlertFieldValueRuleOutput expectedAlertFieldValueRuleObject() {
        SetAlertFieldValueRuleOutputBuilder setAlertFieldValueRuleOutputBuilder = new SetAlertFieldValueRuleOutputBuilder();

        setAlertFieldValueRuleOutputBuilder.setConfigID("1000");
        setAlertFieldValueRuleOutputBuilder.setFieldValueField("hello");
        setAlertFieldValueRuleOutputBuilder.setNodeType("more");
        setAlertFieldValueRuleOutputBuilder.setRuleID("rule1");
        setAlertFieldValueRuleOutputBuilder.setStreamID("stream1");
        setAlertFieldValueRuleOutputBuilder.setAlertTypeClassifier(AlertType.AlertFieldValue);
        setAlertFieldValueRuleOutputBuilder.setFieldValueBacklog((short) 12);
        setAlertFieldValueRuleOutputBuilder.setFieldValueThreshhold((short) 34);
        setAlertFieldValueRuleOutputBuilder.setFieldValueThreshholdType("t2");
        setAlertFieldValueRuleOutputBuilder.setFieldValueGrace((short) 1);
        setAlertFieldValueRuleOutputBuilder.setFieldValueType("type1");
        setAlertFieldValueRuleOutputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        setAlertFieldValueRuleOutputBuilder.setTimeStamp((short) 4);

        return setAlertFieldValueRuleOutputBuilder.build();
    }

    /**
     * Provided dummy params for SetAlertFieldvalue
     */

    public SetAlertFieldValueRuleInput setInputForAlertFieldValueRule() {
        SetAlertFieldValueRuleInputBuilder alertFieldValueRuleInputBuilder = new SetAlertFieldValueRuleInputBuilder();

        alertFieldValueRuleInputBuilder.setConfigID("1000");
        alertFieldValueRuleInputBuilder.setFieldValueField("hello");
        alertFieldValueRuleInputBuilder.setNodeType("more");
        alertFieldValueRuleInputBuilder.setRuleID("rule1");
        alertFieldValueRuleInputBuilder.setStreamID("stream1");
        alertFieldValueRuleInputBuilder.setAlertTypeClassifier(AlertType.AlertFieldValue);
        alertFieldValueRuleInputBuilder.setFieldValueBacklog((short) 12);
        alertFieldValueRuleInputBuilder.setFieldValueThreshhold((short) 34);
        alertFieldValueRuleInputBuilder.setFieldValueGrace((short) 1);
        alertFieldValueRuleInputBuilder.setFieldValueThreshholdType("t2");
        alertFieldValueRuleInputBuilder.setFieldValueType("type1");
        alertFieldValueRuleInputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        alertFieldValueRuleInputBuilder.setTimeStamp((short) 4);

        return alertFieldValueRuleInputBuilder.build();
    }

    /**
     * Invalid dummy params for SetAlertFieldvalue
     */

    public SetAlertFieldValueRuleInput setInputForAlertFieldValueWithInvalidInput() {
        SetAlertFieldValueRuleInputBuilder alertFieldValueRuleInputBuilder = new SetAlertFieldValueRuleInputBuilder();

        alertFieldValueRuleInputBuilder.setConfigID(null);
        alertFieldValueRuleInputBuilder.setFieldValueField(null);
        alertFieldValueRuleInputBuilder.setNodeType(null);
        alertFieldValueRuleInputBuilder.setRuleID(null);
        alertFieldValueRuleInputBuilder.setStreamID(null);
        alertFieldValueRuleInputBuilder.setAlertTypeClassifier(AlertType.AlertFieldValue);
        alertFieldValueRuleInputBuilder.setFieldValueBacklog(null);
        alertFieldValueRuleInputBuilder.setFieldValueThreshhold(null);
        alertFieldValueRuleInputBuilder.setFieldValueGrace(null);
        alertFieldValueRuleInputBuilder.setFieldValueThreshholdType(null);
        alertFieldValueRuleInputBuilder.setFieldValueType(null);
        alertFieldValueRuleInputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        alertFieldValueRuleInputBuilder.setTimeStamp(null);

        return alertFieldValueRuleInputBuilder.build();
    }

    /**
     * Expected dummy params for SetAlertFieldContent
     */
    public SetAlertFieldContentRuleOutput expectedAlertFieldContentRuleObject() {
        SetAlertFieldContentRuleOutputBuilder setAlertFieldContentRuleOutputBuilder = new SetAlertFieldContentRuleOutputBuilder();

        setAlertFieldContentRuleOutputBuilder.setConfigID("1000");
        setAlertFieldContentRuleOutputBuilder.setFieldContentCompareToValue("one");
        setAlertFieldContentRuleOutputBuilder.setNodeType("more");
        setAlertFieldContentRuleOutputBuilder.setRuleID("rule1");
        setAlertFieldContentRuleOutputBuilder.setStreamID("stream1");
        setAlertFieldContentRuleOutputBuilder.setAlertTypeClassifier(AlertType.AlertFieldContent);
        setAlertFieldContentRuleOutputBuilder.setFieldContentField("new");
        setAlertFieldContentRuleOutputBuilder.setFieldContentBacklog((short) 45);
        setAlertFieldContentRuleOutputBuilder.setFieldContentGrace((short) 65);
        setAlertFieldContentRuleOutputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        setAlertFieldContentRuleOutputBuilder.setTimeStamp((short) 7);

        return setAlertFieldContentRuleOutputBuilder.build();
    }

    /**
     * Provided dummy params for SetAlertFieldContent
     */

    public SetAlertFieldContentRuleInput setInputForAlertFieldContentRule() {
        SetAlertFieldContentRuleInputBuilder setAlertFieldContentRuleInputBuilder = new SetAlertFieldContentRuleInputBuilder();

        setAlertFieldContentRuleInputBuilder.setFieldContentCompareToValue("one");
        setAlertFieldContentRuleInputBuilder.setConfigID("1000");
        setAlertFieldContentRuleInputBuilder.setNodeType("more");
        setAlertFieldContentRuleInputBuilder.setRuleID("rule1");
        setAlertFieldContentRuleInputBuilder.setStreamID("stream1");
        setAlertFieldContentRuleInputBuilder.setAlertTypeClassifier(AlertType.AlertFieldContent);
        setAlertFieldContentRuleInputBuilder.setFieldContentField("new");
        setAlertFieldContentRuleInputBuilder.setFieldContentBacklog((short) 45);
        setAlertFieldContentRuleInputBuilder.setFieldContentGrace((short) 65);
        setAlertFieldContentRuleInputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        setAlertFieldContentRuleInputBuilder.setTimeStamp((short) 7);
        return setAlertFieldContentRuleInputBuilder.build();
    }

    /**
     * Invalid dummy params for SetAlertFieldContent
     */

    public SetAlertFieldContentRuleInput setInputForAlertFieldContentWithInvalidInput() {
        SetAlertFieldContentRuleInputBuilder setAlertFieldContentRuleInputBuilder = new SetAlertFieldContentRuleInputBuilder();

        setAlertFieldContentRuleInputBuilder.setFieldContentCompareToValue(null);
        setAlertFieldContentRuleInputBuilder.setConfigID(null);
        setAlertFieldContentRuleInputBuilder.setNodeType(null);
        setAlertFieldContentRuleInputBuilder.setRuleID(null);
        setAlertFieldContentRuleInputBuilder.setStreamID(null);
        setAlertFieldContentRuleInputBuilder.setAlertTypeClassifier(AlertType.AlertFieldContent);
        setAlertFieldContentRuleInputBuilder.setFieldContentField(null);
        setAlertFieldContentRuleInputBuilder.setFieldContentBacklog(null);
        setAlertFieldContentRuleInputBuilder.setFieldContentGrace(null);
        setAlertFieldContentRuleInputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        setAlertFieldContentRuleInputBuilder.setTimeStamp(null);
        return setAlertFieldContentRuleInputBuilder.build();
    }

    /**
     * Providing dummy params for expectedUpdatedAlertMessageCountRuleObject
     */

    public UpdateAlertMessageCountRuleOutput expectedUpdatedAlertMessageCountRuleObject() {
        UpdateAlertMessageCountRuleOutputBuilder updateAlertMessageCountRuleOutputBuilder = new UpdateAlertMessageCountRuleOutputBuilder();

        updateAlertMessageCountRuleOutputBuilder.setConfigID("1000");
        updateAlertMessageCountRuleOutputBuilder.setMessageCountOperator("hello");
        updateAlertMessageCountRuleOutputBuilder.setNodeType("more");
        updateAlertMessageCountRuleOutputBuilder.setRuleID("rule1");
        updateAlertMessageCountRuleOutputBuilder.setStreamID("stream1");
        updateAlertMessageCountRuleOutputBuilder.setAlertTypeClassifier(AlertType.AlertMessageCount);
        updateAlertMessageCountRuleOutputBuilder.setMessageCountBacklog((short) 18);
        updateAlertMessageCountRuleOutputBuilder.setMessageCountCount((short) 15);
        updateAlertMessageCountRuleOutputBuilder.setMessageCountGrace((short) 45);
        updateAlertMessageCountRuleOutputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        updateAlertMessageCountRuleOutputBuilder.setTimeStamp((short) 4);

        return updateAlertMessageCountRuleOutputBuilder.build();
    }

    /**
     * Providing dummy params for updateInputForAlertMessageCount
     */

    public UpdateAlertMessageCountRuleInput updateInputForAlertMessageCount() {
        UpdateAlertMessageCountRuleInputBuilder updateAlertMessageCountRuleInputBuilder = new UpdateAlertMessageCountRuleInputBuilder();
        updateAlertMessageCountRuleInputBuilder.setConfigID("1000");
        updateAlertMessageCountRuleInputBuilder.setMessageCountOperator("hello");
        updateAlertMessageCountRuleInputBuilder.setNodeType("more");
        updateAlertMessageCountRuleInputBuilder.setRuleID("rule1");
        updateAlertMessageCountRuleInputBuilder.setStreamID("stream1");
        updateAlertMessageCountRuleInputBuilder.setAlertTypeClassifier(AlertType.AlertMessageCount);
        updateAlertMessageCountRuleInputBuilder.setMessageCountBacklog((short) 18);
        updateAlertMessageCountRuleInputBuilder.setMessageCountCount((short) 15);
        updateAlertMessageCountRuleInputBuilder.setMessageCountGrace((short) 45);
        updateAlertMessageCountRuleInputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        updateAlertMessageCountRuleInputBuilder.setTimeStamp((short) 4);
        return updateAlertMessageCountRuleInputBuilder.build();
    }

    /**
     * Providing dummy params for
     * updateInputForAlertMessageCountWithInvalidValues
     */

    public UpdateAlertMessageCountRuleInput updateInputForAlertMessageCountWithInvalidValues() {
        UpdateAlertMessageCountRuleInputBuilder updateAlertMessageCountRuleInputBuilder = new UpdateAlertMessageCountRuleInputBuilder();
        updateAlertMessageCountRuleInputBuilder.setConfigID("1000");
        updateAlertMessageCountRuleInputBuilder.setMessageCountOperator("hello");
        updateAlertMessageCountRuleInputBuilder.setNodeType("more");
        updateAlertMessageCountRuleInputBuilder.setStreamID("stream1");
        updateAlertMessageCountRuleInputBuilder.setAlertTypeClassifier(AlertType.AlertMessageCount);
        updateAlertMessageCountRuleInputBuilder.setMessageCountBacklog((short) 18);
        updateAlertMessageCountRuleInputBuilder.setMessageCountCount((short) 15);
        updateAlertMessageCountRuleInputBuilder.setMessageCountGrace((short) 45);
        updateAlertMessageCountRuleInputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        updateAlertMessageCountRuleInputBuilder.setTimeStamp((short) 4);
        return updateAlertMessageCountRuleInputBuilder.build();
    }

    /**
     * Mocking params of StreamAlertMessageCountRule
     */

    public StreamAlertMessageCountRuleList mockStreamAlertMessageCountRuleObjectBuilder() {
        StreamAlertMessageCountRuleListBuilder streamAlertMessageCountRuleListObj = new StreamAlertMessageCountRuleListBuilder();
        streamAlertMessageCountRuleListObj.setConfigID("1000");
        streamAlertMessageCountRuleListObj.setMessageCountOperator("hello");
        streamAlertMessageCountRuleListObj.setNodeType("more");
        streamAlertMessageCountRuleListObj.setRuleID("E99uI");
        streamAlertMessageCountRuleListObj.setStreamID("stream4");
        streamAlertMessageCountRuleListObj.setAlertTypeClassifier(AlertType.AlertMessageCount);
        streamAlertMessageCountRuleListObj.setMessageCountBacklog((short) 18);
        streamAlertMessageCountRuleListObj.setMessageCountCount((short) 15);
        streamAlertMessageCountRuleListObj.setMessageCountGrace((short) 45);
        streamAlertMessageCountRuleListObj.setRuleTypeClassifier(RuleType.BaseAlert);
        streamAlertMessageCountRuleListObj.setTimeStamp((short) 4);
        return streamAlertMessageCountRuleListObj.build();
    }

    /**
     * Mocking params of StreamAlertFieldValueRule
     */

    public StreamAlertFieldValueRuleList mockStreamFieldValueRuleObjectBuilder() {
        StreamAlertFieldValueRuleListBuilder streamAlertFieldValueRuleListObj = new StreamAlertFieldValueRuleListBuilder();
        streamAlertFieldValueRuleListObj.setConfigID("1000");
        streamAlertFieldValueRuleListObj.setNodeType("more");
        streamAlertFieldValueRuleListObj.setRuleID("E99uI");
        streamAlertFieldValueRuleListObj.setStreamID("str");
        streamAlertFieldValueRuleListObj.setAlertTypeClassifier(AlertType.AlertMessageCount);
        streamAlertFieldValueRuleListObj.setRuleTypeClassifier(RuleType.BaseAlert);
        streamAlertFieldValueRuleListObj.setTimeStamp((short) 4);
        return streamAlertFieldValueRuleListObj.build();
    }

    /**
     * Mocking params of StreamAlertFieldContentRule
     */

    public StreamAlertFieldContentRuleList mockStreamAlertFieldContentRuleObjectBuilder() {
        StreamAlertFieldContentRuleListBuilder streamAlertFieldContentRuleListObj = new StreamAlertFieldContentRuleListBuilder();
        streamAlertFieldContentRuleListObj.setConfigID("1000");
        streamAlertFieldContentRuleListObj.setNodeType("more");
        streamAlertFieldContentRuleListObj.setRuleID("rule1");
        streamAlertFieldContentRuleListObj.setStreamID("stream4");
        streamAlertFieldContentRuleListObj.setAlertTypeClassifier(AlertType.AlertMessageCount);
        streamAlertFieldContentRuleListObj.setRuleTypeClassifier(RuleType.BaseAlert);
        streamAlertFieldContentRuleListObj.setTimeStamp((short) 4);
        return streamAlertFieldContentRuleListObj.build();
    }

    /**
     * Providing dummy params for ValidValuesForAlertFieldContentRule
     */

    public UpdateAlertFieldContentRuleInput updateInputWithValidValuesForAlertFieldContentRule() {
        UpdateAlertFieldContentRuleInputBuilder updateAlertFieldContentRuleInputBuilder = new UpdateAlertFieldContentRuleInputBuilder();
        updateAlertFieldContentRuleInputBuilder.setConfigID("1000");
        updateAlertFieldContentRuleInputBuilder.setNodeType("more");
        updateAlertFieldContentRuleInputBuilder.setRuleID("rule1");
        updateAlertFieldContentRuleInputBuilder.setStreamID("stream1");
        updateAlertFieldContentRuleInputBuilder.setAlertTypeClassifier(AlertType.AlertMessageCount);
        updateAlertFieldContentRuleInputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        updateAlertFieldContentRuleInputBuilder.setTimeStamp((short) 4);
        return updateAlertFieldContentRuleInputBuilder.build();
    }

    /**
     * Providing dummy params for InvalidValuesForAlertFieldContentRule
     */

    public UpdateAlertFieldContentRuleInput updateInputWithInvalidValuesForAlertFieldContentRule() {
        UpdateAlertFieldContentRuleInputBuilder updateAlertFieldContentRuleInputBuilder = new UpdateAlertFieldContentRuleInputBuilder();
        updateAlertFieldContentRuleInputBuilder.setConfigID("1000");
        updateAlertFieldContentRuleInputBuilder.setNodeType("more");
        updateAlertFieldContentRuleInputBuilder.setRuleID("rule1");
        updateAlertFieldContentRuleInputBuilder.setAlertTypeClassifier(AlertType.AlertMessageCount);
        updateAlertFieldContentRuleInputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        updateAlertFieldContentRuleInputBuilder.setTimeStamp((short) 4);
        return updateAlertFieldContentRuleInputBuilder.build();
    }

    /**
     * Providing dummy params for ValidValuesForAlertFieldValueRule
     */

    public UpdateAlertFieldValueRuleInput updateInputWithValidValuesForAlertFieldValueRule() {
        UpdateAlertFieldValueRuleInputBuilder updateAlertFieldValueInputBuilder = new UpdateAlertFieldValueRuleInputBuilder();
        updateAlertFieldValueInputBuilder.setConfigID("1000");
        updateAlertFieldValueInputBuilder.setNodeType("more");
        updateAlertFieldValueInputBuilder.setRuleID("rule1");
        updateAlertFieldValueInputBuilder.setStreamID("stream1");
        updateAlertFieldValueInputBuilder.setAlertTypeClassifier(AlertType.AlertMessageCount);
        updateAlertFieldValueInputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        updateAlertFieldValueInputBuilder.setTimeStamp((short) 4);
        return updateAlertFieldValueInputBuilder.build();
    }

    /**
     * Providing dummy params for InvalidValuesForAlertFieldValueRule
     */

    public UpdateAlertFieldValueRuleInput updateInputWithInvalidValuesForAlertFieldValueRule() {
        UpdateAlertFieldValueRuleInputBuilder updateAlertFieldValueInputBuilder = new UpdateAlertFieldValueRuleInputBuilder();
        updateAlertFieldValueInputBuilder.setConfigID("1000");
        updateAlertFieldValueInputBuilder.setNodeType("more");
        updateAlertFieldValueInputBuilder.setRuleID("rule1");
        updateAlertFieldValueInputBuilder.setAlertTypeClassifier(AlertType.AlertMessageCount);
        updateAlertFieldValueInputBuilder.setRuleTypeClassifier(RuleType.BaseAlert);
        updateAlertFieldValueInputBuilder.setTimeStamp((short) 4);
        return updateAlertFieldValueInputBuilder.build();
    }

    /**
     * Providing dummy params for getAllRuleInputWithInvalidValues
     */

    public GetAllAlertRuleInput getAllRuleInputWithInvalidValues() {
        GetAllAlertRuleInputBuilder getallAlertRuleInputBuilder = new GetAllAlertRuleInputBuilder();
        getallAlertRuleInputBuilder.setStreamID(null);
        return getallAlertRuleInputBuilder.build();
    }

    /**
     * Providing dummy params for getAllRuleInputWithValidValues
     */

    public GetAllAlertRuleInput getAllRulesInputWithValidValues() {
        GetAllAlertRuleInputBuilder getallAlertRuleInputBuilder = new GetAllAlertRuleInputBuilder();
        getallAlertRuleInputBuilder.setStreamID("stream1");
        return getallAlertRuleInputBuilder.build();
    }

    /**
     * Mocking params of GetAllRule
     */

    public GetAllAlertRuleOutput mockGetAllAlertRuleOutputObjectBuilder() {
        GetAllAlertRuleOutputBuilder getAllAlertRuleOutputBuilderObj = new GetAllAlertRuleOutputBuilder();

        return getAllAlertRuleOutputBuilderObj.build();
    }

    /**
     * Providing dummy params for
     * deleteInputWithInvalidValuesForAlertMessageCount
     */

    public DeleteAlertMessageCountRuleInput deleteInputWithInvalidValuesForAlertMessageCount() {
        DeleteAlertMessageCountRuleInputBuilder deleteAlertMessageCountRuleInputBuilder = new DeleteAlertMessageCountRuleInputBuilder();
        deleteAlertMessageCountRuleInputBuilder.setStreamID("stream1");
        return deleteAlertMessageCountRuleInputBuilder.build();
    }

    /**
     * Providing dummy params for deleteInputWithValidValuesForAlertMessageCount
     */

    public DeleteAlertMessageCountRuleInput deleteInputValidValuesForAlertMessageCount() {
        DeleteAlertMessageCountRuleInputBuilder deleteAlertMessageCountRuleInputBuilder = new DeleteAlertMessageCountRuleInputBuilder();
        deleteAlertMessageCountRuleInputBuilder.setStreamID("stream1");
        deleteAlertMessageCountRuleInputBuilder.setRuleID("E99uI");
        deleteAlertMessageCountRuleInputBuilder.setConfigID("1000");
        return deleteAlertMessageCountRuleInputBuilder.build();
    }

    /**
     * Providing dummy params for deleteInputWithInvalidValuesForAlertFieldValue
     */

    public DeleteAlertFieldValueRuleInput deleteInputWithInvalidValuesForAlertFieldValue() {
        DeleteAlertFieldValueRuleInputBuilder deleteAlertFieldValueRuleInputBuilder = new DeleteAlertFieldValueRuleInputBuilder();
        deleteAlertFieldValueRuleInputBuilder.setStreamID("stream1");
        return deleteAlertFieldValueRuleInputBuilder.build();
    }

    /**
     * Providing dummy params for deleteInputWithValidValuesForAlertFieldValue
     */

    public DeleteAlertFieldValueRuleInput deleteInputValidValuesForAlertFieldValue() {
        DeleteAlertFieldValueRuleInputBuilder deleteAlertFieldValueRuleInputBuilder = new DeleteAlertFieldValueRuleInputBuilder();
        deleteAlertFieldValueRuleInputBuilder.setStreamID("stream1");
        deleteAlertFieldValueRuleInputBuilder.setRuleID("E99uI");
        deleteAlertFieldValueRuleInputBuilder.setConfigID("1000");
        return deleteAlertFieldValueRuleInputBuilder.build();
    }

    /**
     * Providing dummy params for
     * deleteInputWithInvalidValuesForAlertFieldContent
     */

    public DeleteAlertFieldContentRuleInput deleteInputWithInvalidValuesForAlertFieldContent() {
        DeleteAlertFieldContentRuleInputBuilder deleteAlertFieldContentRuleInputBuilder = new DeleteAlertFieldContentRuleInputBuilder();
        deleteAlertFieldContentRuleInputBuilder.setStreamID("stream1");
        return deleteAlertFieldContentRuleInputBuilder.build();
    }

    /**
     * Providing dummy params for deleteInputWithValidValuesForAlertFieldContent
     */

    public DeleteAlertFieldContentRuleInput deleteInputValidValuesForAlertFieldContent() {
        DeleteAlertFieldContentRuleInputBuilder deleteAlertFieldContentRuleInputBuilder = new DeleteAlertFieldContentRuleInputBuilder();
        deleteAlertFieldContentRuleInputBuilder.setStreamID("strea1");
        deleteAlertFieldContentRuleInputBuilder.setRuleID("E99uI");
        deleteAlertFieldContentRuleInputBuilder.setConfigID("1000");
        return deleteAlertFieldContentRuleInputBuilder.build();
    }

}
