package org.opendaylight.centinel.impl.dashboard;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Before;
import org.junit.Test;


public class StreamCounterInfoCacheTest {

	private StreamCounterInfoCache mockStreamCounterInfoCache;
	StreamCounterInfoCacheFactory streamCounterInfoCacheFactory ;
	private List<WidgetStreamCounterVO> mockListofcounter = new ArrayList<WidgetStreamCounterVO>();
	WidgetStreamCounterVO widgetStreamCounterVO=null;
	@Before
	public void beforeTest()
	{
		streamCounterInfoCacheFactory = new StreamCounterInfoCacheFactory();
		mockStreamCounterInfoCache = StreamCounterInfoCache.getInstance();
		widgetStreamCounterVO = streamCounterInfoCacheFactory.widgetStreamCounterVOInput();
	}
	
	@Test
	public void removeCounterForWidgetTest()
	{
		mockListofcounter.add(widgetStreamCounterVO);
//		mockStreamCounterInfoCache.addCounter(streamCounterInfoCacheFactory.widgetStreamCounterVOInput(), mockStreamhandlerImpl);
//		mockStreamCounterInfoCache.removeCounterForWidget(streamCounterInfoCacheFactory.removeCounterForWidgetInput());
		mockStreamCounterInfoCache.setListofcounter(mockListofcounter);
		int counter =mockStreamCounterInfoCache.getCounterValue(streamCounterInfoCacheFactory.removeCounterForWidgetInput());
			assertNotNull(counter);
	}

	@Test
	public void removeCounterForWidgetListEmptyTest()
	{
		int counter =mockStreamCounterInfoCache.getCounterValue(streamCounterInfoCacheFactory.removeCounterForWidgetInput());
			assertNotNull(counter);
	}
	@Test
	public void incrementCounterTest()
	{
		mockListofcounter.add(widgetStreamCounterVO);
		mockStreamCounterInfoCache.setListofcounter(mockListofcounter);
		mockStreamCounterInfoCache.incrementCounter(streamCounterInfoCacheFactory.removeCounterForWidgetInput());
			assertNotNull(mockStreamCounterInfoCache.toString());
	}

}
