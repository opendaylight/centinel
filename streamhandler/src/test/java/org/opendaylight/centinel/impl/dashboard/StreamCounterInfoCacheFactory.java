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
