package de.p3monitor.ttn;

import de.p3monitor.ttn.api.TtnApiApplicationClient;
import de.p3monitor.ttn.api.TtnApiNetworkServerClient;
import de.p3monitor.ttn.config.TtnProperties;
import org.junit.jupiter.api.BeforeEach;
import de.p3monitor.ttn.api.TtnApiEndDeviceClient;

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