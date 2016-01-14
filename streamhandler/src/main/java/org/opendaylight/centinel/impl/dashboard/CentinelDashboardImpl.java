/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.NotificationService;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.streamhandler.impl.StreamhandlerImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DashboardIncrementTestInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DashboardIncrementTestOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DashboardIncrementTestOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DashboardRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DashboardRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DashboardruleService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DeleteDashboardInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DeleteDashboardOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DeleteDashboardOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DeleteWidgetInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DeleteWidgetOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.DeleteWidgetOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetDashboardInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetDashboardOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetWidgetHistogramInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetWidgetHistogramOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetWidgetHistogramOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetWidgetMessageCountInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetWidgetMessageCountOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.GetWidgetMessageCountOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetDashboardInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetDashboardOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetDashboardOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetWidgetInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetWidgetOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.SetWidgetOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.WidgetMode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.dashboard.Widgets;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.dashboard.WidgetsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.dashboardrecord.DashboardList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.dashboardrecord.DashboardListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.get.widget.histogram.output.Values;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.dashboardrule.rev150105.get.widget.histogram.output.ValuesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlRelativeApiInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlRelativeApiOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.query.sql.relative.api.output.Records;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.record.Fields;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorType;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.SettableFuture;

/***
 * 
 * @author rajender
 * @see dashboardrule.yang This class handles all RPC to be implemented for
 *      dashboard
 *
 */
public class CentinelDashboardImpl implements DashboardruleService, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(CentinelDashboardImpl.class);

    public static final InstanceIdentifier<DashboardRecord> dashboardRecordRecordId = InstanceIdentifier.builder(
            DashboardRecord.class).build();

    private DataBroker dataProvider;
    private final ExecutorService executor;

	private StreamhandlerImpl streamhandlerImpl;

    public CentinelDashboardImpl() {
        executor = Executors.newFixedThreadPool(1);
    }

    public CentinelDashboardImpl(StreamhandlerImpl streamhandlerImpl) {
		// TODO Auto-generated constructor stub
    	
        executor = Executors.newFixedThreadPool(1);
        this.streamhandlerImpl=streamhandlerImpl;

        
	}

	public void setDataProvider(final DataBroker salDataProvider) {
        LOG.info("Entered to Data Provider");
        this.dataProvider = salDataProvider;
        LOG.info("data provider set");
    }

    @Override
    public void close() throws Exception {
        // When we close this service we need to shutdown our executor!
        executor.shutdown();

        if (dataProvider != null) {
            WriteTransaction tx = dataProvider.newWriteOnlyTransaction();
           LOG.info(" Transaction closed CentinelDashboardImpl");
            tx.delete(LogicalDatastoreType.OPERATIONAL, dashboardRecordRecordId);

        }

    }

    @Override
    public Future<RpcResult<SetWidgetOutput>> setWidget(SetWidgetInput input) {

        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<SetWidgetOutput>> futureResult = SettableFuture.create();
        final SetWidgetOutputBuilder setWidgetOutputBuilder = new SetWidgetOutputBuilder();
        try {
            List<Widgets> listofwidgets = new ArrayList<Widgets>();
            Widgets widget;

            if (input.getDashboardID() == null) {
                ErrorType errorType = ErrorType.APPLICATION;
                futureResult.set(RpcResultBuilder.<SetWidgetOutput> failed()
                        .withError(errorType, "Dashboard cannot be empty")

                        .build());
                return futureResult;
            }
            if (input.getMode() == null) {
                ErrorType errorType = ErrorType.APPLICATION;
                futureResult.set(RpcResultBuilder.<SetWidgetOutput> failed()
                        .withError(errorType, "Mode cannot be empty").build());
                return futureResult;

            }

            if (input.getWidgetID() == null) {
                widget = new WidgetsBuilder().setDashboardID(input.getDashboardID())
                        .setDescription(input.getDescription()).setStreamID(input.getStreamID())
                        .setType(input.getType()).setMode(input.getMode()).setAlertID(input.getAlertID())
                        .setTimeRange(input.getTimeRange())
                        .setWidgetID(String.format("%x", (int) (Math.random() * 10000))).build();
            } else {
                widget = new WidgetsBuilder().setDashboardID(input.getDashboardID())
                        .setDescription(input.getDescription()).setStreamID(input.getStreamID())
                        .setType(input.getType()).setAlertID(input.getAlertID()).setMode(input.getMode())
                        .setTimeRange(input.getTimeRange()).setWidgetID(input.getWidgetID()).build();
            }

            listofwidgets.add(widget);

            DashboardList newitem = new DashboardListBuilder().setWidgets(listofwidgets)
                    .setDashboardID(input.getDashboardID()).build();

            List<DashboardList> Listofmerge = new ArrayList<DashboardList>();
            Listofmerge.add(newitem);
            try {
                tx.merge(LogicalDatastoreType.OPERATIONAL, dashboardRecordRecordId, new DashboardRecordBuilder()
                        .setDashboardList(Listofmerge)

                .build(), false);
                tx.submit();
            } catch (Exception ex) {
                futureResult.set(RpcResultBuilder.<SetWidgetOutput> failed()
                        .withRpcErrors(((TransactionCommitFailedException) ex).getErrorList()).build());

                return futureResult;

            }
            setWidgetOutputBuilder.setDashboardID(newitem.getDashboardID());
            setWidgetOutputBuilder.setStreamID(newitem.getWidgets().get(0).getStreamID());
            setWidgetOutputBuilder.setWidgetID(newitem.getWidgets().get(0).getWidgetID());

            futureResult.set(RpcResultBuilder.<SetWidgetOutput> success(setWidgetOutputBuilder.build()).build());
            if (input.getMode() == WidgetMode.Stream)
                createCounterForStream(newitem.getWidgets().get(0).getWidgetID(), newitem.getWidgets().get(0)
                        .getStreamID(), newitem.getWidgets().get(0).getTimeRange(), input.getMode(),streamhandlerImpl);
            if (input.getMode() == WidgetMode.Alert)
                createCounterForStream(newitem.getWidgets().get(0).getWidgetID(), newitem.getWidgets().get(0)
                        .getAlertID(), newitem.getWidgets().get(0).getTimeRange(), input.getMode(),streamhandlerImpl);
            LOG.info("Dashboard Widget created/modified:"+newitem.getWidgets().get(0).getWidgetID());

        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            ErrorType errorType = ErrorType.APPLICATION;
            futureResult.set(RpcResultBuilder.<SetWidgetOutput> failed()
                    .withError(errorType, "Exception Caught at Widget Creation:" + ex.getMessage())

            .build());

        }

        return futureResult;

        // TODO Auto-generated method stub
    }

    private void createCounterForStream(String widgetID, String streamID, Short resettime, Enum mode, StreamhandlerImpl streamhandlerImpl2) {
        // TODO Auto-generated method stub
        StreamCounterInfoCache streamCounterInfoCache = StreamCounterInfoCache.getInstance();
        WidgetStreamCounterVO widgetStreamCounterVO = new WidgetStreamCounterVO();
        widgetStreamCounterVO.setCounter(0);
        widgetStreamCounterVO.setMode(mode);
        widgetStreamCounterVO.setStreamID(streamID);
        widgetStreamCounterVO.setWidgetID(widgetID);
        widgetStreamCounterVO.setResettime(resettime);
        streamCounterInfoCache.addCounter(widgetStreamCounterVO,streamhandlerImpl2);
        LOG.info("created counter for widgetID:" + widgetID + ":streamID:" + streamID);

    }

    @Override
    public Future<RpcResult<DeleteDashboardOutput>> deleteDashboard(DeleteDashboardInput input) {

        String dashBoardID = input.getDashboardID();
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<DeleteDashboardOutput>> futureResult = SettableFuture.create();
        final DeleteDashboardOutputBuilder setWidgetOutputBuilder = new DeleteDashboardOutputBuilder();

        DashboardList dashboardtodelete = new DashboardListBuilder().setDashboardID(dashBoardID)

        .build();

        List<String> widgetIDs = new ArrayList<String>();

        try {

            CheckedFuture<Optional<DashboardList>, ReadFailedException> readobject = tx.read(
                    LogicalDatastoreType.OPERATIONAL,
                    dashboardRecordRecordId.child(DashboardList.class, dashboardtodelete.getKey()));

            Optional<DashboardList> dashboardList = (Optional<DashboardList>) readobject.checkedGet(1000,
                    TimeUnit.MILLISECONDS);
		if (null != dashboardList.get().getWidgets()){
		    for (Widgets widget : dashboardList.get().getWidgets()) {
		        widgetIDs.add(widget.getWidgetID());
		    }
		}

        } catch (Exception readEx) {
            ErrorType errorType = ErrorType.APPLICATION;
            futureResult.set(RpcResultBuilder.<DeleteDashboardOutput> failed()
                    .withError(errorType, "Exception Caught at Dashboard read/deletion:" + readEx.getMessage())

            .build());
            return futureResult;

        }

        try {
            tx.delete(LogicalDatastoreType.OPERATIONAL,
                    dashboardRecordRecordId.child(DashboardList.class, dashboardtodelete.getKey()));
            tx.submit();
            setWidgetOutputBuilder.setMessage("SUCCESS");
            futureResult.set(RpcResultBuilder.<DeleteDashboardOutput> success(setWidgetOutputBuilder.build()).build());

            StreamCounterInfoCache streamCounterInfoCache = StreamCounterInfoCache.getInstance();

            for (String widgetID : widgetIDs) {
                streamCounterInfoCache.removeCounterForWidget(widgetID);

            }

        } catch (Exception Ex) {
            ErrorType errorType = ErrorType.APPLICATION;
            futureResult.set(RpcResultBuilder.<DeleteDashboardOutput> failed()
                    .withError(errorType, "Exception Caught at Dashboard deletion:" + Ex.getMessage())

            .build());
            return futureResult;

        }
        return futureResult;
    }

    @Override
    public Future<RpcResult<GetDashboardOutput>> getDashboard(GetDashboardInput input) {
        return null;
    }

    @Override
    public Future<RpcResult<SetDashboardOutput>> setDashboard(SetDashboardInput input) {
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<SetDashboardOutput>> futureResult = SettableFuture.create();
        final SetDashboardOutputBuilder setDashboardOutputBuilder = new SetDashboardOutputBuilder();
        try {
            DashboardList newitem = new DashboardListBuilder().setDescription(input.getDescription())
                    .setTitle(input.getTitle()).setDashboardID(String.format("%x", (int) (Math.random() * 10000)))
                    .build();

            List<DashboardList> Listofmerge = new ArrayList<DashboardList>();
            Listofmerge.add(newitem);

            tx.merge(LogicalDatastoreType.OPERATIONAL, dashboardRecordRecordId, new DashboardRecordBuilder()
                    .setDashboardList(Listofmerge)

            .build(), true);
            tx.submit();

            setDashboardOutputBuilder.setDashboardID(newitem.getDashboardID());
            setDashboardOutputBuilder.setTitle(newitem.getTitle());
            setDashboardOutputBuilder.setDescription(newitem.getDescription());

            futureResult.set(RpcResultBuilder.<SetDashboardOutput> success(setDashboardOutputBuilder.build()).build());
            LOG.info("Created Dashboard:" + newitem.getDashboardID());

        } catch (Exception e) {
            LOG.error(e.getMessage());

            ErrorType errorType = ErrorType.APPLICATION;
            futureResult.set(RpcResultBuilder.<SetDashboardOutput> failed()
                    .withError(errorType, "Exception caught at creating Dashboard:" + e.getMessage()).build());
        }

        return futureResult;

    }

    @Override
    public Future<RpcResult<GetWidgetMessageCountOutput>> getWidgetMessageCount(GetWidgetMessageCountInput input) {

        final SettableFuture<RpcResult<GetWidgetMessageCountOutput>> futureResult = SettableFuture.create();
        final GetWidgetMessageCountOutputBuilder setDashboardOutputBuilder = new GetWidgetMessageCountOutputBuilder();
        try {
            Integer value = null;

            StreamCounterInfoCache streamCounterInfoCache = StreamCounterInfoCache.getInstance();
            value = streamCounterInfoCache.getCounterValue(input.getWidgetID());

            setDashboardOutputBuilder.setValue(value);

            futureResult.set(RpcResultBuilder.<GetWidgetMessageCountOutput> success(setDashboardOutputBuilder.build())
                    .build());

        } catch (Exception e) {
            LOG.error(e.getMessage());

            futureResult.set(RpcResultBuilder.<GetWidgetMessageCountOutput> failed().build());
        }

        return futureResult;

    }

    @Override
    public Future<RpcResult<GetWidgetHistogramOutput>> getWidgetHistogram(GetWidgetHistogramInput input) {
        StreamCounterInfoCache streamCounterInfoCache = StreamCounterInfoCache.getInstance();
        List<WidgetStreamCounterVO> listofwidgets = streamCounterInfoCache.getListofcounter();
        for (WidgetStreamCounterVO widgetStreamCounterVO : listofwidgets) {
            LOG.info("listofwidgets" + widgetStreamCounterVO.getWidgetID());

            if (widgetStreamCounterVO.getWidgetID().equals(input.getWidgetID())) {
            	
            	LOG.info("fetching data from DB ");
                String widgetID = input.getWidgetID();
                String starttime = input.getFromTimestamp();
                String endtime = input.getToTimestamp();

                // fetch data from DB where widgetID,starttime >=time<= endtime
                QuerySqlRelativeApiInputBuilder querySqlRelativeApiInputBuilder=new QuerySqlRelativeApiInputBuilder();
                List<String> value1;
				//querySqlRelativeApiInputBuilder.setEventFields(value1);
                String value2="select dashboard from centinel where centinel.dashboard.widgetId='"+widgetID +"'";
				querySqlRelativeApiInputBuilder.setQueryString(value2);
				LOG.info("querySqlRelativeApiInputBuilder query_string: " + querySqlRelativeApiInputBuilder.getQueryString());
                Future<RpcResult<QuerySqlRelativeApiOutput>> result = streamhandlerImpl.querySqlRelativeApi(querySqlRelativeApiInputBuilder.build());
                
                
                final SettableFuture<RpcResult<GetWidgetHistogramOutput>> futureResult = SettableFuture.create();
                final GetWidgetHistogramOutputBuilder setWidgetHistogramOutputBuilder = new GetWidgetHistogramOutputBuilder();
                List<Values> values = new ArrayList<Values>();
              
                		
                try{
                	if(result.get().getResult().getRecords().size()==0)
                	{
                		
                		 ErrorType errorType = ErrorType.APPLICATION;
                         futureResult.set(RpcResultBuilder.<GetWidgetHistogramOutput> failed()
                                 .withError(errorType, "Exception caught at getWidgetHistogram:No Results found in DB").build());
                         
                         
                		
                		  return futureResult;

                	}
            	LOG.info("fetching data from DB "+result.get().getResult().getRecords().get(0).getFields());
            	List<Records> records = result.get().getResult().getRecords();
            	for (Records record : records) {
            		
            		ValuesBuilder valbuil=new ValuesBuilder();
            		
            		List<Fields> fields = record.getFields();
            		for (Fields field : fields) {
            			switch(field.getFieldName())
            			{
            			case "resetTime":
            				valbuil.setTimestamp(field.getFieldValue());
            			    break;
            			
            			case "counter":
            				valbuil.setValue(Integer.parseInt(field.getFieldValue()));
            			    break;
            			
            			}
					}
            		
            		Values val=valbuil.build();
              		values.add(val);
				}
            	
            	if(!values.isEmpty())
            	{
                	LOG.info("Created list of data from DB of Size "+values.size());

            	 setWidgetHistogramOutputBuilder.setValues(values);
            	}
            	else
            	{
                	LOG.warn("Could not fetch list of data from DB of Size ");

            	}

                 futureResult.set(RpcResultBuilder.<GetWidgetHistogramOutput> success(
                         setWidgetHistogramOutputBuilder.build()).build());

          		
          		return futureResult;
          		
            	
                }catch(Exception Ex)
                {
                	LOG.error("exception after fetching from DB "+Ex.getLocalizedMessage());
                	 ErrorType errorType = ErrorType.APPLICATION;
                     futureResult.set(RpcResultBuilder.<GetWidgetHistogramOutput> failed()
                             .withError(errorType, "Exception Caught at widget deletion:" + Ex.getMessage())

                             .build());
                     
               		return futureResult;

                }
            	
            	
                
       
                // set the values here
               
            }
        }

        return null;
    }

    @Override
    public Future<RpcResult<DeleteWidgetOutput>> deleteWidget(DeleteWidgetInput input) {
      
        String dashboardID = input.getDashboardID();

        String widgetID = input.getWidgetID();
        final ReadWriteTransaction tx = dataProvider.newReadWriteTransaction();
        final SettableFuture<RpcResult<DeleteWidgetOutput>> futureResult = SettableFuture.create();
        final DeleteWidgetOutputBuilder deleteWidgetOutputBuilder = new DeleteWidgetOutputBuilder();

        DashboardList dashboardtodelete = new DashboardListBuilder().setDashboardID(dashboardID)

        .build();

        Widgets widgettodelete = new WidgetsBuilder().setWidgetID(widgetID).build();

        try {
            tx.delete(
                    LogicalDatastoreType.OPERATIONAL,
                    dashboardRecordRecordId.child(DashboardList.class, dashboardtodelete.getKey()).child(Widgets.class,
                            widgettodelete.getKey()));
            tx.submit();
            deleteWidgetOutputBuilder.setMessage("SUCCESS");
            futureResult.set(RpcResultBuilder.<DeleteWidgetOutput> success(deleteWidgetOutputBuilder.build()).build());

            StreamCounterInfoCache streamCounterInfoCache = StreamCounterInfoCache.getInstance();

            streamCounterInfoCache.removeCounterForWidget(widgetID);
            LOG.info("Widget deleted:" + widgetID);
        } catch (Exception Ex) {
            ErrorType errorType = ErrorType.APPLICATION;
            futureResult.set(RpcResultBuilder.<DeleteWidgetOutput> failed()
                    .withError(errorType, "Exception Caught at widget deletion:" + Ex.getMessage())

            .build());
        }
        return futureResult;
    }

    @Override
    public Future<RpcResult<DashboardIncrementTestOutput>> dashboardIncrementTest(DashboardIncrementTestInput input) {

        final SettableFuture<RpcResult<DashboardIncrementTestOutput>> futureResult = SettableFuture.create();

        futureResult.set(RpcResultBuilder.<DashboardIncrementTestOutput> success(
                new DashboardIncrementTestOutputBuilder().setMessage("SUCCESS").build()).build());
        /**
         * code to increase the counter for streamID:Start
         */
        StreamCounterInfoCache streamCounterInfoCache = StreamCounterInfoCache.getInstance();
        streamCounterInfoCache.incrementCounter(input.getStreamID());
        /**
         * code to increase the counter for streamID:End
         */
        return futureResult;
    }

}
