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

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.ConfigurationChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/*
 * @author Sumit kapoor
 * This class provides common utility services.
 */
public class CommonServices {

    Client client = null;
    JsonBuilderFactory factory = null;
    Properties configProperties;

    private static final Logger LOG = LoggerFactory.getLogger(CommonServices.class);
    private static CommonServices singleton = null;
    String flumeHostname = "localhost";
    String flumePort = "41414";
    String drillHostname = "localhost";
    String drillPort = "8047";
    String dbType = null;
    String defaultLimit = null;
    String graylogHostname = "localhost";
    String syslogPort = "1514";
    boolean secureSyslog = false;
    String gelfPort;
    String tlsSecurityType = null;
    String tlsServerKeyPath = null;
    String tlsClientCertKeyPath = null;
    String tlsServerKeyPwd = null;
    String tlsClientCertPwd = null;
    String tlsParingAlgo = null;
    String tlsEncodeAlgo = null;

    private CommonServices() {
        super();
        client = Client.create();
        factory = Json.createBuilderFactory(null);
        loadPropertiesFiles();
        loadPropertiesValues();

    }

    /**
     * Provide single instance of CentinelStreamConditionRESTServices across then
     * application throughout its life cycle.
     * @return
     *         Returns singelton.
     */
    public static synchronized CommonServices getInstance() {
        if (singleton == null) {
            singleton = new CommonServices();
        }
        return singleton;
    }

    /**
     * @param drillQuery
     *            Drill query.
     * @param hostname
     *            Drill host name.
     * @param port
     *            Drill port number.
     * @return ClientResponse
     *             Client response.
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
     *            response
     * @param  eventKey
     *            key
     * @return
     *         String Parsing the HTTP method response.
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

            if (jsonArray.getJsonObject(i).keySet().iterator().hasNext()) {
                String row = jsonArray.getJsonObject(i).get(jsonArray.getJsonObject(i).keySet().iterator().next())
                        .toString();
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
     * To load property file.
     */
    public void loadPropertiesFiles() {
        configProperties = new Properties();
        try {

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Configuration.properties");
            configProperties.load(inputStream);
            LOG.info("Properties files for Stream Handler loaded successfully");
        } catch (IOException e) {
            LOG.error("Problem while loading Properties file for Stream Handller: " + e.getMessage(), e);
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

    /**
     * To load property value.
     */
    public void loadPropertiesValues() {
        dbType = configProperties.getProperty("db.type");
        defaultLimit = configProperties.getProperty("default.limit");
        gelfPort = configProperties.getProperty("gelf.port");
        tlsSecurityType = configProperties.getProperty("TLS.securityType");
        tlsServerKeyPath = configProperties.getProperty("TLS.serverKeyPath");
        tlsClientCertKeyPath = configProperties.getProperty("TLS.clientCertKeyPath");
        tlsServerKeyPwd = configProperties.getProperty("TLS.serverKeyPwd");
        tlsClientCertPwd = configProperties.getProperty("TLS.clientCertPwd");
        tlsParingAlgo = configProperties.getProperty("TLS.paringAlgo");
        tlsEncodeAlgo = configProperties.getProperty("TLS.encodeAlgo");
    }

    /**
     * @param query
     *           query
     * @return String
     *           To match regular expression.
     */
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

    /**
     * @param notification
     *            notification.
     */
    public void updateConfigurationProperties(ConfigurationChanged notification) {
        graylogHostname = notification.getGraylogIp();
        flumeHostname = notification.getFlumeIp();
        drillHostname = notification.getDrillIp();
        flumePort = notification.getFlumePort();
        drillPort = notification.getDrillPort();
        syslogPort = notification.getSyslogPort();
        secureSyslog = notification.isSecureSysLog();
        new PersistEvent(flumeHostname, flumePort);
        LOG.info("centinel configurations updated");
    }

}

