/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.streamhandler.impl;

import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;

public class CommonServicesFactory {

    public JsonObject inputJSON() {
        JsonObject mockJsonObject = Json
                .createObjectBuilder()
                .add("streamId", "John")
                .add("streamCode", "Smith")
                .add("streamData", 25)
                .add("streamInfo",
                        Json.createObjectBuilder().add("streamAddress", "21 2nd Street").add("streamCity", "New York")
                                .add("streamState", "NY").add("streamPostalCode", "10021"))
                .add("streamPhoneNumber",
                        Json.createArrayBuilder()
                                .add(Json.createObjectBuilder().add("streamType", "home")
                                        .add("streamNumber", "212 555-1234"))
                                .add(Json.createObjectBuilder().add("streamType", "fax")
                                        .add("streamNumber", "646 555-4567"))).build();
        return mockJsonObject;
    }

    public String stringGetFromJson() {
        String key = "streamId";
        return key;
    }

    public String inputResponse() {
        String ss = "{\"columns\": [\"EXPR$0\"],\"rows\": [{\"EXPR$0\": \"{\\\"streamId\\\":\\\"John\\\",\\\"streamCode\\\":\\\"Smith\\\",\\\"streamData\\\":25,\\\"streamInfo\\\":{\\\"streamAddress\\\":\\\"21 2nd Street\\\",\\\"streamCity\\\":\\\"New York\\\",\\\"streamState\\\":\\\"NY\\\",\\\"streamPostalCode\\\":\\\"10021\\\"},\\\"streamPhoneNumber\\\":[{\\\"streamType\\\":\\\"home\\\",\\\"streamNumber\\\":\\\"212 555-1234\\\"},{\\\"streamType\\\":\\\"fax\\\",\\\"streamNumber\\\":\\\"646 555-4567\\\"}]}\"}]}";
        return ss;
    }

    public List<String> eventKeys() {
        List<String> keys = new ArrayList<String>();
        keys.add("name");
        keys.add("type");
        return keys;
    }
}
