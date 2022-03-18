package de.p3monitor.devices;

import de.p3monitor.devices.requests.CreateDeviceRequest;
import de.p3monitor.devices.responses.DeviceResponse;
import de.p3monitor.influxdb.InfluxDbService;
import de.p3monitor.testhelper.data.TestEndDevices;
import de.p3monitor.ttn.TtnService;
import de.p3monitor.ttn.api.dtos.EndDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("ReactiveStreamsUnusedPublisher")
class DeviceServiceTest
{
	private DeviceRepository deviceRepositoryMock;
	private TtnService ttnServiceMock;
	private InfluxDbService influxDbServiceMock;

	private DeviceService deviceService;

	@BeforeEach
	public void setUp()
	{
		deviceRepositoryMock = mock(DeviceRepository.class);
		ttnServiceMock = mock(TtnService.class);
		influxDbServiceMock = mock(InfluxDbService.class);

		deviceService = new DeviceService(deviceRepositoryMock, ttnServiceMock, influxDbServiceMock);
	}

	@Test
	public void createDevice_returnsDeviceResponseWithValidTtnSync_forGoodTtnDeviceCreation()
	{
		when(deviceRepositoryMock.save(any())).thenAnswer(i -> {
			DeviceEntity deviceEntity = i.getArgument(0);
			deviceEntity.setId(1L);
			return Mono.just(deviceEntity);
		});

		EndDevice endDevice = TestEndDevices.create("dev-id", "dev-eui", "dev-address", "app-session-key",
				"app-network-key", LocalDateTime.of(2021, 1, 1, 12, 43), LocalDateTime.of(2021, 2, 5, 19, 22));
		when(ttnServiceMock.createEndDevice()).thenReturn(Mono.just(endDevice));

		when(influxDbServiceMock.getLatestDeviceValues("dev-id")).thenReturn(Mono.empty());

		Mono<DeviceResponse> response = deviceService.createDevice(new CreateDeviceRequest("name", 10.0, 20.0, 7L));

		StepVerifier.create(response)
				.expectNext(new DeviceResponse(1L, 7L, "name", "dev-id", null, null, null, 10.0, 20.0, null))
				.expectComplete()
				.verify();

		verify(deviceRepositoryMock, atLeastOnce()).save(new DeviceEntity(1L, 7L, "name", "dev-id", "dev-address",
				"app-network-key", "app-session-key", 10.0, 20.0));
	}

	@Test
	public void createDevice_returnsDeviceResponseWithTtnRequestError_forBadTtnDeviceCreation()
	{
		when(deviceRepositoryMock.save(any())).thenAnswer(i -> {
			DeviceEntity deviceEntity = i.getArgument(0);
			deviceEntity.setId(1L);
			return Mono.just(deviceEntity);
		});

		Exception exception = new RuntimeException("Any error");
		when(ttnServiceMock.createEndDevice()).thenReturn(Mono.error(exception));

		Mono<DeviceResponse> response = deviceService.createDevice(new CreateDeviceRequest("name", 10.0, 20.0, 7L));

		StepVerifier.create(response)
				.expectError(RuntimeException.class)
				.verify();
	}
}
