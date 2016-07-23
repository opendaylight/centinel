/*
 * Copyright (c) 2016 Tata Consultancy Services and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.centinel.impl.dashboard;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.opendaylight.streamhandler.impl.StreamhandlerImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.subscribe.rev150105.Subscribe.Mode;

public class StreamCounterInfoCacheFactory {


	public String removeCounterForWidgetInput()
	{
		return "1001";
	}
	public WidgetStreamCounterVO widgetStreamCounterVOInput()
	{
		WidgetStreamCounterVO mockWidgetStreamCounterVO = new WidgetStreamCounterVO();
		mockWidgetStreamCounterVO.setCal(Calendar.getInstance());
		mockWidgetStreamCounterVO.setMode(Mode.Alert);
		mockWidgetStreamCounterVO.setWidgetID("1001");
		mockWidgetStreamCounterVO.setStreamID("1001");
		return mockWidgetStreamCounterVO;
	}
}
