package de.p3monitor.devices;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.jayway.jsonpath.JsonPath;
import de.p3monitor.testhelper.mocks.ttn.mqtt.MockTtnMqttTestHelper;
import de.p3monitor.testhelper.mocks.ttn.mqtt.UplinkMessage;
import de.p3monitor.testhelper.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

class DeviceControllerIntegrationTest extends AbstractIntegrationTest
{
	@Autowired
	private MockTtnMqttTestHelper mockTtnMqttTestHelper;

	@Autowired
	private WireMockServer mockTtnApiWireMockServer;

	@Test
	public void createDevice_returnsOkAndCreatedDevice()
	{
		mockTtnApiTestHelper.mockAllCreateDeviceRequests("ttn-device-id-123", "eui-1245", "device-address-1234",
				"network-session-key", "app-session-key");

		String adminToken = getToken("admin", "password");

		post("/devices", adminToken, """
				{
					"name": "device_name",
					"latitude": 40.23,
					"longitude": 12.43,
					"qrCodeId": 10
				}
				""")
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.id").isNotEmpty()
				.jsonPath("$.qrCodeId").isEqualTo(10L)
				.jsonPath("$.name").isEqualTo("device_name")
				.jsonPath("$.ttnId").isEqualTo("ttn-device-id-123")
				.jsonPath("$.latitude").isEqualTo(40.23)
				.jsonPath("$.longitude").isEqualTo(12.43);
	}

	@Test
	public void createDevice_returnsInternalServerError_ifAnyTtnRequestFails()
	{
		mockTtnApiTestHelper.mockAllCreateDeviceRequests("ttn-device-id-123", "eui-1245", "device-address-1234",
				"network-session-key", "app-session-key");

		mockTtnApiWireMockServer.stubFor(WireMock.post(urlEqualTo("/api/v3/applications/ttn-test-app-id/dev-eui"))
				.willReturn(aResponse().withStatus(500)));

		String adminToken = getToken("admin", "password");

		post("/devices", adminToken, """
				{
					"name": "device_name",
					"latitude": 40.23,
					"longitude": 12.43,
					"qrCodeId": 10
				}
				""")
				.expectStatus().is5xxServerError();
	}

	@Test
	public void getDevices_returnsOkAndDeviceWithTtnKeys()
	{
		mockTtnApiTestHelper.mockAllCreateDeviceRequests("ttn-device-id-123", "eui-1245", "device-address-1234",
				"network-session-key", "app-session-key");

		String adminToken = getToken("admin", "password");

		String response = post("/devices", adminToken, """
				{
					"name": "device_name",
					"latitude": 40.23,
					"longitude": 12.43,
					"qrCodeId": 10
				}
				""")
				.expectStatus().isOk()
				.returnResult(String.class)
				.getResponseBody()
				.blockFirst();

		int id = JsonPath.read(response, "$.id");

		get("/devices/" + id, adminToken)
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.id").isEqualTo(id)
				.jsonPath("$.qrCodeId").isEqualTo(10L)
				.jsonPath("$.name").isEqualTo("device_name")
				.jsonPath("$.ttnId").isEqualTo("ttn-device-id-123")
				.jsonPath("$.latitude").isEqualTo(40.23)
				.jsonPath("$.longitude").isEqualTo(12.43)
				.jsonPath("$.longitude").isEqualTo(12.43)
				.jsonPath("$.ttnDeviceAddress").isEqualTo("device-address-1234")
				.jsonPath("$.ttnNetworkSessionKey").isEqualTo("network-session-key")
				.jsonPath("$.ttnApplicationSessionKey").isEqualTo("app-session-key");
	}

	@Test
	public void getDevices_returnsOkAndLastContact() throws Exception
	{
		mockTtnApiTestHelper.mockAllCreateDeviceRequests("ttn-device-id-123", "eui-1245", "device-address-1234",
				"network-session-key", "app-session-key");

		String adminToken = getToken("admin", "password");

		String response = post("/devices", adminToken, """
				{
					"name": "device_name",
					"latitude": 40.23,
					"longitude": 12.43,
					"qrCodeId": 10
				}
				""")
				.expectStatus().isOk()
				.returnResult(String.class)
				.getResponseBody()
				.blockFirst();

		int id = JsonPath.read(response, "$.id");

		mockTtnMqttTestHelper.publishUplinkMessage(UplinkMessage.builder()
				.deviceId("ttn-device-id-123")
				.applicationId("ttn-test-app-id")
				.mqttUsername("ttn-mqtt-username")
				.sensorType(0)
				.batteryVoltage(3.2)
				.humidity(51.0)
				.temperature(20.0)
				.irTemperature(22.0)
				.brightnessCurrent(100000)
				.build());

		Thread.sleep(1500);

		get("/devices/" + id, adminToken)
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.id").isEqualTo(id)
				.jsonPath("$.lastContact.timestamp").isNotEmpty()
				.jsonPath("$.lastContact.sensorType").isEqualTo("CLIMATE_SENSOR")
				.jsonPath("$.lastContact.deviceValues.bat").isEqualTo(3.2)
				.jsonPath("$.lastContact.deviceValues.tmp").isEqualTo(20.0)
				.jsonPath("$.lastContact.deviceValues.hum").isEqualTo(51.0)
				.jsonPath("$.lastContact.deviceValues.irt").isEqualTo(22.0)
				.jsonPath("$.lastContact.deviceValues.bgh").isEqualTo(1);
	}

	/*@Test
	public void getLiveMeasurementsReturnsMeasurementsPublishedViaMqtt() throws Exception
	{
		// One message needs to be published before, otherwise exchange() blocks
		mockTtnMqttTestHelper.publishUplinkMessage(UplinkMessage.builder().build());

		String userToken = getToken("user", "password");

		Flux<String> eventFlux = client.get().uri("/devices/1/live-measurements")
				.header("Authorization", "Bearer " + userToken)
				.accept(MediaType.TEXT_EVENT_STREAM)
				.exchange()
				.expectStatus().isOk()
				.returnResult(String.class)
				.getResponseBody();

		mockTtnMqttTestHelper.publishUplinkMessage(UplinkMessage.builder().build());

		StepVerifier.create(eventFlux)
				.expectNext("v3/application-id/devices/device-id/up")
				.expectNext("v3/application-id/devices/device-id/up")
				.thenCancel()
				.verify();
	}*/
}
