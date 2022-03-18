package de.p3monitor.testhelper.mocks.ttn.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Component
@AllArgsConstructor
public class MockTtnApiTestHelper
{
	private final WireMockServer mockTtnApiWireMockServer;

	public void resetMocks()
	{
		mockTtnApiWireMockServer.resetAll();
	}
	
	public void mockAllCreateDeviceRequests(String ttnDeviceId, String deviceEui, String deviceAddress,
	                                        String networkSessionKey, String appSessionKey)
	{
		mockGenerateDeviceEui(deviceEui);
		mockGenerateDeviceAddress(deviceAddress);
		mockCreateDevice(ttnDeviceId, deviceEui);
		mockCreateDeviceOnApplicationServer(ttnDeviceId, deviceEui, deviceAddress, appSessionKey);
		mockCreateDeviceOnNetworkServer(ttnDeviceId, deviceEui, deviceAddress, networkSessionKey);
	}

	public void mockGenerateDeviceEui(String deviceEui) {
		mockTtnApiWireMockServer.stubFor(post(urlEqualTo("/api/v3/applications/ttn-test-app-id/dev-eui"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody("""
								{
									"dev_eui": "%s"
								}
								""".formatted(deviceEui))));
	}

	public void mockGenerateDeviceAddress(String deviceAddress) {
		mockTtnApiWireMockServer.stubFor(get(urlEqualTo("/api/v3/ns/dev_addr"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody("""
								{
									"dev_addr": "%s"
								}
								""".formatted(deviceAddress))));
	}

	public void mockCreateDevice(String ttnDeviceId, String deviceEui) {
		mockTtnApiWireMockServer.stubFor(post(urlEqualTo("/api/v3/applications/ttn-test-app-id/devices"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody("""
								{
									"ids": {
										"device_id": "%s",
										"dev_eui": "%s"
									}
								}
								""".formatted(ttnDeviceId, deviceEui))));
	}

	public void mockCreateDeviceOnNetworkServer(String ttnDeviceId, String deviceEui, String deviceAddress,
	                                            String networkSessionKey) {
		mockTtnApiWireMockServer.stubFor(post(urlEqualTo("/api/v3/ns/applications/ttn-test-app-id/devices"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody("""
								{
									"ids": {
										"device_id": "%s",
										"dev_eui": "%s"
									},
									"session": {
										"dev_addr": "%s",
										"keys": {
											"f_nwk_s_int_key": {
												"key": "%s"
											}
										}
									}
								}
								""".formatted(ttnDeviceId, deviceEui, deviceAddress, networkSessionKey))));
	}

	public void mockCreateDeviceOnApplicationServer(String ttnDeviceId, String deviceEui, String deviceAddress,
	                                                String appSessionKey) {
		mockTtnApiWireMockServer.stubFor(post(urlEqualTo("/api/v3/as/applications/ttn-test-app-id/devices"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody("""
								{
									"ids": {
										"device_id": "%s",
										"dev_eui": "%s"
									},
									"session": {
										"dev_addr": "%s",
										"keys": {
											"app_s_key": {
												"key": "%s"
											}
										}
									}
								}
								""".formatted(ttnDeviceId, deviceEui, deviceAddress, appSessionKey))));
	}
}
