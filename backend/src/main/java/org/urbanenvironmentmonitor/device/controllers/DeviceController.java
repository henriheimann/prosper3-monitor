package org.urbanenvironmentmonitor.device.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.urbanenvironmentmonitor.device.dtos.CreateDeviceRequest;
import org.urbanenvironmentmonitor.device.dtos.DeviceResponse;
import org.urbanenvironmentmonitor.device.dtos.UpdateDeviceRequest;
import org.urbanenvironmentmonitor.device.services.DeviceService;
import org.urbanenvironmentmonitor.live.LiveMeasurementService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/devices")
public class DeviceController
{
	private final DeviceService deviceService;
	private final LiveMeasurementService measurementService;

	public DeviceController(DeviceService deviceService, LiveMeasurementService measurementService)
	{
		this.deviceService = deviceService;
		this.measurementService = measurementService;
	}

	@PostMapping("")
	private Mono<DeviceResponse> createDevice(@RequestBody CreateDeviceRequest createDeviceRequest)
	{
		return deviceService.createDevice(createDeviceRequest.getName());
	}

	@GetMapping("")
	private Flux<DeviceResponse> getDevices()
	{
		return deviceService.getDevices();
	}

	@GetMapping("/{id}")
	private Mono<DeviceResponse> getDevice(@PathVariable long id)
	{
		return deviceService.getDevice(id);
	}

	@PutMapping("/{id}")
	private Mono<DeviceResponse> updateDevice(@PathVariable long id,
	                                          @RequestBody UpdateDeviceRequest updateDeviceRequest)
	{
		return deviceService.updateDevice(id, updateDeviceRequest);
	}

	@DeleteMapping("/{id}")
	private Mono<Void> deleteDevice(@PathVariable long id)
	{
		return deviceService.deleteDevice(id);
	}

	@GetMapping(value = "/{id}/live-measurements", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	private Flux<String> getDeviceLiveMeasurements(@PathVariable String id)
	{
		return measurementService.getMeasurements();
	}
}
