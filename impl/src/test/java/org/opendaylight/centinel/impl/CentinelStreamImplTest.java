/*
 * Copyright (c) 2015 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.DeleteStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.DeleteStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.GetStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.GetStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.PauseStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.PauseStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.ResumeStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.ResumeStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.SetStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.StreamRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.UpdateStreamInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.UpdateStreamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.stream.rev150105.streamrecord.StreamList;
import org.opendaylight.yangtools.yang.common.RpcResult;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.Futures;

/**
 * @author Sunaina Khanna
 * 
 *         This class consists of junit test cases for streamImpl .
 */

public class CentinelStreamImplTest {
	CentinelStreamImpl centinelStreamImpl;
	@Mock
	private DataBroker mockDataBroker;

	@Mock
	private CentinelStreamImpl mockCentinelStreamImpl;
	private MockCentinelStreamImpl myMock = new MockCentinelStreamImpl();
	private CentinelStreamImplFactory centinelStreamImplFactory = new CentinelStreamImplFactory();

	@Before
	public void beforeTest() {
		MockitoAnnotations.initMocks(this);
		centinelStreamImpl = new MockCentinelStreamImpl();
		myMock.setDataProvider(mockDataBroker);
	}

	@Test
	public void testsetRule() {

	}

	@Test
	public void testSetStreamFailureOnInvalidInput() {

		// build dummy input
		SetStreamInput input = centinelStreamImplFactory
				.setInputForStreamWithInvalidInput();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		Future<RpcResult<SetStreamOutput>> futureOutput = myMock
				.setStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getResult();
		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testSetStreamRuleFailureOnInvalidInput() {

		// build dummy input
		SetRuleInput input = centinelStreamImplFactory
				.setInputForStreamRuleWithInvalidInput();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		Future<RpcResult<SetRuleOutput>> futureOutput = myMock.setRule(input);

		boolean caught = false;
		try {
			futureOutput.get().getResult();
		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testSetStreamRuleFailureOnInvalidInputWithType() {

		// build dummy input
		SetRuleInput input = centinelStreamImplFactory
				.setInputForStreamRuleWithInvalidInputWithType();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		Future<RpcResult<SetRuleOutput>> futureOutput = myMock.setRule(input);

		boolean caught = false;
		try {
			futureOutput.get().getResult();
		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testGetStreamFailureOnNoStreamInDataStore() {

		GetStreamInput input = centinelStreamImplFactory
				.validInputForGetStream();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();
		Future<RpcResult<GetStreamOutput>> futureOutput = myMock
				.getStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getResult();
		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testGetStreamFailureOnInvalidStreamId() {

		GetStreamInput input = centinelStreamImplFactory
				.invalidInputForGetStream();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();
		Future<RpcResult<GetStreamOutput>> futureOutput = myMock
				.getStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getResult();
		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testStreamRuleOnInValidStreamId() {
		// build dummy input
		SetRuleInput input = centinelStreamImplFactory
				.setInputForStreamRuleWithInValidStreamId();
		StreamList input1 = centinelStreamImplFactory
				.setInputForStreamWithValidInputForRule();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();
		streamList.add(input1);

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		Future<RpcResult<SetRuleOutput>> futureOutput = myMock.setRule(input);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();

		boolean caught = false;
		try {
			futureOutput.get().getResult();
		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testStreamOnValidInput() {

		// build dummy input
		SetStreamInput input = centinelStreamImplFactory
				.setInputForStreamWithValidInput();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		Future<RpcResult<SetStreamOutput>> futureOutput = myMock
				.setStream(input);

		try {
			SetStreamOutput out = futureOutput.get().getResult();
			String expectedMsg = centinelStreamImplFactory
					.expectedStreamObject().toString();
			assertEquals(expectedMsg, out.toString());

		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getCause().toString());
		} catch (ExecutionException e) {
			e.printStackTrace();
			fail(e.getCause().toString());
		}

	}

	@Test
	public void testDeleteStreamOnValidInput() {
		// build dummy inputsetMessage
		DeleteStreamInput input = centinelStreamImplFactory
				.deleteInputValidValuesForStream();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();
		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);

		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();
		doNothing().when(mockReadWriteTx).delete(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId.child(StreamList.class,
						streamListObj.getKey()));
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();

		Future<RpcResult<DeleteStreamOutput>> futureOutput = myMock
				.deleteStream(input);
		assertNotNull(futureOutput);
	}

	@Test
	public void testDeleteStreamFailureDueToInvalidInput() {
		// build dummy input
		DeleteStreamInput input = centinelStreamImplFactory
				.deleteInputWithInvalidValuesForStream();
		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();
		List<StreamList> streamList = new ArrayList<StreamList>();
		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();
		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		Future<RpcResult<DeleteStreamOutput>> futureOutput = myMock
				.deleteStream(input);
		boolean caught = false;
		try {
			futureOutput.get().getResult();
		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testDeleteStreamFailureDueToInvalidStreamId() {
		// build dummy input
		DeleteStreamInput input = centinelStreamImplFactory
				.deleteInputWithInvalidIdForStream();
		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();
		List<StreamList> streamList = new ArrayList<StreamList>();
		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();
		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		Future<RpcResult<DeleteStreamOutput>> futureOutput = myMock
				.deleteStream(input);
		boolean caught = false;
		try {
			futureOutput.get().getResult();
		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testPauseStreamFailureDueToInvalidStreamId() {
		// build dummy input
		PauseStreamInput input = centinelStreamImplFactory
				.pauseInputValidValuesForStream();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();

		CentinelStreamImpl obj = new CentinelStreamImpl();
		Class mockCentinelStreamImpl = CentinelStreamImpl.class;
		StreamList strmListObj = null;

		// reflecting build method
		Class[] arg = new Class[] { StreamList.class, String.class,
				PauseStreamInput.class };
		Method method = null;
		try {
			method = mockCentinelStreamImpl.getDeclaredMethod(
					"buildPausedStreamListRecord", arg);
			method.setAccessible(true);
			strmListObj = (StreamList) method.invoke(obj, streamListObj,
					streamListObj.getStreamID(), input);
		} catch (NoSuchMethodException | IllegalAccessException
				| SecurityException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		streamList.add(strmListObj);

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		Future<RpcResult<PauseStreamOutput>> futureOutput = myMock
				.pauseStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getErrors();

		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testPauseStreamFailureDueToNullStreamId() {
		// build dummy input
		PauseStreamInput input = centinelStreamImplFactory
				.pauseInputValidValuesForStreamNull();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();

		CentinelStreamImpl obj = new CentinelStreamImpl();
		Class mockCentinelStreamImpl = CentinelStreamImpl.class;
		StreamList strmListObj = null;

		// reflecting build method
		Class[] arg = new Class[] { StreamList.class, String.class,
				PauseStreamInput.class };
		Method method = null;
		try {
			method = mockCentinelStreamImpl.getDeclaredMethod(
					"buildPausedStreamListRecord", arg);
			method.setAccessible(true);
			strmListObj = (StreamList) method.invoke(obj, streamListObj,
					streamListObj.getStreamID(), input);
		} catch (NoSuchMethodException | IllegalAccessException
				| SecurityException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		streamList.add(strmListObj);

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		Future<RpcResult<PauseStreamOutput>> futureOutput = myMock
				.pauseStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getErrors();

		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testResumeStreamFailureDueToStreamIdNull() {
		// build dummy input
		ResumeStreamInput input = centinelStreamImplFactory
				.resumeInputValidValuesForStreamNull();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();

		CentinelStreamImpl obj = new CentinelStreamImpl();
		Class mockCentinelStreamImpl = CentinelStreamImpl.class;
		StreamList strmListObj = null;

		// reflecting build method
		Class[] arg = new Class[] { StreamList.class, String.class,
				ResumeStreamInput.class };
		Method method = null;
		try {
			method = mockCentinelStreamImpl.getDeclaredMethod(
					"buildResumedStreamListRecord", arg);
			method.setAccessible(true);
			strmListObj = (StreamList) method.invoke(obj, streamListObj,
					streamListObj.getStreamID(), input);
		} catch (NoSuchMethodException | IllegalAccessException
				| SecurityException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();
		streamList.add(strmListObj);

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		Future<RpcResult<ResumeStreamOutput>> futureOutput = myMock
				.resumeStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getErrors();

		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testResumeStreamFailureDueToInvalidStreamId() {
		// build dummy input
		ResumeStreamInput input = centinelStreamImplFactory
				.resumeInputValidValuesForStream();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();

		CentinelStreamImpl obj = new CentinelStreamImpl();
		Class mockCentinelStreamImpl = CentinelStreamImpl.class;
		StreamList strmListObj = null;

		// reflecting build method
		Class[] arg = new Class[] { StreamList.class, String.class,
				ResumeStreamInput.class };
		Method method = null;
		try {
			method = mockCentinelStreamImpl.getDeclaredMethod(
					"buildResumedStreamListRecord", arg);
			method.setAccessible(true);
			strmListObj = (StreamList) method.invoke(obj, streamListObj,
					streamListObj.getStreamID(), input);
		} catch (NoSuchMethodException | IllegalAccessException
				| SecurityException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();
		streamList.add(strmListObj);

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		Future<RpcResult<ResumeStreamOutput>> futureOutput = myMock
				.resumeStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getErrors();

		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testUpdateStreamFailureDueToInvalidInput() {
		// build dummy input
		UpdateStreamInput input = centinelStreamImplFactory
				.updateInvalidInputForStream();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();

		CentinelStreamImpl obj = new CentinelStreamImpl();
		Class mockCentinelStreamImpl = CentinelStreamImpl.class;
		StreamList strmListObj = null;

		// reflecting build method
		Class[] arg = new Class[] { UpdateStreamInput.class, StreamList.class };
		Method method = null;
		try {
			method = mockCentinelStreamImpl.getDeclaredMethod(
					"buildUpdateStreamListRecord", arg);
			method.setAccessible(true);
			strmListObj = (StreamList) method.invoke(obj, input, streamListObj);
		} catch (NoSuchMethodException | IllegalAccessException
				| SecurityException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);
		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();
		streamList.add(strmListObj);

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		Future<RpcResult<UpdateStreamOutput>> futureOutput = myMock
				.updateStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getErrors();

		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testUpdateStreamOnValidInput() {
		// build dummy input
		UpdateStreamInput input = centinelStreamImplFactory
				.updateInvalidInputForStream();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();

		CentinelStreamImpl obj = new CentinelStreamImpl();
		Class mockCentinelStreamImpl = CentinelStreamImpl.class;
		StreamList strmListObj = null;

		// reflecting build method
		Class[] arg = new Class[] { UpdateStreamInput.class, StreamList.class };
		Method method = null;
		try {
			method = mockCentinelStreamImpl.getDeclaredMethod(
					"buildUpdateStreamListRecord", arg);
			method.setAccessible(true);
			strmListObj = (StreamList) method.invoke(obj, input, streamListObj);
		} catch (NoSuchMethodException | IllegalAccessException
				| SecurityException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();
		streamList.add(strmListObj);

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();
		Future<RpcResult<UpdateStreamOutput>> futureOutput = myMock
				.updateStream(input);

		assertNotNull(futureOutput);
	}

	@Test
	public void testUpdateStreamOnNoRecordInOperationalDatastore() {
		// build dummy input
		UpdateStreamInput input = centinelStreamImplFactory
				.updateValidInputForStream();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();

		CentinelStreamImpl obj = new CentinelStreamImpl();
		Class mockCentinelStreamImpl = CentinelStreamImpl.class;
		StreamList strmListObj = null;

		// reflecting build method
		Class[] arg = new Class[] { UpdateStreamInput.class, StreamList.class };
		Method method = null;
		try {
			method = mockCentinelStreamImpl.getDeclaredMethod(
					"buildUpdateStreamListRecord", arg);
			method.setAccessible(true);
			strmListObj = (StreamList) method.invoke(obj, input, streamListObj);
		} catch (NoSuchMethodException | IllegalAccessException
				| SecurityException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();
		streamList.add(strmListObj);

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(Optional.absent())).when(
				mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
				MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();
		Future<RpcResult<UpdateStreamOutput>> futureOutput = myMock
				.updateStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getErrors();

		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testbuildUpdateStreamListRecord() {
		UpdateStreamInput input = centinelStreamImplFactory
				.updateValidInputForStream();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();
		Class mockCentinelStreamImpl = CentinelStreamImpl.class;
		StreamList strmListObj = null;
		// reflecting build method
		Class[] arg = new Class[] { UpdateStreamInput.class, StreamList.class };
		Method method = null;
		try {
			CentinelStreamImpl obj = new CentinelStreamImpl();
			method = mockCentinelStreamImpl.getDeclaredMethod(
					"buildUpdateStreamListRecord", arg);
			method.setAccessible(true);
			strmListObj = (StreamList) method.invoke(obj, input, streamListObj);
		} catch (NoSuchMethodException | IllegalAccessException
				| SecurityException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBuildStreamRule() {
		SetStreamInput input = centinelStreamImplFactory
				.setInputForStreamWithValidInput();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();
		CentinelStreamImpl obj = new CentinelStreamImpl();
		Class mockCentinelStreamImpl = CentinelStreamImpl.class;
		StreamList strmListObj = null;
		// reflecting build method
		Class[] arg = new Class[] { StreamList.class, String.class };
		Method method = null;
		try {
			method = mockCentinelStreamImpl.getDeclaredMethod(
					"buildStreamRule", arg);
			method.setAccessible(true);
			method.invoke(obj, streamListObj, "");
		} catch (NoSuchMethodException | IllegalAccessException
				| SecurityException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDeleteStreamOnNoRecordInOperationalDatastore() {
		// build dummy input
		DeleteStreamInput input = centinelStreamImplFactory
				.deleteInputValidValuesForStream();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(Optional.absent())).when(
				mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
				MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();
		Future<RpcResult<DeleteStreamOutput>> futureOutput = myMock
				.deleteStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getErrors();

		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testPauseStreamOnNoRecordInOperationalDatastore() {
		// build dummy input
		PauseStreamInput input = centinelStreamImplFactory
				.pauseInputValidValuesForStream();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(Optional.absent())).when(
				mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
				MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();
		Future<RpcResult<PauseStreamOutput>> futureOutput = myMock
				.pauseStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getErrors();

		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testResumeStreamOnRecordPresentInOperationalDatastore() {
		// build dummy input
		ResumeStreamInput input = centinelStreamImplFactory
				.resumeInputValidValuesForStream();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();
		StreamRecord record = centinelStreamImplFactory.getStreamRecord();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		Optional<StreamRecord> expectedOperationalRecord = Optional.of(record);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(expectedOperationalRecord))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);
		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();

		Future<RpcResult<ResumeStreamOutput>> futureOutput = myMock
				.resumeStream(input);

	}

	@Test
	public void testPauseStreamOnRecordPresentInOperationalDataStore() {
		// build dummy input
		PauseStreamInput input = centinelStreamImplFactory
				.pauseInputValidValuesForStream();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();
		StreamRecord record = centinelStreamImplFactory.getStreamRecord();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		Optional<StreamRecord> expectedOperationalRecord = Optional.of(record);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(expectedOperationalRecord))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();

		Future<RpcResult<PauseStreamOutput>> futureOutput = myMock
				.pauseStream(input);
	}

	@Test
	public void testGetStreamOnRecordPresentInOperationalDataStore() {
		// build dummy input
		GetStreamInput input = centinelStreamImplFactory
				.validInputForGetStream();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();
		StreamRecord record = centinelStreamImplFactory.getStreamRecord();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		Optional<StreamRecord> expectedOperationalRecord = Optional.of(record);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(expectedOperationalRecord))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();

		Future<RpcResult<GetStreamOutput>> futureOutput = myMock
				.getStream(input);
		try {
			myMock.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGenerateRandomId() {
		CentinelStreamImpl c = new CentinelStreamImpl();
		c.generateRandomId();
	}

	@Test
	public void testSetRuleOnRecordPresentInOperationalDataStore() {
		// build dummy input
		SetRuleInput input = centinelStreamImplFactory
				.setInputForStreamRuleWithInValidStreamId();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();
		StreamRecord record = centinelStreamImplFactory.getStreamRecord();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		Optional<StreamRecord> expectedOperationalRecord = Optional.of(record);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(expectedOperationalRecord))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();

		Future<RpcResult<SetRuleOutput>> futureOutput = myMock.setRule(input);
	}

	@Test
	public void testUpdateStreamOnRecordPresentInOperationalDataStore() {
		// build dummy input
		UpdateStreamInput input = centinelStreamImplFactory
				.updateValidInputForStream();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();
		StreamRecord record = centinelStreamImplFactory.getStreamRecord();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		Optional<StreamRecord> expectedOperationalRecord = Optional.of(record);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(expectedOperationalRecord))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();

		Future<RpcResult<UpdateStreamOutput>> futureOutput = myMock
				.updateStream(input);
	}

	@Test
	public void testDeleteStreamOnRecordPresentInOperationalDataStore() {
		// build dummy input
		DeleteStreamInput input = centinelStreamImplFactory
				.deleteInputValidValuesForStream();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();
		StreamRecord record = centinelStreamImplFactory.getStreamRecord();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		Optional<StreamRecord> expectedOperationalRecord = Optional.of(record);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(expectedOperationalRecord))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();

		Future<RpcResult<DeleteStreamOutput>> futureOutput = myMock
				.deleteStream(input);
	}

	@Test
	public void testResumeStream() {
		// build dummy input
		ResumeStreamInput input = centinelStreamImplFactory
				.resumeInputValidValuesForStream();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(Optional.absent())).when(
				mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
				MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();
		Future<RpcResult<ResumeStreamOutput>> futureOutput = myMock
				.resumeStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getErrors();

		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testUpdateStreamFailureIfIdDoesNotMatch() {
		// build dummy input
		UpdateStreamInput input = centinelStreamImplFactory
				.updateValidInputForStream();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();
		streamList.add(streamListObj);

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();
		Future<RpcResult<UpdateStreamOutput>> futureOutput = myMock
				.updateStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getErrors();

		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testDeleteStreamFailureIfIdDoesNotMatch() {
		// build dummy input
		DeleteStreamInput input = centinelStreamImplFactory
				.deleteInputValidValuesForStream();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();
		streamList.add(streamListObj);

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();
		Future<RpcResult<DeleteStreamOutput>> futureOutput = myMock
				.deleteStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getErrors();

		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testPauseStreamFailureIfIdDoesNotMatch() {
		// build dummy input
		PauseStreamInput input = centinelStreamImplFactory
				.pauseInputValidValuesForStream();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();
		streamList.add(streamListObj);

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();
		Future<RpcResult<PauseStreamOutput>> futureOutput = myMock
				.pauseStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getErrors();

		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testResumeStreamFailureIfIdDoesNotMatch() {
		// build dummy input
		ResumeStreamInput input = centinelStreamImplFactory
				.resumeInputValidValuesForStream();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();
		streamList.add(streamListObj);

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.CONFIGURATION,
						MockCentinelStreamImpl.streamRecordId);

		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);

		doNothing().when(mockReadWriteTx).merge(
				LogicalDatastoreType.CONFIGURATION,
				MockCentinelStreamImpl.streamRecordId,
				new StreamRecordBuilder().setStreamList(streamList).build(),
				false);
		doReturn(Mockito.mock(CheckedFuture.class)).when(mockReadWriteTx)
				.submit();
		Future<RpcResult<ResumeStreamOutput>> futureOutput = myMock
				.resumeStream(input);

		boolean caught = false;
		try {
			futureOutput.get().getErrors();

		} catch (ExecutionException | InterruptedException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testGetStreamOnValidInput() {
		// build dummy input
		GetStreamInput input = centinelStreamImplFactory
				.validInputForGetStream();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		streamList.add(streamListObj);

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);
		Future<RpcResult<GetStreamOutput>> futureOutput = myMock
				.getStream(input);

		assertNotNull(futureOutput);
	}

	@Test
	public void testResumeStreamOnValidInput() {
		// build dummy input
		ResumeStreamInput input = centinelStreamImplFactory
				.resumeInputValidValuesForStream();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		streamList.add(streamListObj);

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);
		Future<RpcResult<ResumeStreamOutput>> futureOutput = myMock
				.resumeStream(input);

		assertNotNull(futureOutput);
	}

	@Test
	public void testPauseStreamOnValidInput() {
		// build dummy input
		PauseStreamInput input = centinelStreamImplFactory
				.pauseInputValidValuesForStream();
		StreamList streamListObj = centinelStreamImplFactory
				.mockStreamObjectBuilder();

		// to mock the tx object
		ReadWriteTransaction mockReadWriteTx = mock(ReadWriteTransaction.class);

		doReturn(mockReadWriteTx).when(mockDataBroker)
				.newReadWriteTransaction();

		List<StreamList> streamList = new ArrayList<StreamList>();

		streamList.add(streamListObj);

		StreamRecord streamRecord = new StreamRecordBuilder().setStreamList(
				streamList).build();

		Optional<StreamRecord> expected = Optional.of(streamRecord);
		doReturn(Futures.immediateCheckedFuture(expected))
				.when(mockReadWriteTx).read(LogicalDatastoreType.OPERATIONAL,
						MockCentinelStreamImpl.streamRecordId);
		Future<RpcResult<PauseStreamOutput>> futureOutput = myMock
				.pauseStream(input);

		assertNotNull(futureOutput);
	}

	@After
	public void AfterTest() {
		centinelStreamImpl = null;

	}

	public CentinelStreamImplFactory getCentinelImplFactory() {
		return centinelStreamImplFactory;
	}

	public void setCentinelImplFactory(
			CentinelStreamImplFactory centinelImplFactory) {
		this.centinelStreamImplFactory = centinelImplFactory;
	}

	private class MockCentinelStreamImpl extends CentinelStreamImpl {
		public String generateRandomId() {
			return "1000";
		}
	}
}
