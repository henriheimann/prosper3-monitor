package org.urbanenvironmentmonitor.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urbanenvironmentmonitor.device.dtos.*;
import org.urbanenvironmentmonitor.ttn.TtnService;
import org.urbanenvironmentmonitor.ttn.exception.TtnApiNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
public class DeviceService
{
	private final DeviceRepository deviceRepository;
	private final TtnService ttnService;

	public DeviceService(DeviceRepository deviceRepository, TtnService ttnService)
	{
		this.deviceRepository = deviceRepository;
		this.ttnService = ttnService;
	}

	@Transactional
	public Mono<DeviceResponse> createDevice(CreateDeviceRequest createDeviceRequest)
	{
		return deviceRepository.save(new DeviceEntity(createDeviceRequest.getName()))
				.zipWith(ttnService.createEndDevice()
						.map(Optional::of)
						.doOnError(e -> log.warn("Unable to create TTN end device", e))
						.onErrorReturn(Optional.empty()))
				.flatMap(tuple -> {
					var deviceEntity = tuple.getT1();
					var endDeviceOptional = tuple.getT2();

					endDeviceOptional.ifPresent(endDevice -> deviceEntity.setTtnId(endDevice.getIds().getDeviceId()));

					return Mono.zip(
							deviceRepository.save(deviceEntity),
							Mono.just(endDeviceOptional)
					);
				})
				.map(tuple -> {
					var deviceEntity = tuple.getT1();
					var endDeviceOptional = tuple.getT2();

					TtnSyncResponse ttnSyncResponse;
					if (endDeviceOptional.isEmpty()) {
						ttnSyncResponse = new TtnSyncResponse(TtnSyncState.NO_ASSOCIATION, null);
					} else {
						ttnSyncResponse = new TtnSyncResponse(TtnSyncState.VALID,
								new TtnDeviceResponse(endDeviceOptional.get().getCreatedAt()));
					}

					return new DeviceResponse(
							deviceEntity.getId(),
							deviceEntity.getName(),
							ttnSyncResponse
					);
				});
	}

	@Transactional
	public Mono<DeviceResponse> getDevice(long id)
	{
		return deviceRepository.findById(id)
				.zipWhen(device -> {
					if (device.getTtnId() != null) {
						return ttnService.getEndDevice(device.getTtnId())
								.map(endDevice -> new TtnSyncResponse(TtnSyncState.VALID,
										new TtnDeviceResponse(endDevice.getCreatedAt())))
								.onErrorReturn(TtnApiNotFoundException.class,
										new TtnSyncResponse(TtnSyncState.NO_MATCH, null))
								.onErrorReturn(new TtnSyncResponse(TtnSyncState.TTN_REQUEST_ERROR, null));
					} else {
						return Mono.just(new TtnSyncResponse(TtnSyncState.NO_ASSOCIATION, null));
					}
				})
				.map(tuple -> {
					var deviceEntity = tuple.getT1();
					var ttnSyncResponse = tuple.getT2();

					return new DeviceResponse(
							deviceEntity.getId(),
							deviceEntity.getName(),
							ttnSyncResponse
					);
				});
	}

	@Transactional
	public Flux<DeviceResponse> getDevices()
	{
		return deviceRepository.findAll()
				.map(deviceEntity -> new DeviceResponse(deviceEntity.getId(), deviceEntity.getName()));
	}

	@Transactional
	public Mono<DeviceResponse> updateDevice(long id, UpdateDeviceRequest updateDeviceRequest)
	{
		return deviceRepository.findById(id)
				.doOnNext(device -> {
					device.setName(updateDeviceRequest.getName());
				})
				.flatMap(deviceRepository::save)
				.map(deviceEntity -> new DeviceResponse(deviceEntity.getId(), deviceEntity.getName()));
	}

	@Transactional
	public Mono<Void> deleteDevice(long id)
	{
		return deviceRepository.findById(id)
				.flatMap(deviceRepository::delete);
	}
}
