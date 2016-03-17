/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.laas.rest.utilities;

import java.io.InputStream;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Abhishek Sharma This class will provide access point to the common
 *         REST services exposed by GraylogServer
 */
public class CentinelCommonRESTServices {

    Client client = null;
    JsonBuilderFactory factory = null;
    Properties properties;
    private static final String RESOURCE_TYPE = "application/json";

    private static final Logger LOG = LoggerFactory.getLogger(CentinelCommonRESTServices.class);
    String graylogServerIp;
    String graylogStream;
    String message;
    String streams;
    String alarmCallback;

    CentinelCommonRESTServices() {
        client = Client.create();
        authenticator();
        factory = Json.createBuilderFactory(null);
        loadPropertiesFiles();
        loadPropertiesValues();
    }

    /*
     * authenticates the access to Analysis server
     */
    public void authenticator() {
        final String admin = "admin";
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(admin, admin.toCharArray());
            }

        });
    }

    /*
     * @param obj JsonObject
     * 
     * @param resource String
     * 
     * @return ClientResponse
     * 
     * Utility method to send HTTP POST request to Analysis Server
     */
    public ClientResponse graylogRESTPost(JsonObject obj, String resource) {
        WebResource webResource = client.resource(resource);
        return webResource.type(RESOURCE_TYPE).post(ClientResponse.class, obj.toString());
    }

    /*
     * @param resource String
     * 
     * @return ClientResponse Utility method to send HTTP GET request to
     * Analysis Server
     */
    public ClientResponse graylogRESTGet(String resource) {
        WebResource webResource = client.resource(resource);
        return webResource.get(ClientResponse.class);
    }

    /*
     * @param obj JsonObject
     * 
     * @param resource String
     * 
     * @return ClientResponse Utility method to send HTTP PUT request to
     * Analysis Server
     */
    public ClientResponse graylogRESTPut(JsonObject obj, String resource) {
        WebResource webResource = client.resource(resource);
        return webResource.type(RESOURCE_TYPE).put(ClientResponse.class, obj.toString());
    }

    /*
     * @param resource String
     * 
     * @return ClientResponse Utility method to send HTTP DELETE request to
     * Analysis Server
     */
    public ClientResponse graylogRESTDelete(String resource) {
        WebResource webResource = client.resource(resource);
        return webResource.delete(ClientResponse.class);
    }

    /*
     * @param response String
     * 
     * @param key
     * 
     * @return String Parsing the HTTP method response
     */
    public String fetchResponse(String response, String key) {
        JsonReader reader = Json.createReader(new StringReader(response));
        JsonObject personObject = reader.readObject();
        reader.close();
        return personObject.getString(key);
    }

    public ClientResponse graylogRESTPOSTEnabler(String resource) {
        WebResource webResource = client.resource(resource);
        return webResource.type(RESOURCE_TYPE).post(ClientResponse.class);
    }

    /*
     * To load property file
     */
    public void loadPropertiesFiles() {
        properties = new Properties();
        try {
            InputStream inputStreamForData = getClass().getClassLoader().getResourceAsStream("data.properties");
            properties.load(inputStreamForData);
            LOG.info("Properties files for Graylog loaded successfully");
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
    }

    private void loadPropertiesValues() {
        graylogStream = properties.getProperty("graylog_stream");
        message = properties.getProperty("graylog_message");
        streams = properties.getProperty("graylog_streams");
        alarmCallback = properties.getProperty("graylog_alarmcallback");
    }

    public void setGraylogIp(String ip, String port) {
        graylogServerIp = "http://" + ip + ":" + port + "/";
        LOG.info("graylog ip ultimate " + graylogServerIp);
    }

}
