package de.p3monitor.device;

import de.p3monitor.device.dtos.*;
import de.p3monitor.testdata.TestDeviceEntities;
import de.p3monitor.testdata.TestEndDevices;
import de.p3monitor.testdata.TestWebClientResponseException;
import de.p3monitor.ttn.TtnService;
import de.p3monitor.ttn.api.dtos.EndDevice;
import de.p3monitor.ttn.exception.TtnApiNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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

	private DeviceService deviceService;

	@BeforeEach
	public void setUp()
	{
		deviceRepositoryMock = mock(DeviceRepository.class);
		ttnServiceMock = mock(TtnService.class);

		deviceService = new DeviceService(deviceRepositoryMock, ttnServiceMock);
	}

	@Test
	public void createDevice_returnsDeviceResponseWithValidTtnSync_forGoodTtnDeviceCreation()
	{
		when(deviceRepositoryMock.save(any())).thenAnswer(i -> {
			DeviceEntity deviceEntity = i.getArgument(0);
			deviceEntity.setId(1L);
			return Mono.just(deviceEntity);
		});

		EndDevice endDevice = TestEndDevices.create("dev-id", "dev-eui",
				LocalDateTime.of(2021, 1, 1, 12, 43), LocalDateTime.of(2021, 2, 5, 19, 22));
		when(ttnServiceMock.createEndDevice()).thenReturn(Mono.just(endDevice));

		Mono<DeviceResponse> response = deviceService.createDevice(new CreateDeviceRequest("name"));

		StepVerifier.create(response)
				.expectNext(new DeviceResponse(1L, "name", new TtnSyncResponse(TtnSyncState.VALID,
						new TtnDeviceResponse(LocalDateTime.of(2021, 1, 1, 12, 43)))))
				.expectComplete()
				.verify();

		verify(deviceRepositoryMock, atLeastOnce()).save(new DeviceEntity(1L, "name", "dev-id"));
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

		Mono<DeviceResponse> response = deviceService.createDevice(new CreateDeviceRequest("name"));

		StepVerifier.create(response)
				.expectNext(new DeviceResponse(1L, "name", new TtnSyncResponse(TtnSyncState.NO_ASSOCIATION, null)))
				.expectComplete()
				.verify();
	}

	@Test
	public void getDevice_returnsDeviceResponseWithValidTtnSync_forGoodTtnResponse()
	{
		DeviceEntity deviceEntity = TestDeviceEntities.create(1L, "name", "ttn-id");
		when(deviceRepositoryMock.findById(1L)).thenReturn(Mono.just(deviceEntity));

		EndDevice endDevice = TestEndDevices.create("dev-id", "dev-eui",
				LocalDateTime.of(2021, 1, 1, 12, 43), LocalDateTime.of(2021, 2, 5, 19, 22));
		when(ttnServiceMock.getEndDevice("ttn-id")).thenReturn(Mono.just(endDevice));

		Mono<DeviceResponse> response = deviceService.getDevice(1L);

		StepVerifier.create(response)
				.expectNext(new DeviceResponse(1L, "name", new TtnSyncResponse(TtnSyncState.VALID,
						new TtnDeviceResponse(LocalDateTime.of(2021, 1, 1, 12, 43)))))
				.expectComplete()
				.verify();
	}

	@Test
	public void getDevice_returnsDeviceResponseWithNoAssociation_forNullTtnId()
	{
		DeviceEntity deviceEntity = TestDeviceEntities.create(1L, "name", null);
		when(deviceRepositoryMock.findById(1L)).thenReturn(Mono.just(deviceEntity));

		Mono<DeviceResponse> response = deviceService.getDevice(1L);

		StepVerifier.create(response)
				.expectNext(new DeviceResponse(1L, "name", new TtnSyncResponse(TtnSyncState.NO_ASSOCIATION, null)))
				.expectComplete()
				.verify();
	}

	@Test
	public void getDevice_returnsDeviceResponseWithNoMatch_forNotFoundTtnResponse()
	{
		DeviceEntity deviceEntity = TestDeviceEntities.create(1L, "name", "ttn-id");
		when(deviceRepositoryMock.findById(1L)).thenReturn(Mono.just(deviceEntity));

		TtnApiNotFoundException exception = new TtnApiNotFoundException(
				TestWebClientResponseException.createWithStatus(404));
		when(ttnServiceMock.getEndDevice("ttn-id")).thenReturn(Mono.error(exception));

		Mono<DeviceResponse> response = deviceService.getDevice(1L);

		StepVerifier.create(response)
				.expectNext(new DeviceResponse(1L, "name", new TtnSyncResponse(TtnSyncState.NO_MATCH, null)))
				.expectComplete()
				.verify();
	}

	@Test
	public void getDevice_returnsDeviceResponseWithTtnRequestError_forBadTtnResponses()
	{
		DeviceEntity deviceEntity = TestDeviceEntities.create(1L, "name", "ttn-id");
		when(deviceRepositoryMock.findById(1L)).thenReturn(Mono.just(deviceEntity));

		WebClientResponseException exception = TestWebClientResponseException.createWithStatus(400);
		when(ttnServiceMock.getEndDevice("ttn-id")).thenReturn(Mono.error(exception));

		Mono<DeviceResponse> response = deviceService.getDevice(1L);

		StepVerifier.create(response)
				.expectNext(new DeviceResponse(1L, "name", new TtnSyncResponse(TtnSyncState.TTN_REQUEST_ERROR, null)))
				.expectComplete()
				.verify();
	}

	@Test
	public void getDevice_returnsDeviceResponseWithTtnRequestError_forAnyExceptionDuringTtnAccess()
	{
		DeviceEntity deviceEntity = TestDeviceEntities.create(1L, "name", "ttn-id");
		when(deviceRepositoryMock.findById(1L)).thenReturn(Mono.just(deviceEntity));

		Exception exception = new RuntimeException("Any error");
		when(ttnServiceMock.getEndDevice("ttn-id")).thenReturn(Mono.error(exception));

		Mono<DeviceResponse> response = deviceService.getDevice(1L);

		StepVerifier.create(response)
				.expectNext(new DeviceResponse(1L, "name", new TtnSyncResponse(TtnSyncState.TTN_REQUEST_ERROR, null)))
				.expectComplete()
				.verify();
	}
}
