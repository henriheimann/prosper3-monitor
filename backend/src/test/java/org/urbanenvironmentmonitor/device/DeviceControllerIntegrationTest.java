package org.urbanenvironmentmonitor.device;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.urbanenvironmentmonitor.mock.ttn.api.MockTtnApiTestHelper;
import org.urbanenvironmentmonitor.mock.ttn.mqtt.MockTtnMqttTestHelper;
import org.urbanenvironmentmonitor.mock.ttn.mqtt.UplinkMessage;
import org.urbanenvironmentmonitor.shared.AbstractIntegrationTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class DeviceControllerIntegrationTest extends AbstractIntegrationTest
{
	@Autowired
	private MockTtnMqttTestHelper mockTtnMqttTestHelper;

	@Autowired
	private MockTtnApiTestHelper mockTtnApiTestHelper;

	@Test
	public void getDevice()
	{

	}

	@Test
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
	}
}
