package org.opendaylight.centinel.laas.impl;

import org.junit.Test;
import org.opendaylight.laas.impl.ConfigurationChangeLaasImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.configuration.rev150105.ConfigurationChangedBuilder;

public class ConfigurationChangeLaasImplTest {
	

	
	@Test
	public void onConfigurationChangedTest()
	{
		ConfigurationChangeLaasImpl changedconf = new ConfigurationChangeLaasImpl();
		ConfigurationChangedBuilder notification = new ConfigurationChangedBuilder();
		notification.setGraylogIp("172.21.88.156");
		notification.setGraylogPort("12900");
		changedconf.onConfigurationChanged(notification.build());
	}

}
