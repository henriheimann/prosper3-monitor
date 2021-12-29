package org.urbanenvironmentmonitor.ttn;

import org.junit.jupiter.api.BeforeEach;
import org.urbanenvironmentmonitor.ttn.api.TtnApiApplicationClient;
import org.urbanenvironmentmonitor.ttn.api.TtnApiEndDeviceClient;
import org.urbanenvironmentmonitor.ttn.api.TtnApiNetworkServerClient;
import org.urbanenvironmentmonitor.ttn.config.TtnProperties;

import static org.mockito.Mockito.mock;

class TtnServiceTest
{
	private TtnService ttnService;

	private TtnApiEndDeviceClient ttnDeviceClientMock;
	private TtnApiApplicationClient ttnApiApplicationClientMock;
	private TtnApiNetworkServerClient ttnApiNetworkServerClientMock;
	private TtnProperties ttnPropertiesMock;

	@BeforeEach
	public void setUp()
	{
		ttnDeviceClientMock = mock(TtnApiEndDeviceClient.class);
		ttnApiApplicationClientMock = mock(TtnApiApplicationClient.class);
		ttnApiNetworkServerClientMock = mock(TtnApiNetworkServerClient.class);
		ttnPropertiesMock = mock(TtnProperties.class);

		ttnService = new TtnService(ttnDeviceClientMock, ttnApiApplicationClientMock, ttnApiNetworkServerClientMock,
				ttnPropertiesMock);
	}
}