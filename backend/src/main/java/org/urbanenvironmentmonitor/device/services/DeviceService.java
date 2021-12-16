package org.urbanenvironmentmonitor.device.services;

import org.springframework.stereotype.Service;
import org.urbanenvironmentmonitor.device.controllers.dtos.UpdateDeviceRequest;
import org.urbanenvironmentmonitor.device.entities.Device;
import org.urbanenvironmentmonitor.device.repositories.DeviceRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DeviceService
{
	private final DeviceRepository deviceRepository;

	public DeviceService(DeviceRepository deviceRepository)
	{
		this.deviceRepository = deviceRepository;
	}

	public Mono<Device> createDevice(String name)
	{
		Device device = new Device();
		device.setName(name);

		return Mono.just(device)
				.flatMap(deviceRepository::save);
	}

	public Mono<Device> getDevice(long id)
	{
		return deviceRepository.findById(id);
	}

	public Flux<Device> getDevices()
	{
		return deviceRepository.findAll();
	}

	public Mono<Device> updateDevice(long id, UpdateDeviceRequest updateDeviceRequest)
	{
		return deviceRepository.findById(id)
				.doOnNext(device -> {
					device.setName(updateDeviceRequest.getName());
				})
				.flatMap(deviceRepository::save);
	}

	public Mono<Void> deleteDevice(long id)
	{
		return deviceRepository.findById(id)
				.flatMap(deviceRepository::delete);
	}
}
