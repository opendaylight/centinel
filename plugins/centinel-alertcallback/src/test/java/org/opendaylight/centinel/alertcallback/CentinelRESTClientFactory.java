/*
 * Copyright (c) 2016 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.alertcallback;

import java.util.HashMap;
import java.util.Map;

public class CentinelRESTClientFactory {

    public String inputCreatoruserid() {
        return "centinel";
    }

    public String inputMatchingtype() {
        return "stringdata";
    }

    public String inputDescription() {
        return "centinel code testing";
    }

    public boolean inputDisabled() {
        return true;
    }

    public String inputId() {
        return "1001";
    }

    public String inputTitle() {
        return "CENTINEL";
    }

    public String inputFields() {
        return "stream";
    }

    public String inputStreamId() {
        return "1001";
    }

    public String inputType() {
        return "ALERT";
    }

    public boolean inputInvertied() {
        return true;
    }

    public String inputValue() {
        return "StreamRule";
    }

    public String inputIndex() {
        return "1";
    }

    public String inputMessage() {
        return "Stream created successfully";
    }

    public String inputSource() {
        return "Network";
    }

    public String inputTimestamp() {
        return "18-MAR-2016";
    }

    public Map<String, Object> inputFieldsMap() {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("1", "stream");
        fields.put("2", "alert");
        return fields;
    }

    public Map<String, Object> inputParameter() {
        Map<String, Object> setParameter = new HashMap<String, Object>();
        setParameter.put("1", "streamId");
        return setParameter;
    }

    public String inputCreatedAt() {
        return "centinel";
    }

    public int inputGrace() {
        return 1;
    }

    public int inputBacklog() {
        return 1;
    }
}
