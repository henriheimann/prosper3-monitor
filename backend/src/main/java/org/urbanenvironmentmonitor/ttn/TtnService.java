package org.urbanenvironmentmonitor.ttn;

import org.springframework.stereotype.Service;
import org.urbanenvironmentmonitor.ttn.api.TtnApiDeviceClient;

@Service
public class TtnService
{
	private final TtnApiDeviceClient ttnDeviceClient;

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	public TtnService(TtnApiDeviceClient ttnDeviceClient)
	{
		this.ttnDeviceClient = ttnDeviceClient;
	}

	public void createDevice()
	{

	}
}
