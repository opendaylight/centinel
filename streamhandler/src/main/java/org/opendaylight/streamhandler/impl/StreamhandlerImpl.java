/*
 * Copyright (c) 2015 Tata Consultancy services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.streamhandler.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.PersistEventInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryEventsInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryEventsOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryLuceneApiInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryLuceneApiOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryLuceneRelativeApiInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QueryLuceneRelativeApiOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlApiInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlApiOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlApiOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlRelativeApiInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlRelativeApiOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.QuerySqlRelativeApiOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.StreamhandlerService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.query.sql.relative.api.output.Records;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.query.sql.relative.api.output.RecordsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.record.Fields;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.record.FieldsBuilder;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorType;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.sun.jersey.api.client.ClientResponse;

public class StreamhandlerImpl implements StreamhandlerService, AutoCloseable {

	private static final String ORDER_BY = " ORDER BY ";
	private static final String WHERE = " WHERE ";
	private static final String AND = " AND ";
	private static final String CONVERT_QUERY_STREAM_TO_JSON = "convert_from(centinel.stream.stringdata, 'json')";
	private static final String CONVERT_QUERY_ALERT_TO_JSON = "convert_from(centinel.alert.stringdata, 'json')";
	private static final String CONVERT_QUERY_DASHBOARD_TO_JSON = "convert_from(centinel.dashboard.stringdata, 'json')";

	private static final Logger LOG = LoggerFactory
			.getLogger(StreamhandlerImpl.class);

	private final ExecutorService executor;
	PersistEvent client = null;
	CommonServices commonServices = null;

	Calendar now = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat(StreamConstants.SDF_DATE_FORMAT);

	public StreamhandlerImpl() {
		commonServices = CommonServices.getInstance();
		executor = Executors.newFixedThreadPool(1);
	}

	@Override
	public void close() throws Exception {

		executor.shutdown();
	}

    @Override
    public Future<RpcResult<Void>> persistEvent(PersistEventInput input) {
        final SettableFuture<RpcResult<Void>> futureResult = SettableFuture.create();
        boolean result = true;
        try {
            // Initialize client with the remote Flume agent's host and port
            if (client == null) {
                client = new PersistEvent(commonServices.flumeHostname, commonServices.flumePort);
            }
            result = client.sendDataToFlume(input);
        } catch (Exception e) {
            futureResult.set(RpcResultBuilder.<Void> failed().build());
            LOG.error("Unable to save data " + e.getMessage(), e);
        }
        if (!result) {
            futureResult.set(RpcResultBuilder.<Void> failed().build());
        } else {
            futureResult.set(RpcResultBuilder.<Void> success().build());
        }
        return futureResult;
    }

	@Override
	public Future<RpcResult<QuerySqlRelativeApiOutput>> querySqlRelativeApi(
			QuerySqlRelativeApiInput input) {

		final SettableFuture<RpcResult<QuerySqlRelativeApiOutput>> futureResult = SettableFuture
				.create();
		String query = input.getQueryString();
		Short timeRange = input.getTimeRange();
		Short limit = input.getLimit();
		List<String> eventFields = input.getEventFields();
		List<Map<String, Object>> output = null;

		if (query == null) {
			return Futures
					.immediateFailedCheckedFuture(new TransactionCommitFailedException(
							"invalid-input", RpcResultBuilder.newError(
									ErrorType.APPLICATION, "Field missing",
									"Mandatory field Query missing")));
		}
		if (limit == null) {
			limit = Short.parseShort(commonServices.defaultLimit);
		}
		if (timeRange == null && limit != null) {
			if (commonServices.dbType.equalsIgnoreCase(StreamConstants.HBASE)) {
				if (checkIfQueryContainsStreamWithSpaces(query)) {
					query = updateWhenQueryContainsStream(query);
					query = amendLimitToQuery(query, limit);
				} else if (checkIfQueryContainsAlertWithSpaces(query)) {
					query = updateWhenQueryContainsAlert(query);
					query = amendLimitToQuery(query, limit);
				} else if (checkIfQueryContainsDashboardWithSpaces(query)) {
					query = updateWhenQueryContainsDashboard(query);
					query = amendLimitToQuery(query, limit);
				} else if (checkIfQueryContainsData(query)) {
					query = updateWhenQueryContainsData(query);
					query = amendLimitToQuery(query, limit);
				} else {

					return Futures
							.immediateFailedCheckedFuture(new TransactionCommitFailedException(
									"invalid-input",
									RpcResultBuilder
											.newError(ErrorType.APPLICATION,
													"invalid query",
													"supported columns are stream, alert, dashboard and data ")));
				}

			} else {
				return Futures
						.immediateFailedCheckedFuture(new TransactionCommitFailedException(
								"invalid-input", RpcResultBuilder.newError(
										ErrorType.APPLICATION,
										"DB type not supported", "DB type "
												+ commonServices.dbType
												+ "not supported")));

			}

		} else if (timeRange != null && limit != null) {
			now.add(Calendar.MINUTE, -timeRange);
			String formattedTimestamp = sdf.format(now.getTime());

			if (commonServices.dbType.equalsIgnoreCase(StreamConstants.HBASE)) {
				if (checkIfQueryContainsStreamWithSpaces(query)) {
					query = replaceFirstCentinelForDBType(query);
					if (checkIfQueryContainsStreamDot(query)) {
						query = query.replace(StreamConstants.STREAM_DOT,
								StreamConstants.CENTINEL_DOT
										+ StreamConstants.STREAM_DOT);
						query = query
								+ " AND centinel.stream.event_timestamp>="
								+ getSingleQuotedValue(formattedTimestamp);
					} else if (!checkIfQueryContainsStreamDot(query)) {
						query = query
								+ " where centinel.stream.event_timestamp>="
								+ getSingleQuotedValue(formattedTimestamp);
					}
					query = replaceFirstStream(query);
					query = amendLimitToQuery(query, limit);
				} else if (checkIfQueryContainsAlertWithSpaces(query)) {
					query = replaceFirstCentinelForDBType(query);
					if (checkIfQueryContainsAlertDot(query)) {
						query = query.replace(StreamConstants.ALERT_DOT,
								StreamConstants.CENTINEL_DOT
										+ StreamConstants.ALERT_DOT);
						query = query
								+ " AND centinel.alert.check_result:triggeredAt>="
								+ getSingleQuotedValue(formattedTimestamp);
					} else if (!checkIfQueryContainsAlertDot(query)) {
						query = query
								+ " where centinel.alert.check_result:triggeredAt>="
								+ getSingleQuotedValue(formattedTimestamp);
					}
					query = replaceFirstAlert(query);
					query = amendLimitToQuery(query, limit);
				} else if (checkIfQueryContainsDashboardWithSpaces(query)) {
					query = replaceFirstCentinelForDBType(query);

					if (checkIfQueryContainsDashboardDot(query)) {
						query = query.replace(StreamConstants.DASHBOARD_DOT,
								StreamConstants.CENTINEL_DOT
										+ StreamConstants.DASHBOARD_DOT);
						query = query + AND + "centinel.dashboard.resetTime>="
								+ getSingleQuotedValue(formattedTimestamp);

					} else if (!checkIfQueryContainsDashboardDot(query)) {
						query = query + WHERE
								+ "centinel.dashboard.resetTime>="
								+ getSingleQuotedValue(formattedTimestamp);
					}
					query = replaceFirstDashboard(query);
					query = amendLimitToQuery(query, limit);

				} else if (checkIfQueryContainsData(query)) {
					query = updateWhenQueryContainsData(query);
					query = amendLimitToQuery(query, limit);
				} else {
					return Futures
							.immediateFailedCheckedFuture(new TransactionCommitFailedException(
									"invalid-input",
									RpcResultBuilder
											.newError(ErrorType.APPLICATION,
													"invalid query",
													"supported columns are stream, alert, dashboard and data ")));
				}
			} else {
				return Futures
						.immediateFailedCheckedFuture(new TransactionCommitFailedException(
								"invalid-input", RpcResultBuilder.newError(
										ErrorType.APPLICATION,
										"DB type not supported", "DB type "
												+ commonServices.dbType
												+ "not supported")));
			}
		}

		query = query
				.replace(StreamConstants.COLON, StreamConstants.UNDERSCORE);
		query = commonServices.matchRegEx(query);

		Map<String, String> drillQuery = new HashMap<String, String>();
		drillQuery.put(StreamConstants.QUERY_TYPE, StreamConstants.SQL);
		drillQuery.put(StreamConstants.QUERY, query);

		LOG.info("Drill Query: " + query);

		ClientResponse response = commonServices.drillRESTPost(drillQuery,
				commonServices.drillHostname, commonServices.drillPort);

		if (response != null && response.getStatus() != 200) {
			LOG.info("Error in Drill: " + response.getStatus());
			return Futures
					.immediateFailedCheckedFuture(new TransactionCommitFailedException(
							"invalid-input", RpcResultBuilder.newError(
									ErrorType.APPLICATION,
									"Error connecting drill", response
											.getClientResponseStatus()
											.toString())));

		} else {

			output = commonServices.parseResponse(
					response.getEntity(String.class), eventFields);

		}

		List<Records> recordList = new ArrayList<Records>();

		for (Map<String, Object> out : output) {

			Iterator<Entry<String, Object>> itr = out.entrySet().iterator();
			List<Fields> fieldsList = new ArrayList<Fields>();
			while (itr.hasNext()) {
				Entry<String, Object> obj = itr.next();
				fieldsList.add(new FieldsBuilder()
						.setFieldValue(obj.getValue().toString())
						.setFieldName(obj.getKey()).build());
			}

			Records recordObj = new RecordsBuilder().setFields(fieldsList)
					.build();
			recordList.add(recordObj);
		}

		QuerySqlRelativeApiOutput queryOutput = new QuerySqlRelativeApiOutputBuilder()
				.setRecords(recordList).build();
		futureResult.set(RpcResultBuilder.<QuerySqlRelativeApiOutput> success(
				queryOutput).build());
		return futureResult;
	}

	@Override
	public Future<RpcResult<QuerySqlApiOutput>> querySqlApi(
			QuerySqlApiInput input) {

		final SettableFuture<RpcResult<QuerySqlApiOutput>> futureResult = SettableFuture
				.create();
		String query = input.getQueryString();
		String fromTime = input.getFromTime();
		String toTime = input.getToTime();
		Short limit = input.getLimit();
		List<String> eventFields = input.getEventFields();
		List<Map<String, Object>> output = null;

		if (query == null) {
			return Futures
					.immediateFailedCheckedFuture(new TransactionCommitFailedException(
							"invalid-input", RpcResultBuilder.newError(
									ErrorType.APPLICATION, "Field missing",
									"Mandatory field Query missing")));

		}
		if (limit == null) {
			limit = Short.parseShort(commonServices.defaultLimit);
		}
		if (fromTime == null && toTime == null && limit != null) {
			if (commonServices.dbType.equalsIgnoreCase(StreamConstants.HBASE)) {
				if (checkIfQueryContainsStreamWithSpaces(query)) {
					query = updateWhenQueryContainsStream(query);
					query = query + ORDER_BY
							+ "centinel.stream.event_timestamp DESC limit "
							+ limit;
				} else if (checkIfQueryContainsAlertWithSpaces(query)) {
					query = updateWhenQueryContainsAlert(query);
					query = query
							+ ORDER_BY
							+ "centinel.alert.check_result:triggeredAt DESC limit "
							+ limit;
				} else if (checkIfQueryContainsDashboardWithSpaces(query)) {
					query = updateWhenQueryContainsDashboard(query);
					query = query + ORDER_BY
							+ "centinel.dashboard.resetTime DESC limit "
							+ limit;
				} else if (checkIfQueryContainsData(query)) {
					query = updateWhenQueryContainsData(query);
					query = amendLimitToQuery(query, limit);
				} else {

					return Futures
							.immediateFailedCheckedFuture(new TransactionCommitFailedException(
									"invalid-input",
									RpcResultBuilder
											.newError(ErrorType.APPLICATION,
													"invalid query",
													"supported columns are stream, alert, dashboard and data ")));
				}

			} else {
				return Futures
						.immediateFailedCheckedFuture(new TransactionCommitFailedException(
								"invalid-input", RpcResultBuilder.newError(
										ErrorType.APPLICATION,
										"DB type not supported", "DB type "
												+ commonServices.dbType
												+ "not supported")));

			}
		} else if (fromTime != null && toTime != null && limit != null) {
			if (commonServices.dbType.equalsIgnoreCase(StreamConstants.HBASE)) {
				if (query.contains(StreamConstants.STREAM)) {
					query = replaceFirstCentinelForDBType(query);
					if (checkIfQueryContainsStreamDot(query)) {
						query = query.replace(StreamConstants.STREAM_DOT,
								StreamConstants.CENTINEL_DOT
										+ StreamConstants.STREAM_DOT);
						query = query + AND
								+ "centinel.stream.event_timestamp>="
								+ getSingleQuotedValue(fromTime) + AND
								+ "centinel.stream.event_timestamp<="
								+ getSingleQuotedValue(toTime);
					} else if (!checkIfQueryContainsStreamDot(query)) {
						query = query + WHERE
								+ "centinel.stream.event_timestamp>="
								+ getSingleQuotedValue(fromTime) + AND
								+ "centinel.stream.event_timestamp<="
								+ getSingleQuotedValue(toTime);
					}
					query = replaceFirstStream(query);
					query = amendLimitToQuery(query, limit);
				} else if (checkIfQueryContainsAlertWithSpaces(query)) {
					query = replaceFirstCentinelForDBType(query);
					if (checkIfQueryContainsAlertDot(query)) {
						query = query.replace(StreamConstants.ALERT_DOT,
								StreamConstants.CENTINEL_DOT
										+ StreamConstants.ALERT_DOT);
						query = query + AND
								+ "centinel.alert.check_result:triggeredAt>="
								+ getSingleQuotedValue(fromTime) + AND
								+ "centinel.alert.check_result:triggeredAt<="
								+ getSingleQuotedValue(toTime);

					} else if (!checkIfQueryContainsAlertDot(query)) {
						query = query + WHERE
								+ "centinel.alert.check_result:triggeredAt>="
								+ getSingleQuotedValue(fromTime) + AND
								+ "centinel.alert.check_result:triggeredAt<="
								+ getSingleQuotedValue(toTime);
					}
					query = replaceFirstAlert(query);
					query = amendLimitToQuery(query, limit);
				} else if (checkIfQueryContainsDashboardWithSpaces(query)) {
					query = replaceFirstCentinelForDBType(query);
					if (checkIfQueryContainsDashboardDot(query)) {
						query = query.replace(StreamConstants.DASHBOARD_DOT,
								StreamConstants.CENTINEL_DOT
										+ StreamConstants.DASHBOARD_DOT);
						query = query + AND + "centinel.dashboard.resetTime>="
								+ getSingleQuotedValue(fromTime) + AND
								+ "centinel.dashboard.resetTime<="
								+ getSingleQuotedValue(toTime);

					} else if (!checkIfQueryContainsDashboardDot(query)) {
						query = query + WHERE
								+ "centinel.dashboard.resetTime>="
								+ getSingleQuotedValue(fromTime) + AND
								+ "centinel.dashboard.resetTime<="
								+ getSingleQuotedValue(toTime);
					}
					query = replaceFirstDashboard(query);
					query = amendLimitToQuery(query, limit);
				} else if (checkIfQueryContainsData(query)) {
					query = updateWhenQueryContainsData(query);
					query = amendLimitToQuery(query, limit);
				} else {

					return Futures
							.immediateFailedCheckedFuture(new TransactionCommitFailedException(
									"invalid-input",
									RpcResultBuilder
											.newError(ErrorType.APPLICATION,
													"invalid query",
													"supported columns are stream, alert, dashboard and data ")));
				}

			} else {
				return Futures
						.immediateFailedCheckedFuture(new TransactionCommitFailedException(
								"invalid-input", RpcResultBuilder.newError(
										ErrorType.APPLICATION,
										"DB type not supported", "DB type "
												+ commonServices.dbType
												+ "not supported")));
			}
		}

		query = query
				.replace(StreamConstants.COLON, StreamConstants.UNDERSCORE);
		query = commonServices.matchRegEx(query);

		Map<String, String> drillQuery = new HashMap<String, String>();
		drillQuery.put(StreamConstants.QUERY_TYPE, StreamConstants.SQL);
		drillQuery.put(StreamConstants.QUERY, query);
		LOG.info("Drill Query: " + query);

		ClientResponse response = commonServices.drillRESTPost(drillQuery,
				commonServices.drillHostname, commonServices.drillPort);

		if (response != null && response.getStatus() != 200) {
			LOG.info("Error in Drill: " + response.getStatus());
			return Futures
					.immediateFailedCheckedFuture(new TransactionCommitFailedException(
							"invalid-input", RpcResultBuilder.newError(
									ErrorType.APPLICATION,
									"Error connecting drill", response
											.getClientResponseStatus()
											.toString())));
		} else {

			output = commonServices.parseResponse(
					response.getEntity(String.class), eventFields);

		}

		List<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.query.sql.api.output.Records> recordList = new ArrayList<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.query.sql.api.output.Records>();

		for (Map<String, Object> out : output) {

			Iterator<Entry<String, Object>> itr = out.entrySet().iterator();
			List<Fields> fieldsList = new ArrayList<Fields>();
			while (itr.hasNext()) {
				Entry<String, Object> obj = itr.next();
				fieldsList.add(new FieldsBuilder()
						.setFieldValue(obj.getValue().toString())
						.setFieldName(obj.getKey()).build());
			}

			org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.query.sql.api.output.Records recordObj = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.streamhandler.rev150105.query.sql.api.output.RecordsBuilder()
					.setFields(fieldsList).build();
			recordList.add(recordObj);
		}

		QuerySqlApiOutput queryOutput = new QuerySqlApiOutputBuilder()
				.setRecords(recordList).build();
		futureResult.set(RpcResultBuilder.<QuerySqlApiOutput> success(
				queryOutput).build());
		return futureResult;

	}

	@Override
	public Future<RpcResult<QueryEventsOutput>> queryEvents(
			QueryEventsInput input) {
		return null;
	}

	@Override
	public Future<RpcResult<QueryLuceneRelativeApiOutput>> queryLuceneRelativeApi(
			QueryLuceneRelativeApiInput input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<RpcResult<QueryLuceneApiOutput>> queryLuceneApi(
			QueryLuceneApiInput input) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean checkIfQueryContainsDashboardDot(String query) {
		return query.contains(StreamConstants.SPACE
				+ StreamConstants.DASHBOARD_DOT);
	}

	private boolean checkIfQueryContainsAlertDot(String query) {
		return query
				.contains(StreamConstants.SPACE + StreamConstants.ALERT_DOT);
	}

	private boolean checkIfQueryContainsStreamDot(String query) {
		return query.contains(StreamConstants.SPACE
				+ StreamConstants.STREAM_DOT);
	}

	private boolean checkIfQueryContainsData(String query) {
		return query.contains(StreamConstants.DATA);
	}

	private boolean checkIfQueryContainsDashboardWithSpaces(String query) {
		return query.contains(StreamConstants.SPACE + StreamConstants.DASHBOARD
				+ StreamConstants.SPACE);
	}

	private boolean checkIfQueryContainsAlertWithSpaces(String query) {
		return query.contains(StreamConstants.SPACE + StreamConstants.ALERT
				+ StreamConstants.SPACE);
	}

	private boolean checkIfQueryContainsStreamWithSpaces(String query) {
		return query.contains(StreamConstants.SPACE + StreamConstants.STREAM
				+ StreamConstants.SPACE);
	}

    private String replaceFirstDashboard(String query) {
        return query.replaceFirst(StreamConstants.DASHBOARD, CONVERT_QUERY_DASHBOARD_TO_JSON);
    }

    private String amendLimitToQuery(String query, Short limit) {
        return query + StreamConstants.SPACE + StreamConstants.LIMIT + StreamConstants.SPACE + limit;
    }

    private String replaceFirstAlert(String query) {
        return query.replaceFirst(StreamConstants.ALERT, CONVERT_QUERY_ALERT_TO_JSON);
    }

    private String replaceFirstStream(String query) {
        return query.replaceFirst(StreamConstants.STREAM, CONVERT_QUERY_STREAM_TO_JSON);
    }

    private String replaceFirstCentinelForDBType(String query) {
        return query.replaceFirst(StreamConstants.CENTINEL, commonServices.dbType + StreamConstants.DOT_CENTINEL);
    }

	private String updateWhenQueryContainsData(String query) {
		query = replaceFirstCentinelForDBType(query);
		query = query.replace(StreamConstants.DATA_DOT,
				StreamConstants.CENTINEL_DOT + StreamConstants.DATA_DOT);
		query = query.replaceFirst(StreamConstants.DATA,
				"convert_from(centinel.stringdata.stringdata, 'json')");
		return query;
	}

	private String updateWhenQueryContainsDashboard(String query) {
		query = replaceFirstCentinelForDBType(query);
		query = query.replace(StreamConstants.DASHBOARD_DOT,
				StreamConstants.CENTINEL_DOT + StreamConstants.DASHBOARD_DOT);
		query = replaceFirstDashboard(query);
		return query;
	}

	private String updateWhenQueryContainsAlert(String query) {
		query = replaceFirstCentinelForDBType(query);
		query = query.replace(StreamConstants.ALERT_DOT,
				StreamConstants.CENTINEL_DOT + StreamConstants.ALERT_DOT);
		query = replaceFirstAlert(query);
		return query;
	}

	private String updateWhenQueryContainsStream(String query) {
		query = replaceFirstCentinelForDBType(query);
		query = query.replace(StreamConstants.STREAM_DOT,
				StreamConstants.CENTINEL_DOT + StreamConstants.STREAM_DOT);
		query = replaceFirstStream(query);
		return query;
	}

	// TODO : This can be moved to some Stream Utility class in future
	public String getSingleQuotedValue(String value) {
		return StreamConstants.SINGLE_QUOTE + value
				+ StreamConstants.SINGLE_QUOTE;
	}
}