package org.urbanenvironmentmonitor.device.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.urbanenvironmentmonitor.mock.ttn.mqtt.MockTtnMqttService;
import org.urbanenvironmentmonitor.mock.ttn.mqtt.UplinkMessage;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ActiveProfiles(profiles = "test")
@SpringBootTest
class DeviceControllerTest
{
	@Autowired
	private MockTtnMqttService mockTtnMqttService;

	private WebTestClient client;

	@BeforeEach
	void setUp(ApplicationContext context)
	{
		client = WebTestClient.bindToApplicationContext(context).build();
	}

	@Test
	public void getLiveMeasurementsReturnsMeasurementsPublishedViaMqtt() throws Exception
	{
		// One message needs to be published before, otherwise exchange() blocks
		mockTtnMqttService.publishUplinkMessage(UplinkMessage.builder().build());

		Flux<String> eventFlux = client.get().uri("/devices/1/live-measurements")
				.accept(MediaType.TEXT_EVENT_STREAM)
				.exchange()
				.expectStatus().isOk()
				.returnResult(String.class)
				.getResponseBody();

		mockTtnMqttService.publishUplinkMessage(UplinkMessage.builder().build());

		StepVerifier.create(eventFlux)
				.expectNext("v3/application-id/devices/device-id/up")
				.expectNext("v3/application-id/devices/device-id/up")
				.thenCancel()
				.verify();
	}
}
