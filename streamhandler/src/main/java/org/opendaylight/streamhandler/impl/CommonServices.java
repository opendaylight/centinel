/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author This class will provide access point to the common REST services
 *         exposed by
 */
public class CommonServices {

    Client client = null;
    JsonBuilderFactory factory = null;
    Properties configProperties;

    private static final Logger LOG = LoggerFactory.getLogger(CommonServices.class);
    private static CommonServices singleton = null;
    String flumeHostname = null;
    String flumePort = null;
    String drillHostname = null;
    String drillPort = null;
    String dbType = null;
    String defaultLimit = null;
    String graylogHostname = null;
    String syslogPort = null;

    private CommonServices() {
        super();
        client = Client.create();
        factory = Json.createBuilderFactory(null);
        loadPropertiesFiles();
        loadPropertiesValues();

    }

    /**
     * Provide single instance of CentinelStreamConditionRESTServices across the
     * application throughout its life cycle
     */
    public static synchronized CommonServices getInstance() {
        if (singleton == null) {
            singleton = new CommonServices();
        }
        return singleton;
    }

    /**
     * @param obj
     * @param resource
     * @return ClientResponse
     * 
     *         Utility method to send HTTP POST request to drill
     */
    public ClientResponse drillRESTPost(Map<String, String> drillQuery, String hostname, String port) {
        JsonObject obj = factory.createObjectBuilder()
                .add(StreamConstants.QUERY_TYPE, drillQuery.get(StreamConstants.QUERY_TYPE))
                .add(StreamConstants.QUERY, drillQuery.get(StreamConstants.QUERY)).build();
        WebResource webResource = client.resource("http://" + hostname + StreamConstants.COLON + port + "/query.json");
        return webResource.header("content-type", MediaType.APPLICATION_JSON).post(ClientResponse.class,
                obj.toString());

    }

    /**
     * @param response
     * @param key
     * @return String Parsing the HTTP method response
     */
    public List<Map<String, Object>> parseResponse(String response, List<String> eventKey) {
        JsonReader reader = Json.createReader(new StringReader(response));
        JsonObject personObject = reader.readObject();
        reader.close();

        JsonArray jsonArray = (JsonArray) personObject.get("rows");

        List<Map<String, Object>> listOut = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < jsonArray.toArray().length; i++) {
            Map<String, Object> output = null;
            Map<String, Object> out = new HashMap<String, Object>();

            LOG.info("jsonArray: " + jsonArray.getJsonObject(i));

            if (jsonArray.getJsonObject(i).keySet().iterator().hasNext()) {
                String row = jsonArray.getJsonObject(i).get(jsonArray.getJsonObject(i).keySet().iterator().next())
                        .toString();
                LOG.info(" row ::" + row);
                row = row.replace("\\", StreamConstants.BLANK);
                row = row.replaceFirst("\"", StreamConstants.BLANK);
                row = row.replace("}\"", "}");

                reader = Json.createReader(new StringReader(row));

                output = parseJsonObject(reader.readObject(), StreamConstants.BLANK);
                reader.close();

                if (eventKey != null) {
                    for (String key : eventKey) {
                        out.put(key, output.get(key));
                    }
                } else {
                    out = output;
                }
                listOut.add(out);
            } else {
                LOG.error("Drill response is empty");
            }
        }
        return listOut;
    }

    /**
     * To load property file
     */
    public void loadPropertiesFiles() {
        configProperties = new Properties();
        try {

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Configuration.properties");
            configProperties.load(inputStream);
            LOG.info("Properties files for Stream Handler loaded successfully");
        } catch (IOException e) {
            LOG.error("Problem while loading Properties file for Stream Handller: "+ e.getMessage(), e);
        }
    }

    private Map<String, Object> parseJsonObject(JsonObject jsonObj, String str) {
        Map<String, Object> out = new HashMap<String, Object>();
        for (Object key : jsonObj.keySet()) {

            String keyStr = (String) key;
            Object keyvalue = jsonObj.get(keyStr);

            // for nested objects iteration if required
            if (keyvalue instanceof JsonObject) {
                if (StreamConstants.BLANK.equals(str)) {
                    out.putAll(parseJsonObject((JsonObject) keyvalue, keyStr));
                } else {
                    out.putAll(parseJsonObject((JsonObject) keyvalue, str + StreamConstants.COLON + keyStr));
                }
            } else {
                keyvalue = (keyvalue).toString().replace("\"", StreamConstants.BLANK);
                if (StreamConstants.BLANK.equals(str)) {
                    out.put(keyStr, keyvalue);
                } else {
                    out.put(str + StreamConstants.COLON + keyStr, keyvalue);
                }
            }
        }
        return out;
    }

    public void loadPropertiesValues() {
        flumeHostname = configProperties.getProperty("flume.hostname");
        flumePort = configProperties.getProperty("flume.port");
        drillHostname = configProperties.getProperty("drill.hostname");
        drillPort = configProperties.getProperty("drill.port");
        dbType = configProperties.getProperty("db.type");
        defaultLimit = configProperties.getProperty("default.limit");
        graylogHostname = configProperties.getProperty("graylog_server_ip");
        syslogPort = configProperties.getProperty("syslog.port");
    }

    public String matchRegEx(String query) {
        String regex = "([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2})_([0-9]{2})_([0-9]{2})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query);
        while (matcher.find()) {
            String str = matcher.group();
            str = str.replace(StreamConstants.UNDERSCORE, StreamConstants.COLON);
            query = query.replaceFirst(regex, str);
        }
        return query;
    }

}
