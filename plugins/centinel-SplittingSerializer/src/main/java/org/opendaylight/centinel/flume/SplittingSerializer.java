/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.centinel.flume;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.hbase.async.AtomicIncrementRequest;
import org.hbase.async.PutRequest;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.sink.hbase.AsyncHbaseEventSerializer;

/**
 * A serializer for the AsyncHBaseSink, which splits the event body into
 * multiple columns and inserts them into a row whose key is available in
 * the headers
 *
 * Originally from https://blogs.apache.org/flume/entry/streaming_data_into_apache_hbase
 * 
 */
public class SplittingSerializer implements AsyncHbaseEventSerializer {
  private byte[] table;
  private byte[] colFam;
  private Event currentEvent;
  private byte[][] columnNames;
  private final List<PutRequest> puts = new ArrayList<PutRequest>();
  private final List<AtomicIncrementRequest> incs = new ArrayList<AtomicIncrementRequest>();
  private byte[] currentRowKey;
  private final byte[] eventCountCol = "eventCount".getBytes();
//  private String delim;

  
  public void initialize(byte[] table, byte[] cf) {
    this.table = table;
    this.colFam = cf;
  }

  
  public void setEvent(Event event) {
	  // Set the event and verify that the rowKey is not present
	    this.currentEvent = event;
	    String rowKeyStr =null;
	    int rowKeyLength = 0;
	    Map<String,String> header = currentEvent.getHeaders();
	    for (Map.Entry<String, String> entry : header.entrySet()) {
		    if(rowKeyStr==null)
		  		rowKeyStr = entry.getValue()+":";
	    	else
	    		rowKeyStr= rowKeyStr+entry.getValue()+":";
	    
		}
	    rowKeyLength = rowKeyStr.length();
	    rowKeyStr = rowKeyStr.substring(0, rowKeyLength-1);
	    if (rowKeyStr == null) {
	     throw new FlumeException("No row key found in headers!");
	    }
	    currentRowKey = rowKeyStr.getBytes();
  }

  
  public List<PutRequest> getActions() {
    // Split the event body and get the values for the columns
	  String eventStr = new String(currentEvent.getBody());
	  Map<String,String> header =currentEvent.getHeaders();
	  int headerSize = header.size();
	  int i=0;

	
	    String[] cols =new String[headerSize];
	    String[] names=new String[headerSize];
	    puts.clear();
	    for (Map.Entry<String, String> entry : header.entrySet()) {
		
		   if((entry.getKey()).equalsIgnoreCase("eventtype"))
		   {
			  colFam = entry.getValue().getBytes(); 
		   }
		   else
		   {
			   names[i] = entry.getKey();
			   cols[i] = entry.getValue();
			   i++;
			  
		   }
		}
		int l=i++;
		 cols[l] = eventStr;
		 names[l] = "stringdata";
		  byte[][] columnNames = new byte[names.length][];
	      int j = 0;
	      for(String name : names) {
	    	  columnNames[j++] = name.getBytes();

	      }
	      for (int k = 0; k < cols.length; k++) {
	    	
	      PutRequest req = new PutRequest(table, currentRowKey, colFam,
	              columnNames[k], cols[k].getBytes());
	      puts.add(req);
	    }

    return puts;
  }

  
  public List<AtomicIncrementRequest> getIncrements() {
    incs.clear();
    //Increment the number of events received
    incs.add(new AtomicIncrementRequest(table, "totalEvents".getBytes(), colFam, eventCountCol));
    return incs;
  }

  
  public void cleanUp() {
    table = null;
    colFam = null;
    currentEvent = null;
    columnNames = null;
    currentRowKey = null;
  }

  
  public void configure(Context context) {
    //Get the column names from the configuration
    String cols = new String(context.getString("columns"));
    String[] names = cols.split(",");
    columnNames = new byte[names.length][];
    int i = 0;
    for(String name : names) {
      columnNames[i++] = name.getBytes();
    }
  //  delim = new String(context.getString("delimiter"));
  }

  
  public void configure(ComponentConfiguration conf) {
  }
}