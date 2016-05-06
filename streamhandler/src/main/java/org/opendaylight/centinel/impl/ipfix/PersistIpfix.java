/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.impl.ipfix;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import org.json.JSONObject;
import org.opendaylight.streamhandler.impl.StreamhandlerImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.EventBodyType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.PersistEventInputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;

public class PersistIpfix {

    /**
     * @author Sunaina Khanna
     * 
     *         This class saves the data in header json and persist it into
     *         database.
     * 
     */

    static JSONObject headerJson = new JSONObject();
    static PersistEventInputBuilder input = null;
    static StreamhandlerImpl streamHandlerImpl = new StreamhandlerImpl();

    public static void addJsonObjectInDatabase(JSONObject headerJson) {
        input = new PersistEventInputBuilder();
        input.setEventBodyType(EventBodyType.Avro);
        List<String> keyList = new ArrayList<String>();
        keyList.add("SequenceNumber");
        keyList.add("ObservationDomainID");
        input.setEventKeys(keyList);
        input.setEventType("stringdata");
        input.setEventBody(headerJson.toString());
        Future<RpcResult<Void>> persistEvent = streamHandlerImpl.persistEvent(input.build());
    }

    public void setHeaderObjectinJson(IPFIXHeaderDAO ipfixHeaderObject) {
        headerJson.put("Version", Integer.toString(ipfixHeaderObject.getVersionNumber()));
        headerJson.put("MessageLength", Integer.toString(ipfixHeaderObject.getLength()));
        headerJson.put("Export Timestamp", ipfixHeaderObject.getExportTime());
        headerJson.put("SequenceNumber", Long.toString(ipfixHeaderObject.getSequenceNumber()));
        headerJson.put("ObservationDomainID", Long.toString(ipfixHeaderObject.getObservationDomainID()));
    }

    public void setSetHeaderObjectinJson(IPFIXSetsDAO ipfixSetsHeaderObject) {
        headerJson.put("SetID", Integer.toString(ipfixSetsHeaderObject.getSetID()));
    }

    public static void setTemplateHeaderObjectinJson(TemplateRecord tr) {
        headerJson.put("TemplateID", Integer.toString(tr.getTemplateID()));
        headerJson.put("FieldCount", Integer.toString(tr.getFieldCount()));
    }

    public static void setInformationElementObjectinJson(List<IPFIXInformationElementParser> list) {
        int i = 1;
        Iterator<IPFIXInformationElementParser> iterator = list.iterator();
        while (iterator.hasNext()) {
            IPFIXInformationElementParser operationalObject = iterator.next();
            headerJson.put(("InformationElementID" + i).toString(),
                    Integer.toString(operationalObject.getInformationElementID()));
            headerJson.put(("FieldLength" + i).toString(), Integer.toString(operationalObject.getFieldLength()));
            i++;
            addJsonObjectInDatabase(headerJson);
        }
    }

    public void setSetDataObjectInJson(IPFIXDataSetParser sdr) {
        headerJson.put("ObservationPointId", sdr.getObservationDomainId());
        addJsonObjectInDatabase(headerJson);
    }
}
